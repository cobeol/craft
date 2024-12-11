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
    val avatars: HashMap<String, ServerPlayer> = hashMapOf()

    fun getAvatar(name: String): ServerPlayer? {
        return avatars.get(name)
    }

    override fun deleteAvatar(name: String): Boolean {
        val playerAvatar = getAvatar(name)
        if (playerAvatar == null)
            return false

        val removeEntitiesPacket = ClientboundRemoveEntitiesPacket(playerAvatar.id)
        playerAvatar.remove(Entity.RemovalReason.DISCARDED)

        Bukkit.getOnlinePlayers().forEach { _player ->
            (_player as CraftPlayer).handle.connection.send(removeEntitiesPacket)
        }

        avatars.remove(name)

        return true
    }

    override fun getAvatarInv(name: String): Inventory? {
        val playerAvatar = getAvatar(name)
        if (playerAvatar == null)
            return null

        return CraftInventory(playerAvatar.inventory)
    }

    // TODO 내일 핸들러 만들고 이케하고 저케하자
    override fun setAvatarInv(name: String, inventory: Inventory): Boolean {
        val playerAvatar = getAvatar(name)
        if (playerAvatar == null)
            return false

        val inv = (inventory as CraftInventory).inventory
        val avatarInv = playerAvatar.inventory

        if (inventory.size != (9 * 6))
            return false

        for (i in (9 * 2) until inventory.size) {
            inv.getItem(i).let {
                avatarInv.setItem(i, it)
            }
        }

        avatarInv.setItem(8, inv.getItem(8).copy())
        avatarInv.setItem(EquipmentSlot.MAINHAND.ordinal, ItemStack.EMPTY)

        (3 until 7).forEach { i ->
            avatarInv.armor[i - 3] = inv.getItem(i).copy()
        }

        updateArmorData(playerAvatar)

        return true
    }

    fun updateArmorData(playerAvatar: ServerPlayer) {
        playerAvatar.also {
            Bukkit.getOnlinePlayers().forEach { player ->
                val equipmentList = listOf(
                    Pair(EquipmentSlot.HEAD, playerAvatar.inventory.armor[3]),
                    Pair(EquipmentSlot.CHEST, playerAvatar.inventory.armor[2]),
                    Pair(EquipmentSlot.LEGS, playerAvatar.inventory.armor[1]),
                    Pair(EquipmentSlot.FEET, playerAvatar.inventory.armor[0])
                )
                val equipmentPacket = ClientboundSetEquipmentPacket(playerAvatar.id, equipmentList)

                sendPacket(equipmentPacket, player)
            }
        }
    }

    override fun setAvatarInv(player: Player): Boolean {
        val playerAvatar = getAvatar(player.name)
        if (playerAvatar == null)
            return false

        val inv = (player as CraftPlayer).handle.inventory
        val avatarInv = playerAvatar.inventory

        for (i in 0 until player.inventory.size) {
            inv.getItem(i).let {
                avatarInv.setItem(i, it)
            }
        }

        inv.getItem(EquipmentSlot.OFFHAND.ordinal).let {
            avatarInv.setItem(EquipmentSlot.OFFHAND.ordinal, it)
        }

        inv.getItem(EquipmentSlot.MAINHAND.ordinal).let {
            avatarInv.setItem(EquipmentSlot.MAINHAND.ordinal, it)
        }

        inv.armor.forEachIndexed { index, item ->
            avatarInv.armor[index] = item?.copy() ?: ItemStack.EMPTY
        }

        updateArmorData(playerAvatar)

        return true
    }

    override fun setInv(player: Player): Boolean {
        val playerAvatar = getAvatar(player.name)
        if (playerAvatar == null)
            return false

        val inv = (player as CraftPlayer).handle.inventory
        val avatarInv = playerAvatar.inventory

        for (i in 0 until player.inventory.size) {
            avatarInv.getItem(i).let {
                inv.setItem(i, it)
            }
        }

        avatarInv.armor.forEachIndexed { index, item ->
            inv.armor[index] = item?.copy() ?: ItemStack.EMPTY
        }

        avatarInv.getItem(EquipmentSlot.OFFHAND.ordinal).let {
            inv.setItem(EquipmentSlot.OFFHAND.ordinal, it)
        }

        avatarInv.getItem(EquipmentSlot.MAINHAND.ordinal).let {
            inv.setItem(EquipmentSlot.MAINHAND.ordinal, it)
        }

        updateArmorData(playerAvatar)

        return true
    }

    override fun createAvatar(player: Player, location: Location): Boolean {
        if (getAvatar(player.name) != null)
            return false

        val server: MinecraftServer = (Bukkit.getServer() as CraftServer).server
        val serverLevel: ServerLevel = (location.world as CraftWorld).handle

        // UUID, Name
        val profile = GameProfile(UUID.randomUUID(), "").apply {
            properties.removeAll("texture")
            properties.put("textrue", getTextrue(player.uniqueId))
        }

        val playerAvatar = ServerPlayer(server, serverLevel, profile, (player as CraftPlayer).handle.clientInformation()).apply {
            isInvisible = false
            setPos(location.x, location.y, location.z)
        }

        val entityData = playerAvatar.entityData.apply {
            set(EntityDataAccessor(17, EntityDataSerializers.BYTE), 127.toByte())
        }

        if (entityData.nonDefaultValues == null)
            return false

        avatars[player.name] = playerAvatar
        sendAvatarPacket(player.name)

        return true
    }

    override fun sendAvatarPackets(): Boolean {
        avatars.keys.forEach { name ->
            sendAvatarPacket(name)
        }

        return true
    }

    override fun sendAvatarPackets(player: Player): Boolean {
        avatars.keys.forEach { name ->
            sendAvatarPacket(name, player)
        }

        return true
    }

    override fun sendAvatarPacket(name: String): Boolean {
        Bukkit.getOnlinePlayers().forEach { player ->
            sendAvatarPacket(name, player)
        }

        return true
    }

    override fun sendAvatarPacket(name: String, player: Player): Boolean {
        val playerAvatar = getAvatar(name)
        if (playerAvatar == null)
            return false

        playerAvatar.also {
            setConnection(playerAvatar, (player as CraftPlayer).handle.connection)

            val playerInfoUpdatePacket = ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, playerAvatar)
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
            val setEntityDataPacket = ClientboundSetEntityDataPacket(playerAvatar.id, playerAvatar.entityData.nonDefaultValues!!)

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