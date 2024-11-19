package io.github.cobeol.craft.v1_21_1.internal

import io.github.cobeol.craft.internal.Version
import net.minecraft.server.MinecraftServer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer

class NMSVersion: Version {
    override val value: String
        get() {
            val server = (Bukkit.getServer() as CraftServer).server
            return server.javaClass.canonicalName + " - " + server.serverVersion + " " + MinecraftServer::class.java.fields[0].name
        }
}