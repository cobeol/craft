package io.github.cobeol.craft.avatar

import io.github.cobeol.craft.monun.data.PersistentDataKeychain
import io.github.cobeol.craft.protocol.AvatarPacket
import io.github.cobeol.craft.tooltip._text
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

interface AvatarSupport {
    fun createAvatar(player: Player): Boolean

    fun deleteAvatar(name: String): Boolean

    fun getAvatarInv(name: String): Inventory?

    fun getWrappedInv(name: String): Inventory?

    /**
     * 아바타의 인벤토리를 인벤토리로 동기화합니다.
     */
    fun setPlayerInv(player: Player): Boolean

    /**
     * [AvatarHolder]의 [Inventory]를 아바타 인벤토리로 동기화합니다.
     */
    fun setInventory(name: String, inventory: Inventory): Boolean

    /**
     * 인벤토리를 아바타의 인벤토리로 동기화합니다.
     */
    fun setInventory(player: Player): Boolean

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
     * [AvatarPacket.ONE_JOIN], [AvatarPacket.ONE_QUIT] 패킷을 전송하기 위한 함수입니다.
     */
    fun sendPacket(type: AvatarPacket, avatar: String, players: List<Player> = listOf()): Boolean
    /**
     * [AvatarPacket.ONE_JOIN], [AvatarPacket.ONE_QUIT] 패킷을 전송하기 위한 함수입니다.
     */
    fun sendPacket(type: AvatarPacket, avatar: String, player: Player): Boolean


    /**
     * [AvatarPacket.SOME_JOIN], [AvatarPacket.SOME_QUIT] 패킷을 전송하기 위한 함수입니다.
     */
    fun sendPacket(type: AvatarPacket, avatars: List<String>, players: List<Player> = listOf()): Boolean
    /**
     * [AvatarPacket.SOME_JOIN], [AvatarPacket.SOME_QUIT] 패킷을 전송하기 위한 함수입니다.
     */
    fun sendPacket(type: AvatarPacket, avatars: List<String>, player: Player): Boolean


    /**
     * [AvatarPacket.ALL_JOIN], [AvatarPacket.ALL_QUIT] 패킷을 전송하기 위한 함수입니다.
     */
    fun sendPacket(type: AvatarPacket, players: List<Player> = listOf()): Boolean
    /**
     * [AvatarPacket.ALL_JOIN], [AvatarPacket.ALL_QUIT] 패킷을 전송하기 위한 함수입니다.
     */
    fun sendPacket(type: AvatarPacket, player: Player): Boolean
}

object AvatarStatusKeys: PersistentDataKeychain() {
    val playerNameKey = primitive<String>("playerName")

    val healthKey = primitive<Int>("health")
}

class AvatarInvHolder: InventoryHolder {
    override fun getInventory(): Inventory = Bukkit.createInventory(this, (9 * 6), _text())
}