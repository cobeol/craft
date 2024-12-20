package io.github.cobeol.craft.event

import org.bukkit.event.Event
import java.util.concurrent.CopyOnWriteArrayList

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