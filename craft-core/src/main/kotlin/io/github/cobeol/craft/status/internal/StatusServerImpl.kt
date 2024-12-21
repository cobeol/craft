package io.github.cobeol.craft.status.internal

import io.github.cobeol.craft.event.EntityEventManager
import io.github.cobeol.craft.util.getChildInstancesOfType
import io.github.cobeol.craft.status.Stat
import io.github.cobeol.craft.status.Status
import io.github.cobeol.craft.status.StatusServer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class StatusServerImpl<T: Status>(plugin: JavaPlugin, val clazz: Class<T>): StatusServer<T> {
    override val status: HashMap<UUID, T> = hashMapOf()
    private val manager: EntityEventManager = EntityEventManager(plugin)

    override fun addPlayer(player: Player) {
        clazz.getConstructor().newInstance().also { status[player.uniqueId] = it }
        registerStats(player)
    }

    override fun registerStats() {
        Bukkit.getOnlinePlayers().forEach { player ->
            registerStats(player)
        }
    }

    override fun unregisterStats() {
        Bukkit.getOnlinePlayers().forEach { player ->
            unregisterStats(player)
        }
    }

    override fun registerStats(player: Player) {
        val stats = getStats(player)
        stats.forEach { manager.registerEvents(player, it.event) }
    }

    override fun unregisterStats(player: Player) {
        val stats = getStats(player)
        stats.forEach { manager.unregisterEvents(player, it.event) }
    }

    fun getStats(player: Player): List<Stat> {
        val status = status[player.uniqueId]
       return status?.stats?.getChildInstancesOfType<Stat>() ?: listOf()
    }
}