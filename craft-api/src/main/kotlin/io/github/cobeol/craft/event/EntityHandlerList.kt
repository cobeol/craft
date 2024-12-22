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

import org.bukkit.event.Event
import java.util.concurrent.CopyOnWriteArrayList

@Suppress("unused")
class EntityHandlerList {
    private val handlers = CopyOnWriteArrayList<RegisteredEntityHandler>()

    fun register(handler: RegisteredEntityHandler) {
        handlers.add(handler)
        handlers.sortBy { it.priority }
    }

    fun unregister(predicate: (RegisteredEntityHandler) -> Boolean) {
        handlers.removeAll { predicate(it) }
    }

    @Suppress("UNCHECKED_CAST")
    fun callEvent(event: Event) {
        for (handler in handlers) {
            if (handler.provider.event.isInstance(event)) {
                val provider = handler.provider.provider as EntityProvider<Event>
                val entity = provider.getTarget(event)
                if (entity != null) {
                    handler.callEvent(event)
                }
            }
        }
    }

    fun clear() {
        handlers.clear()
    }
}