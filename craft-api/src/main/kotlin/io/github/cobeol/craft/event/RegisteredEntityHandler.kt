package io.github.cobeol.craft.event

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

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