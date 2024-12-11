package io.github.cobeol.craft.avatar.internal

import io.github.cobeol.craft.avatar.avatar
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class AvatarEventListener(private val plugin: JavaPlugin): Listener {
    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        plugin.server.avatar.sendAvatarJoinPackets(event.player)
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        plugin.server.avatar.sendAvatarQuitPacket(event.player.name)
    }
}