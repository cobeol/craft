package io.github.cobeol.craft.plugin

import io.github.cobeol.craft.sample.SampleStatus
import org.bukkit.plugin.java.JavaPlugin
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class CraftPlugin: JavaPlugin() {
    companion object {
        var instance : CraftPlugin? = null
    }

    override fun onEnable() {
        instance = this
        CommandListener().registerCommands()
    }
}