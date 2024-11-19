package io.github.cobeol.craft.plugin

import io.github.cobeol.craft.Sample
import org.bukkit.plugin.java.JavaPlugin

class CraftPlugin: JavaPlugin() {
    override fun onEnable() {
        Sample.printCoreMessage()
    }
}

interface Version {
    val value: String
}