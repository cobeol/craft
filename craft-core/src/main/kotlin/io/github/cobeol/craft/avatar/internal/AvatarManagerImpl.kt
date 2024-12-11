package io.github.cobeol.craft.avatar.internal

import io.github.cobeol.craft.avatar.AvatarManager
import io.github.cobeol.craft.avatar.avatar
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

class AvatarManagerImpl(plugin: JavaPlugin): AvatarManager {
    init {
        Bukkit.getPluginManager().registerEvents(AvatarEventListener(plugin), plugin)
    }

    override fun create(player: Player) {
        player.avatar.create()
    }

    override fun delete(player: Player) {
        player.avatar.delete()
    }

    override fun getInventory(player: Player): Inventory? {
        return player.avatar.inventory.get()
    }

    override fun setAvatarInvToInv(player: Player): Boolean {
        return player.avatar.inventory.setInv()
    }

    override fun setInvToAvatarInv(player: Player): Boolean {
        return player.avatar.inventory.setAvatarInv()
    }
}