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
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

@Suppress("unused")
class RegisteredEntityHandler(
    val listener: Listener,
    val priority: EventPriority,
    val provider: EventEntityProvider,
    private val executor: (Event) -> Unit
) : Comparable<RegisteredEntityHandler> {
    fun callEvent(event: Event) {
        executor(event)
    }

    override fun compareTo(other: RegisteredEntityHandler): Int {
        return priority.compareTo(other.priority)
    }
}