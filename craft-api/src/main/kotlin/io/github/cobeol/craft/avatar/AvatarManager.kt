package io.github.cobeol.craft.avatar

import io.github.cobeol.craft.monun.loader.LibraryLoader
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin

interface AvatarManager {
    companion object : AvatarInternal by LibraryLoader.loadImplement(AvatarInternal::class.java)

    fun create(player: Player)

    fun delete(player: Player)

    fun getInventory(player: Player): Inventory?

    fun setInvToFakeInv(player: Player): Boolean

    fun setFakeInvToInv(player: Player): Boolean
}

interface AvatarInternal {
    fun create(plugin: JavaPlugin): AvatarManager
}