package io.github.cobeol.craft.v1_21.avatar

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.datafixers.util.Pair
import io.github.cobeol.craft.avatar.AvatarInvHolder
import io.github.cobeol.craft.avatar.AvatarStatusKeys
import io.github.cobeol.craft.avatar.AvatarSupport
import io.github.cobeol.craft.inventory.setItemInSlot
import io.github.cobeol.craft.monun.data.persistentData
import io.github.cobeol.craft.protocol.AvatarPacket
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
import org.bukkit.Material
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftInventory
import org.bukkit.entity.Interaction
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.net.URI
import java.util.*
import kotlin.math.abs

class NMSAvatarSupport: AvatarSupport {
    val avatars: HashMap<String, ServerPlayer> = hashMapOf()

    override fun deleteAvatar(name: String): Boolean {
        val playerAvatar = avatars.get(name)
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
        val avatar = avatars.get(name)
        if (avatar == null)
            return null

        return CraftInventory(avatar.inventory)
    }

    override fun getWrappedInv(name: String): Inventory? {
        val avatar = avatars.get(name)
        if (avatar == null)
            return null

        val avatarInv = avatar.inventory
        val inv = AvatarInvHolder().inventory

        // 이건 그래픽 작업 전, 디버깅용?
        org.bukkit.inventory.ItemStack(Material.BARRIER).also { BARRIER ->
            inv.setItemInSlot(arrayOf(1, 0), BARRIER)
            inv.setItemInSlot(arrayOf(2, 0), BARRIER)

            avatarInv.armor.forEachIndexed { index, item ->
                inv.setItemInSlot(arrayOf(((3 + abs(4 - index)) - 1), 0), item.asBukkitCopy())
            }

            inv.setItemInSlot(arrayOf(7, 0), BARRIER)
            inv.setItemInSlot(arrayOf(8, 0), BARRIER)

            for (i in 0 until 9)
                inv.setItemInSlot(arrayOf(i, 1), BARRIER)

            avatarInv.items.forEachIndexed { index, item ->
                val x = (index % 9)
                val y = (index / 9).let { if (it == 0) 3 else (it - 1) }

                inv.setItemInSlot(arrayOf(x, (y + 2)), item.asBukkitCopy())
            }
        }

        return inv
    }

    fun updateArmorData(avatar: ServerPlayer) {
        avatar.also {
            Bukkit.getOnlinePlayers().forEach { player ->
                val equipmentList = listOf(
                    Pair(EquipmentSlot.HEAD, it.inventory.armor[3]),
                    Pair(EquipmentSlot.CHEST, it.inventory.armor[2]),
                    Pair(EquipmentSlot.LEGS, it.inventory.armor[1]),
                    Pair(EquipmentSlot.FEET, it.inventory.armor[0])
                )
                val equipmentPacket = ClientboundSetEquipmentPacket(it.id, equipmentList)

                val connection = (player as CraftPlayer).handle.connection
                connection.send(equipmentPacket)
            }
        }
    }

    override fun setPlayerInv(player: Player): Boolean {
        val avatar = avatars.get(player.name)
        if (avatar == null)
            return false

        val inv = (player as CraftPlayer).handle.inventory
        val avatarInv = avatar.inventory

        for (i in 0 until player.inventory.size) {
            inv.getItem(i).let {
                avatarInv.setItem(i, it)
            }
        }

        inv.getItem(EquipmentSlot.MAINHAND.ordinal).let {
            avatarInv.setItem(EquipmentSlot.MAINHAND.ordinal, it)
        }

        inv.armor.forEachIndexed { index, item ->
            avatarInv.armor[index] = item?.copy() ?: ItemStack.EMPTY
        }

        updateArmorData(avatar)

        return true
    }

    override fun setInventory(player: Player): Boolean {
        val avatar = avatars.get(player.name)
        if (avatar == null)
            return false

        val inv = (player as CraftPlayer).handle.inventory
        val avatarInv = avatar.inventory

        for (i in 0 until player.inventory.size) {
            avatarInv.getItem(i).let {
                inv.setItem(i, it)
            }
        }

        avatarInv.armor.forEachIndexed { index, item ->
            inv.armor[index] = item?.copy() ?: ItemStack.EMPTY
        }

        avatarInv.getItem(EquipmentSlot.MAINHAND.ordinal).let {
            inv.setItem(EquipmentSlot.MAINHAND.ordinal, it)
        }

        updateArmorData(avatar)

        return true
    }

    // 드디어 내일이가 왔다...!
    override fun setInventory(name: String, inventory: Inventory): Boolean {
        val avatar = avatars.get(name)
        if (avatar == null)
            return false

        val inv = (inventory as CraftInventory).inventory
        val avatarInv = avatar.inventory

        if (inventory.size != 9 * 6)
            return false

        for (i in 0 until 4) {
            avatarInv.armor[i] = inv.getItem(3 + abs(4 - i) - 1).copy()
        }

        for (index in 0 until (inventory.size - (9 * 2))) {
            val x = (index % 9)
            val y = (index / 9).let { if (it == 3) 0 else (it + 1) }

            inv.getItem(index + (9 * 2)).let {
                avatarInv.setItem((y * 9) + x, it)
            }
        }

        updateArmorData(avatar)

        return true
    }

    override fun createAvatar(player: Player): Boolean {
//        if (avatars.get(player.name) != null)
//            return false

        val location = player.location

        val server: MinecraftServer = (Bukkit.getServer() as CraftServer).server
        val serverLevel: ServerLevel = (location.world as CraftWorld).handle

        // UUID, Name
        val profile = GameProfile(UUID.randomUUID(), "").apply {
            properties.removeAll("texture")
            properties.put("textrue", getTexture(player.uniqueId))
        }


        // South, West, North
        val direction = listOf(140f, 0f, 220f).random()

        val avatar = ServerPlayer(server, serverLevel, profile, (player as CraftPlayer).handle.clientInformation()).apply {
            customName = Component.literal(player.name)
            isCustomNameVisible = false
            //collides = false, 밀림 방지인데 작동하지 않아 젠장..

            setPos(location.x, location.y, location.z)

            val bodyYaw = direction % 360f
            yHeadRot = if(bodyYaw >= 180f) bodyYaw else (bodyYaw + 180f + 90f)

            yRot = bodyYaw

            setPose(Pose.SLEEPING)
        }

        val entityData = avatar.entityData.apply {
            set(EntityDataAccessor(17, EntityDataSerializers.BYTE), 127.toByte())
        }

        if (entityData.nonDefaultValues == null)
            return false

        if (avatar.customName == null)
            return false

        // player.name = avatar.customName!!.string, 직관적인 구조를 위해 후자를 사용하였음. 가독성은 전자가 나을 수 있음.
        avatars[avatar.customName!!.string] = avatar
        sendPacket(AvatarPacket.ONE_JOIN, avatar.customName!!.string)

        val loc = location.clone().apply {
            when (direction) {
                140f -> z -= 0.3f
                0f -> x += 0.3f
                220f -> z += 0.3f
            }
        }

        for (i in 0 until 3) {
            location.world.spawn((loc.apply {
                when (direction) {
                    140f -> z += 0.6f
                    0f -> x -= 0.6f
                    220f -> z -= 0.6f
                }
            }), Interaction::class.java) {
                it.interactionWidth = 0.6f  // 기본 플레이어 너비: 0.6
                it.interactionHeight = 0.2f  // 기본 플레이어 높이: 1.8 근데 눕혔어용

                it.persistentData.let { data ->
                    data[AvatarStatusKeys.playerNameKey] = avatar.customName!!.string
                    data[AvatarStatusKeys.healthKey] = 20
                }
            }
        }

        return true
    }

    override fun sendPacket(type: AvatarPacket, avatar: String, players: List<Player>): Boolean {
        val avatar = avatars[avatar]
        if (avatar != null)
            if (type == AvatarPacket.ONE_JOIN || type == AvatarPacket.ONE_QUIT)
                return sendPacketInternal(type, listOf(avatar), players)

        return false
    }

    override fun sendPacket(type: AvatarPacket, avatar: String, player: Player): Boolean {
        return sendPacket(type, listOf(avatar), listOf(player))
    }

    override fun sendPacket(type: AvatarPacket, avatars: List<String>, players: List<Player>): Boolean {
        val avatars = avatars.map { this.avatars.get(it) }
        if (!avatars.any { it == null })
            if (type == AvatarPacket.SOME_JOIN || type == AvatarPacket.SOME_QUIT)
                return sendPacketInternal(type, avatars.map { it!! }, players)

        return false
    }

    override fun sendPacket(type: AvatarPacket, avatars: List<String>, player: Player): Boolean {
        return sendPacket(type, avatars, listOf(player))
    }

    override fun sendPacket(type: AvatarPacket, players: List<Player>): Boolean {
        if (type == AvatarPacket.ALL_JOIN || type == AvatarPacket.ALL_QUIT)
            return sendPacketInternal(type, avatars.values.map { it }, players)

        return false
    }

    override fun sendPacket(type: AvatarPacket, player: Player): Boolean {
        return sendPacket(type, listOf(player))
    }

    fun sendPacketInternal(type: AvatarPacket, avatars: List<ServerPlayer>, players: List<Player>): Boolean {
        var players = players
        if (players.isEmpty())
            players = Bukkit.getOnlinePlayers() as MutableList<Player>

        avatars.forEach {
            if (it.customName != null) {
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

                        val rotateHeadPacket = ClientboundRotateHeadPacket(it, (it.yHeadRot * 256 / 360).toInt().toByte())
                        connection.send(rotateHeadPacket)
                    }

                    fun sendQuitPacket() {
                        this.avatars.remove(it.customName!!.string)

                        val playerInfoRemovePacket = ClientboundPlayerInfoRemovePacket(listOf(it.uuid))
                        val removeEntityPacket = ClientboundRemoveEntitiesPacket(it.id)

                        connection.send(removeEntityPacket)
                        connection.send(playerInfoRemovePacket)

                        it.remove(Entity.RemovalReason.DISCARDED)
                    }

                    when (type) {
                        AvatarPacket.ONE_JOIN -> sendJoinPacket()
                        AvatarPacket.SOME_JOIN -> sendJoinPacket()
                        AvatarPacket.ALL_JOIN -> sendJoinPacket()
                        AvatarPacket.ONE_QUIT -> sendQuitPacket()
                        AvatarPacket.SOME_QUIT -> sendQuitPacket()
                        AvatarPacket.ALL_QUIT -> sendQuitPacket()
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