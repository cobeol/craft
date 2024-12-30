/*
 * Copyright (C) 2024 Cobeol
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * For the full license text, see the LICENSE file in the project root.
 */

package io.github.cobeol.craft.event

import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityEvent
import org.bukkit.plugin.Plugin
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * 특정 [Entity]의 전용 이벤트를 등록하고, 관리하기 위한 기능입니다.
 */
@Suppress("unused")
class EntityEventManager(private val plugin: Plugin) {
    private val handlers = ConcurrentHashMap<KClass<out Event>, EntityHandlerList>()
    private val providers = ConcurrentHashMap<KClass<out EntityProvider<*>>, EntityProvider<*>>()
    private val listeners = ConcurrentHashMap<Entity, MutableSet<Listener>>()

    private val _providers = ConcurrentHashMap<Entity, EntityProvider<Event>>()

    /**
     * 해당 [Entity]의 전용 이벤트를 등록합니다.
     *
     * @param entity 대상
     * @param listener 이벤트리스너
     */
    @Suppress("UNCHECKED_CAST")
    fun registerEvents(entity: Entity, listener: Listener) {
        listener::class.members
            .asSequence()
            .filter { it.hasAnnotation<EventHandler>() }
            .forEach { member ->
                val event = member.parameters[1].type.classifier as KClass<out Event>
                val handler = member.findAnnotation<EventHandler>()!!
                val target = member.findAnnotation<TargetEntity>()

                val provider = getOrCreateProvider(target, entity)

                val registeredHandler = RegisteredEntityHandler(
                    listener,
                    handler.priority,
                    EventEntityProvider(event, provider)
                ) { event -> member.call(listener, event) }

                handlers.getOrPut(event) { EntityHandlerList() }.register(registeredHandler)

                registerEvent(event, listener, handler)
            }

        // Maybe thread-safe?
        listeners.getOrPut(entity) { ConcurrentHashMap.newKeySet() }.add(listener)
    }

    private fun getOrCreateProvider(targetEntity: TargetEntity?, entity: Entity): EntityProvider<*> {
        return if (targetEntity != null)
            providers.getOrPut(targetEntity.value) {
                targetEntity.value.objectInstance ?: targetEntity.value.java.getDeclaredConstructor().newInstance()
            }
        else
            _providers.getOrPut(entity) {
                object : EntityProvider<Event> {
                    override fun getTarget(event: Event) = when (event) {
                        is EntityEvent -> if (event.entity == entity) event.entity else null
                        else -> null
                    }
                }
            }
    }

    private fun registerEvent(event: KClass<out Event>, listener: Listener, handler: EventHandler) {
        plugin.server.pluginManager.registerEvent(
            event.java,
            listener,
            handler.priority,
            { _, event -> callEvent(event) },
            plugin,
            handler.ignoreCancelled
        )
    }

    /**
     * 해당 [Entity]의 전용 이벤트를 제거합니다.
     *
     * @param entity 대상
     * @param listener 이벤트리스너
     */
    fun unregisterEvents(entity: Entity, listener: Listener) {
        listeners[entity]?.remove(listener)

        handlers.values.forEach { handlers ->
            handlers.unregister { it.listener == listener && isTargetEntity(it, entity) }
        }

        if (listeners[entity]?.isEmpty() != true)
            return

        listeners.remove(entity)
        _providers.remove(entity)
    }

    @Suppress("UNCHECKED_CAST")
    private fun isTargetEntity(handler: RegisteredEntityHandler, entity: Entity): Boolean {
        return (handler.provider.provider as? EntityProvider<Event>)?.getTarget(
            handler.provider.event.java.getDeclaredConstructor().newInstance()
        ) == entity
    }

    /**
     * 해당 [Entity]의 모든 전용 이벤트를 제거합니다.
     *
     * @param entity 대상
     */
    fun unregisterAllEvents(entity: Entity) {
        listeners[entity]?.forEach { unregisterEvents(entity, it) }
        listeners.remove(entity)

        _providers.remove(entity)
    }

    /**
     * 해당 [Listener]를 모든 [Entity] 이벤트에서 제거합니다.
     *
     * @param listener 이벤트리스너
     */
    fun unregisterListener(listener: Listener) {
        handlers.values.forEach { it.unregister { handler -> handler.listener == listener } }
        listeners.values.forEach { it.remove(listener) }
    }

    fun callEvent(event: Event) {
        handlers[event::class]?.callEvent(event)
    }
}