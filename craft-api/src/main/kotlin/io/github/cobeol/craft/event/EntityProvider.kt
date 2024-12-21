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
import kotlin.reflect.KClass

/**
 * [TargetEntity]의 커스텀 Provider를 정의할 수 있는 기능입니다.
 *
 * ```
 * object DefaultProvider {
 *     object EntityDamageByEntity {
 *         object Damager : EntityProvider<EntityDamageByEntityEvent> {
 *             override fun getTarget(event: EntityDamageByEntityEvent) = event.damager
 *         }
 *         object Damagee : EntityProvider<EntityDamageByEntityEvent> {
 *             override fun getTarget(event: EntityDamageByEntityEvent) = event.entity
 *         }
 *     }
 * }
 * ```
 *
 * @param T 이벤트 종류
 */
interface EntityProvider<T : Event> {
    /**
     * 이벤트에서 반환할 엔티티입니다.
     */
    fun getTarget(event: T): Entity?
}

/**
 * [Event]에서 주체자와 대상으로 나뉠 때, 대상을 확실하게 하기 위해 필요합니다.
 *
 * [TargetEntity] 사용하면 [EntityEventManager]를 이용하여 이벤트를 등록해야합니다.
 *
 * ```
 * // Example
 *
 * // [EntityDamageByEntityEvent], [BlockPlaceEvent]와 같은 이벤트들
 * @EventHandler
 * @TargetEntity(DefaultProvider.EntityDamageByEntity.Damagee::class)
 * fun onEntityDamaged(event: EntityDamageByEntityEvent) {
 *     println("${event.entity.name}가 공격받았습니다.")
 * }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TargetEntity(val value: KClass<out EntityProvider<*>>)