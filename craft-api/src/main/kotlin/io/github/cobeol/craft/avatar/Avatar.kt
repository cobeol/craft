package io.github.cobeol.craft.avatar

import io.github.cobeol.craft.monun.loader.LibraryLoader
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

internal val AvatarSupportNMS = LibraryLoader.loadNMS(AvatarSupport::class.java)//by lazy { LibraryLoader.loadNMS(AvatarSupport::class.java) }

val Server.avatar: AvatarSupport
    get() = AvatarSupportNMS

val Player.avatar: Avatar
    get() = Avatar(this)

class Avatar(private val player: Player) {
    val inventory = AvatarInv()

    fun create(): Boolean {
        return AvatarSupportNMS.createAvatar(player)
    }

    fun delete(): Boolean {
        return AvatarSupportNMS.deleteAvatar(player.name)
    }

    inner class AvatarInv {
        fun get(): Inventory? {
            return AvatarSupportNMS.getAvatarInv(player.name)
        }

        fun getWrapped(): Inventory? {
            return AvatarSupportNMS.getWrappedInv(player.name)
        }

        /**
         * 아바타의 인벤토리를 인벤토리로 동기화합니다.
         */
        fun setPlayerInv(): Boolean {
            return AvatarSupportNMS.setPlayerInv(player)
        }

        /**
         * 플레이어의 인벤토리를 아바타의 인벤토리로 동기화합니다.
         */
        fun setInventory(): Boolean {
            return AvatarSupportNMS.setInventory(player)
        }
    }
}