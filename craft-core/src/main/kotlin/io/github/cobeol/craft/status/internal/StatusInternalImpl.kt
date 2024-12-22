package io.github.cobeol.craft.status.internal

import io.github.cobeol.craft.status.Status
import io.github.cobeol.craft.status.StatusInternal
import io.github.cobeol.craft.status.StatusServer
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class StatusInternalImpl: StatusInternal {
    override fun <T: Status> create(plugin: JavaPlugin, clazz: Class<T>): StatusServer<T> {
        return StatusServerImpl<T>(plugin, clazz)
    }
}