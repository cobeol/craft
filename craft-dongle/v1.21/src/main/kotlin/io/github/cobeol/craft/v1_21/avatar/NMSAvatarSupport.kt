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
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.entity.Entity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import java.net.URI
import java.util.UUID

class NMSAvatarSupport: AvatarSupport {
    val fakePlayers: HashMap<String, ServerPlayer> = hashMapOf()

    fun getFakePlayer(name: String): ServerPlayer? {
        return fakePlayers.get(name)
    }

    override fun deleteFakePlayer(name: String) {
        val fakePlayer = getFakePlayer(name)
        if (fakePlayer == null)
            return

        val removeEntitiesPacket = ClientboundRemoveEntitiesPacket(fakePlayer.id)
        fakePlayer.remove(Entity.RemovalReason.DISCARDED)

        Bukkit.getOnlinePlayers().forEach { _player ->
            (_player as CraftPlayer).handle.connection.send(removeEntitiesPacket)
        }
    }

    override fun createFakePlayer(player: Player, location: Location) {
        if (getFakePlayer(player.name) != null)
            return

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
            return

        fakePlayer.also {
            Bukkit.getOnlinePlayers().forEach { _player ->
                setConnection(fakePlayer, (_player as CraftPlayer).handle.connection)

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
                val setEntityDataPacket = ClientboundSetEntityDataPacket(fakePlayer.id, entityData.nonDefaultValues!!)

                sendPacket(playerInfoUpdatePacket, _player)
                sendPacket(addEntityPacket, _player)
                sendPacket(setEntityDataPacket, _player)
            }

            fakePlayers[player.name] = it
        }
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