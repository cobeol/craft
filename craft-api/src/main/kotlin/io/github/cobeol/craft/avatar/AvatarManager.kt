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

    fun getWrappedInv(player: Player): Inventory?

    /**
     * 아바타의 인벤토리를 인벤토리로 동기화합니다.
     */
    fun setPlayerInv(player: Player): Boolean

    /**
     * 플레이어의 인벤토리를 아바타의 인벤토리로 동기화합니다.
     */
    fun setInventory(player: Player): Boolean
}

interface AvatarInternal {
    fun create(plugin: JavaPlugin): AvatarManager
}