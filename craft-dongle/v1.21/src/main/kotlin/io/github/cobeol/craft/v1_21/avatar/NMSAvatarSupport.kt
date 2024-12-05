package io.github.cobeol.craft.v1_21.avatar

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import io.github.cobeol.craft.avatar.AvatarSupport
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.entity.EquipmentSlot
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftInventory
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.net.URI
import java.util.UUID
import com.mojang.datafixers.util.Pair

class NMSAvatarSupport: AvatarSupport {
    val fakePlayers: HashMap<String, ServerPlayer> = hashMapOf()

    fun getFake(name: String): ServerPlayer? {
        return fakePlayers.get(name)
    }

    override fun deleteFake(name: String): Boolean {
        val fakePlayer = getFake(name)
        if (fakePlayer == null)
            return false

        val removeEntitiesPacket = ClientboundRemoveEntitiesPacket(fakePlayer.id)
        fakePlayer.remove(Entity.RemovalReason.DISCARDED)

        Bukkit.getOnlinePlayers().forEach { _player ->
            (_player as CraftPlayer).handle.connection.send(removeEntitiesPacket)
        }

        fakePlayers.remove(name)

        return true
    }

    override fun getFakeInv(name: String): Inventory? {
        val fakePlayer = getFake(name)
        if (fakePlayer == null)
            return null

        return CraftInventory(fakePlayer.inventory)
    }

    // TODO 내일 핸들러 만들고 이케하고 저케하자
    override fun setFakeInv(name: String, inventory: Inventory): Boolean {
        val fakePlayer = getFake(name)
        if (fakePlayer == null)
            return false

        val inv = (inventory as CraftInventory).inventory
        val fakeInv = fakePlayer.inventory

        if (inventory.size != (9 * 6))
            return false

        for (i in (9 * 2) until inventory.size) {
            inv.getItem(i).let {
                fakeInv.setItem(i, it)
            }
        }

        fakeInv.setItem(8, inv.getItem(8).copy())
        fakeInv.setItem(EquipmentSlot.MAINHAND.ordinal, ItemStack.EMPTY)

        (3 until 7).forEach { i ->
            fakeInv.armor[i - 3] = inv.getItem(i).copy()
        }

        updateArmorData(fakePlayer)

        return true
    }

    fun updateArmorData(fakePlayer: ServerPlayer) {
        fakePlayer.also {
            Bukkit.getOnlinePlayers().forEach { player ->
                val equipmentList = listOf(
                    Pair(EquipmentSlot.HEAD, fakePlayer.inventory.armor[3]),
                    Pair(EquipmentSlot.CHEST, fakePlayer.inventory.armor[2]),
                    Pair(EquipmentSlot.LEGS, fakePlayer.inventory.armor[1]),
                    Pair(EquipmentSlot.FEET, fakePlayer.inventory.armor[0])
                )
                val equipmentPacket = ClientboundSetEquipmentPacket(fakePlayer.id, equipmentList)

                sendPacket(equipmentPacket, player)
            }
        }
    }

    override fun setFakeInv(player: Player): Boolean {
        val fakePlayer = getFake(player.name)
        if (fakePlayer == null)
            return false

        val inv = (player as CraftPlayer).handle.inventory
        val fakeInv = fakePlayer.inventory

        for (i in 0 until player.inventory.size) {
            inv.getItem(i).let {
                fakeInv.setItem(i, it)
            }
        }

        inv.getItem(EquipmentSlot.OFFHAND.ordinal).let {
            fakeInv.setItem(EquipmentSlot.OFFHAND.ordinal, it)
        }

        inv.getItem(EquipmentSlot.MAINHAND.ordinal).let {
            fakeInv.setItem(EquipmentSlot.MAINHAND.ordinal, it)
        }

        inv.armor.forEachIndexed { index, item ->
            fakeInv.armor[index] = item?.copy() ?: ItemStack.EMPTY
        }

        updateArmorData(fakePlayer)

        return true
    }

    override fun setInv(player: Player): Boolean {
        val fakePlayer = getFake(player.name)
        if (fakePlayer == null)
            return false

        val inv = (player as CraftPlayer).handle.inventory
        val fakeInv = fakePlayer.inventory

        for (i in 0 until player.inventory.size) {
            fakeInv.getItem(i).let {
                inv.setItem(i, it)
            }
        }

        fakeInv.armor.forEachIndexed { index, item ->
            inv.armor[index] = item?.copy() ?: ItemStack.EMPTY
        }

        fakeInv.getItem(EquipmentSlot.OFFHAND.ordinal).let {
            inv.setItem(EquipmentSlot.OFFHAND.ordinal, it)
        }

        fakeInv.getItem(EquipmentSlot.MAINHAND.ordinal).let {
            inv.setItem(EquipmentSlot.MAINHAND.ordinal, it)
        }

        updateArmorData(fakePlayer)

        return true
    }

    override fun createFake(player: Player, location: Location): Boolean {
        if (getFake(player.name) != null)
            return false

        val server: MinecraftServer = (Bukkit.getServer() as CraftServer).server
        val serverLevel: ServerLevel = (location.world as CraftWorld).handle

        // UUID, Name
        val profile = GameProfile(UUID.randomUUID(), "").apply {
            properties.removeAll("texture")
            properties.put("textrue", getTextrue(player.uniqueId))
        }

        val fakePlayer = ServerPlayer(server, serverLevel, profile, (player as CraftPlayer).handle.clientInformation()).apply {
            isInvisible = false
            setPos(location.x, location.y, location.z)
        }

        val entityData = fakePlayer.entityData.apply {
            set(EntityDataAccessor(17, EntityDataSerializers.BYTE), 127.toByte())
        }

        if (entityData.nonDefaultValues == null)
            return false

        fakePlayers[player.name] = fakePlayer
        sendFakePacket(player.name)

        return true
    }

    override fun sendFakePackets(): Boolean {
        fakePlayers.keys.forEach { name ->
            sendFakePacket(name)
        }

        return true
    }

    override fun sendFakePackets(player: Player): Boolean {
        fakePlayers.keys.forEach { name ->
            sendFakePacket(name, player)
        }

        return true
    }

    override fun sendFakePacket(name: String): Boolean {
        Bukkit.getOnlinePlayers().forEach { player ->
            sendFakePacket(name, player)
        }

        return true
    }

    override fun sendFakePacket(name: String, player: Player): Boolean {
        val fakePlayer = getFake(name)
        if (fakePlayer == null)
            return false

        fakePlayer.also {
            setConnection(fakePlayer, (player as CraftPlayer).handle.connection)

            val playerInfoUpdatePacket = ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, fakePlayer)
            val addEntityPacket = ClientboundAddEntityPacket(
                it.id,
                it.uuid,
                it.x,
                it.y,
                it.z,
                it.xRot,
                it.yRot,
                it.type,
                0,
                it.deltaMovement,
                0.0
            )
            val setEntityDataPacket = ClientboundSetEntityDataPacket(fakePlayer.id, fakePlayer.entityData.nonDefaultValues!!)

            sendPacket(playerInfoUpdatePacket, player)

            sendPacket(addEntityPacket, player)
            sendPacket(setEntityDataPacket, player)
        }

        return true
    }

    fun setConnection(serverPlayer: ServerPlayer, connection: ServerGamePacketListenerImpl) {
        try {
            ServerPlayer::class.java.getDeclaredField("c").apply {
                isAccessible = true
                set(serverPlayer, connection)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendPacket(packet: Packet<*>, player: Player) {
        (player as CraftPlayer).handle.connection.send(packet)
    }

    fun getTextrue(uniqueId: UUID): Property {
        val gson = Gson()

        val url = "https://sessionserver.mojang.com/session/minecraft/profile/$uniqueId?unsigned=false"
        return URI(url).toURL().readText().let {
            val data = gson.fromJson(it, JsonObject::class.java)
            val properties = data.getAsJsonArray("properties").get(0).asJsonObject

            val texture = properties.get("value").asString
            val signature = properties.get("signature").asString

             Property("textures", texture, signature)
        }
    }
}