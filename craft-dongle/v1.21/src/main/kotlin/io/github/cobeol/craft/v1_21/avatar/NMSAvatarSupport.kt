package io.github.cobeol.craft.v1_21.avatar

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.datafixers.util.Pair
import io.github.cobeol.craft.avatar.AvatarPacketType
import io.github.cobeol.craft.avatar.AvatarPacketType.*
import io.github.cobeol.craft.avatar.AvatarSupport
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.Pose
import net.minecraft.world.item.ItemStack
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftInventory
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.net.URI
import java.util.*

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

                val connection = (player as CraftPlayer).handle.connection
                connection.send(equipmentPacket)
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
            properties.put("textrue", getTexture(player.uniqueId))
        }

        val avatar = ServerPlayer(server, serverLevel, profile, (player as CraftPlayer).handle.clientInformation()).apply {
            customName = Component.literal(player.name)
            isCustomNameVisible = false

            setPos(location.x, location.y, location.z)
            setPose(Pose.SLEEPING)
        }

        val entityData = avatar.entityData.apply {
            set(EntityDataAccessor(17, EntityDataSerializers.BYTE), 127.toByte())
        }

        if (entityData.nonDefaultValues == null)
            return false

        if (avatar.customName == null)
            return false

        avatars[avatar.customName!!.string] = avatar
        sendPacket(ONE_JOIN_PACKET, avatar.customName!!.string)

        return true
    }

    override fun sendPacket(type: AvatarPacketType, avatar: String, players: List<Player>): Boolean {
        val avatar = getAvatar(avatar)
        if (avatar != null)
            if (type == ONE_JOIN_PACKET || type == ONE_QUIT_PACKET)
                return sendPacketInternal(type, listOf(avatar), players)

        return false
    }

    override fun sendPacket(type: AvatarPacketType, avatar: String, player: Player): Boolean {
        val avatar = getAvatar(avatar)
        if (avatar != null)
            if (type == ONE_JOIN_PACKET || type == ONE_QUIT_PACKET)
                return sendPacketInternal(type, listOf(avatar), listOf(player))

        return false
    }

    override fun sendPacket(type: AvatarPacketType, avatars: List<String>, players: List<Player>): Boolean {
        val avatars = avatars.map { getAvatar(it) }
        if (!avatars.any { it == null })
            if (type == SOME_JOIN_PACKET || type == SOME_QUIT_PACKET)
                return sendPacketInternal(type, avatars.map { it!! }, players)

        return false
    }

    override fun sendPacket(type: AvatarPacketType, avatars: List<String>, player: Player): Boolean {
        val avatars = avatars.map { getAvatar(it) }
        if (!avatars.any { it == null })
            if (type == SOME_JOIN_PACKET || type == SOME_QUIT_PACKET)
                return sendPacketInternal(type, avatars.map { it!! }, listOf(player))

        return false
    }

    override fun sendPacket(type: AvatarPacketType, players: List<Player>): Boolean {
        val avatars = avatars.values.map { it }
        if (type == ALL_JOIN_PACKET || type == ALL_QUIT_PACKET)
            return sendPacketInternal(type, avatars, players)

        return false
    }

    override fun sendPacket(type: AvatarPacketType, player: Player): Boolean {
        val avatars = avatars.values.map { it }
        if (type == AvatarPacketType.ALL_JOIN_PACKET || type == AvatarPacketType.ALL_QUIT_PACKET)
            return sendPacketInternal(type, avatars, listOf(player))

        return false
    }

    fun sendPacketInternal(type: AvatarPacketType, avatars: List<ServerPlayer>, players: List<Player>): Boolean {
        var players = players
        if (players.isEmpty())
            players = Bukkit.getOnlinePlayers() as MutableList<Player>

        avatars.forEach {
            if (it.customName != null) {

                this.avatars.remove(it.customName!!.string)

                players.forEach { player ->
                    val connection = (player as CraftPlayer).handle.connection
                    setConnection(it, connection)

                    fun sendJoinPacket() {
                        val playerInfoUpdatePacket = ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, it)
                        val addEntityPacket = ClientboundAddEntityPacket(
                            it.id, it.uuid,
                            it.x, it.y, it.z, it.xRot, it.yRot,
                            it.type, 0,
                            it.deltaMovement, 0.0
                        )

                        val setEntityDataPacket = ClientboundSetEntityDataPacket(it.id, it.entityData.nonDefaultValues!!)

                        connection.send(playerInfoUpdatePacket)

                        connection.send(addEntityPacket)
                        connection.send(setEntityDataPacket)
                    }

                    fun sendQuitPacket() {
                        val playerInfoRemovePacket = ClientboundPlayerInfoRemovePacket(listOf(it.uuid))
                        val removeEntityPacket = ClientboundRemoveEntitiesPacket(it.id)


                        connection.send(removeEntityPacket)
                        connection.send(playerInfoRemovePacket)

                        it.remove(Entity.RemovalReason.DISCARDED)
                    }

                    when (type) {
                        ONE_JOIN_PACKET -> sendJoinPacket()
                        SOME_JOIN_PACKET -> sendJoinPacket()
                        ALL_JOIN_PACKET -> sendJoinPacket()
                        ONE_QUIT_PACKET -> sendQuitPacket()
                        SOME_QUIT_PACKET -> sendQuitPacket()
                        ALL_QUIT_PACKET -> sendQuitPacket()
                    }
                }
            }
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

    fun getTexture(uniqueId: UUID): Property {
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