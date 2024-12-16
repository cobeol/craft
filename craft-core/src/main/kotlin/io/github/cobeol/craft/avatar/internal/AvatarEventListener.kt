package io.github.cobeol.craft.avatar.internal

import io.github.cobeol.craft.avatar.AvatarInvHolder
import io.github.cobeol.craft.avatar.AvatarPacketType
import io.github.cobeol.craft.avatar.AvatarStatusKeys
import io.github.cobeol.craft.avatar.avatar
import io.github.cobeol.craft.monun.data.persistentData
import org.bukkit.entity.Interaction
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class AvatarEventListener(private val plugin: JavaPlugin): Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        plugin.server.avatar.sendPacket(AvatarPacketType.ALL_JOIN_PACKET, event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        plugin.server.avatar.sendPacket(AvatarPacketType.ONE_QUIT_PACKET, event.player.name)
    }

    @EventHandler
    fun onPlayerInteractInteraction(event: PlayerInteractEntityEvent) {
        val interaction = event.rightClicked as? Interaction ?: return

        val player = event.player
        interaction.persistentData[AvatarStatusKeys.playerNameKey]?.let {
            val wrappedInv = plugin.server.avatar.getWrappedInv(it)
            if (wrappedInv != null)
                player.openInventory(wrappedInv)
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val holder = event.inventory.holder
        if (holder is AvatarInvHolder)
            plugin.server.avatar.setInventory(event.player.name, event.inventory)
    }
}