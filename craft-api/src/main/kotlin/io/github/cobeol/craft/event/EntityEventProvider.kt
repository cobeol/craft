package io.github.cobeol.craft.event

import org.bukkit.event.Event
import kotlin.reflect.KClass

class EventEntityProvider(
    val event: KClass<out Event>,
    val provider: EntityProvider<*>
)