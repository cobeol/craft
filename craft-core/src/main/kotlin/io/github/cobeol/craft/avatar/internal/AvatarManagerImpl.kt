package io.github.cobeol.craft.avatar.internal

import io.github.cobeol.craft.avatar.AvatarManager
import io.github.cobeol.craft.avatar.fake
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

class AvatarManagerImpl(plugin: JavaPlugin): AvatarManager {
    override fun create(player: Player) {
        player.fake.create()
    }

    override fun delete(player: Player) {
        player.fake.delete()
    }

    override fun getInventory(player: Player): Inventory? {
        return player.fake.inventory.get()
    }

    override fun setFakeInvToInv(player: Player): Boolean {
        return player.fake.inventory.setInv()
    }

    override fun setInvToFakeInv(player: Player): Boolean {
        return player.fake.inventory.setFakeInv()
    }
}