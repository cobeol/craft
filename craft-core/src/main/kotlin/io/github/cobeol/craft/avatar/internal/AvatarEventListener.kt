package io.github.cobeol.craft.avatar.internal

import io.github.cobeol.craft.avatar.AvatarPacketType
import io.github.cobeol.craft.avatar.avatar
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class AvatarEventListener(private val plugin: JavaPlugin): Listener {
    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        plugin.server.avatar.sendPacket(AvatarPacketType.ALL_JOIN_PACKET, event.player)
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        plugin.server.avatar.sendPacket(AvatarPacketType.ONE_QUIT_PACKET, event.player.name)
    }
}