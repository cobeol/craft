package io.github.cobeol.craft.avatar

import io.github.cobeol.craft.monun.loader.LibraryLoader
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

interface AvatarSupport {
    fun createFake(player: Player, location: Location): Boolean

    fun deleteFake(name: String): Boolean

    fun getFakeInv(name: String): Inventory?

    /**
     * [AvatarHolder]의 [Inventory]를 아바타 인벤토리로 동기화합니다.
     */
    fun setFakeInv(name: String, inventory: Inventory): Boolean

    /**
     * 아바타의 인벤토리를 인벤토리로 동기화합니다
     */
    fun setInv(player: Player): Boolean

    /**
     * 인벤토리를 아바타의 인벤토리로 동기화합니다
     */
    fun setFakeInv(player: Player): Boolean

    /**
     * 한 아바타의 패킷을 모든 플레이어에게 전송합니다.
     */
    fun sendFakePacket(name: String): Boolean

    /**
     * 한 아바타의 패킷을 하나의 플레이에게만 전송합니다.
     */
    fun sendFakePacket(name: String, player: Player): Boolean

    /**
     * 모든 아바타의 패킷을 모든 플레이어에게 전송합니다.
     */
    fun sendFakePackets(): Boolean

    /**
     * 모든 아바타의 패킷을 하나의 플레이에게만 전송합니다.
     */
    fun sendFakePackets(player: Player): Boolean
}

val Player.fake: Fake
    get() = Fake(this)

class Fake(private val player: Player) {
    val inventory = FakeInv()

    fun create(): Boolean {
        return AvatarSupportNMS.createFake(player, player.location)
    }

    fun delete(): Boolean {
        return AvatarSupportNMS.deleteFake(player.name)
    }

    inner class FakeInv {
        fun get(): Inventory? {
            return AvatarSupportNMS.getFakeInv(player.name)
        }

        fun setInv(): Boolean {
            return AvatarSupportNMS.setInv(player)
        }

        fun setFakeInv(): Boolean {
            return AvatarSupportNMS.setFakeInv(player)
        }
    }
}

internal val AvatarSupportNMS = LibraryLoader.loadNMS(AvatarSupport::class.java)
