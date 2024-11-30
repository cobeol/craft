package io.github.cobeol.craft.plugin

import io.github.cobeol.craft.sample.SampleStatus
import org.bukkit.plugin.java.JavaPlugin
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class CraftPlugin: JavaPlugin() {
    val status = SampleStatus()

    companion object {
        var instance : CraftPlugin? = null
    }

    override fun onEnable() {
        instance = this
        status.registerEvent(this)
        status.gui.registerEvent(this)

        CommandListener().registerCommands()
        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            Bukkit.getOnlinePlayers().forEach { player ->
                val stat = status.stats.agility
                player.sendActionBar(Component.text("Level: ${stat.level}, Exp: ${stat.exp}, MaxExp: ${stat.maxExp}"))
            }
        }, 0L, 1L)
    }
}

interface Version {
    val value: String
}