package io.github.cobeol.craft.avatar

import io.github.cobeol.craft.monun.loader.LibraryLoader
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

interface AvatarSupport {
    fun createAvatar(player: Player, location: Location): Boolean

    fun deleteAvatar(name: String): Boolean

    fun getAvatarInv(name: String): Inventory?

    /**
     * [AvatarHolder]의 [Inventory]를 아바타 인벤토리로 동기화합니다.
     */
    fun setAvatarInv(name: String, inventory: Inventory): Boolean

    /**
     * 아바타의 인벤토리를 인벤토리로 동기화합니다
     */
    fun setInv(player: Player): Boolean

    /**
     * 인벤토리를 아바타의 인벤토리로 동기화합니다
     */
    fun setAvatarInv(player: Player): Boolean

    /**
     * 한 아바타의 패킷을 모든 플레이어에게 전송합니다.
     */
    fun sendAvatarPacket(name: String): Boolean

    /**
     * 한 아바타의 패킷을 하나의 플레이에게만 전송합니다.
     */
    fun sendAvatarPacket(name: String, player: Player): Boolean

    /**
     * 모든 아바타의 패킷을 모든 플레이어에게 전송합니다.
     */
    fun sendAvatarPackets(): Boolean

    /**
     * 모든 아바타의 패킷을 하나의 플레이에게만 전송합니다.
     */
    fun sendAvatarPackets(player: Player): Boolean
}

val Player.avatar: Avatar
    get() = Avatar(this)

class Avatar(private val player: Player) {
    val inventory = AvatarInv()

    fun create(): Boolean {
        return AvatarSupportNMS.createAvatar(player, player.location)
    }

    fun delete(): Boolean {
        return AvatarSupportNMS.deleteAvatar(player.name)
    }

    inner class AvatarInv {
        fun get(): Inventory? {
            return AvatarSupportNMS.getAvatarInv(player.name)
        }

        fun setInv(): Boolean {
            return AvatarSupportNMS.setInv(player)
        }

        fun setAvatarInv(): Boolean {
            return AvatarSupportNMS.setAvatarInv(player)
        }
    }
}

internal val AvatarSupportNMS = LibraryLoader.loadNMS(AvatarSupport::class.java)
