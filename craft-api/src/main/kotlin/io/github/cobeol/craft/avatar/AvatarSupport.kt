package io.github.cobeol.craft.avatar

import io.github.cobeol.craft.monun.data.PersistentDataKey
import io.github.cobeol.craft.monun.data.PersistentDataKeychain
import io.github.cobeol.craft.monun.loader.LibraryLoader
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

enum class AvatarPacketType {
    ONE_JOIN_PACKET,
    SOME_JOIN_PACKET,
    ALL_JOIN_PACKET,
    ONE_QUIT_PACKET,
    SOME_QUIT_PACKET,
    ALL_QUIT_PACKET,
}

interface AvatarSupport {
    fun createAvatar(player: Player): Boolean

    fun deleteAvatar(name: String): Boolean

    fun getAvatarInv(name: String): Inventory?

    /**
     * [AvatarHolder]의 [Inventory]를 아바타 인벤토리로 동기화합니다.
     */
    fun setAvatarInv(name: String, inventory: Inventory): Boolean

    /**
     * 아바타의 인벤토리를 인벤토리로 동기화합니다.
     */
    fun setInv(player: Player): Boolean

    /**
     * 인벤토리를 아바타의 인벤토리로 동기화합니다.
     */
    fun setAvatarInv(player: Player): Boolean

//    /**
//     * 한 아바타 생성 패킷을 모든 플레이어에게 전송합니다.
//     */
//    fun sendAvatarJoinPacket(name: String): Boolean
//
//    /**
//     * 한 아바타 생성 패킷을 하나의 플레이에게만 전송합니다.
//     */
//    fun sendAvatarJoinPacket(name: String, player: Player): Boolean
//
//    /**
//     * 모든 아바타 생성 패킷을 모든 플레이어에게 전송합니다.
//     */
//    fun sendAvatarJoinPackets(): Boolean
//
//    /**
//     * 모든 아바타 생성 패킷을 하나의 플레이에게만 전송합니다.
//     */
//    fun sendAvatarJoinPackets(player: Player): Boolean
//
//    /**
//     * 한 아파타 제거 패킷을 하나의 플레이어에게만 전송합니다.
//     */
//    fun sendAvatarQuitPacket(name: String, player: Player): Boolean
//
//    /**
//     * 한 아파타 제거 페킷을 모든 플레이어에게 전송합니다.
//     */
//    fun sendAvatarQuitPacket(name: String): Boolean

    /**
     * [AvatarPacketType.ONE_JOIN_PACKET], [AvatarPacketType.ONE_QUIT_PACKET] 패킷을 전송하기 위한 함수입니다.
     */
    fun sendPacket(type: AvatarPacketType, avatar: String, players: List<Player> = listOf()): Boolean
    /**
     * [AvatarPacketType.ONE_JOIN_PACKET], [AvatarPacketType.ONE_QUIT_PACKET] 패킷을 전송하기 위한 함수입니다.
     */
    fun sendPacket(type: AvatarPacketType, avatar: String, player: Player): Boolean


    /**
     * [AvatarPacketType.SOME_JOIN_PACKET], [AvatarPacketType.SOME_QUIT_PACKET] 패킷을 전송하기 위한 함수입니다.
     */
    fun sendPacket(type: AvatarPacketType, avatars: List<String>, players: List<Player> = listOf()): Boolean
    /**
     * [AvatarPacketType.SOME_JOIN_PACKET], [AvatarPacketType.SOME_QUIT_PACKET] 패킷을 전송하기 위한 함수입니다.
     */
    fun sendPacket(type: AvatarPacketType, avatars: List<String>, player: Player): Boolean


    /**
     * [AvatarPacketType.ALL_JOIN_PACKET], [AvatarPacketType.ALL_QUIT_PACKET] 패킷을 전송하기 위한 함수입니다.
     */
    fun sendPacket(type: AvatarPacketType, players: List<Player> = listOf()): Boolean
    /**
     * [AvatarPacketType.ALL_JOIN_PACKET], [AvatarPacketType.ALL_QUIT_PACKET] 패킷을 전송하기 위한 함수입니다.
     */
    fun sendPacket(type: AvatarPacketType, player: Player): Boolean
}

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

        /**
         * 아바타의 인벤토리를 인벤토리로 동기화합니다.
         */
        fun setInv(): Boolean {
            return AvatarSupportNMS.setInv(player)
        }

        /**
         * 플레이어의 인벤토리를 아바타의 인벤토리로 동기화합니다.
         */
        fun setAvatarInv(): Boolean {
            return AvatarSupportNMS.setAvatarInv(player)
        }
    }
}

object AvatarStatusKeys: PersistentDataKeychain() {
    val playerNameKey = primitive<String>("playerName")

    val healthKey = primitive<Int>("health")
}