package io.github.cobeol.craft.status

import io.github.cobeol.craft.gui.GUIBuilder
import io.github.cobeol.craft.loader.getChildInstancesOfType
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

interface Status {
    val stats: Stats
    val gui: GUIBuilder

    fun registerEvent(plugin: JavaPlugin) {
        val stats = this.stats.getChildInstancesOfType<Stat>()
        stats.forEach { stat -> Bukkit.getPluginManager().registerEvents(stat.expEvent, plugin) }
    }
}

interface Stats