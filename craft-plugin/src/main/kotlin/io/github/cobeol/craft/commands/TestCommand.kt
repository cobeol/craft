package io.github.cobeol.craft.commands

import io.github.cobeol.craft.plugin.CraftPlugin
import io.github.cobeol.craft.sample.SampleStatus
import io.github.cobeol.craft.status.StatusServer
import net.kyori.adventure.text.Component
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player

class TestCommand(private val plugin: CraftPlugin): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: return false
        val statusServer = StatusServer.create<SampleStatus>(plugin, SampleStatus::class.java)

        statusServer.addPlayer(player).let {
            val stats = statusServer.status[player.uniqueId]?.stats
            stats?.let { player.sendMessage(Component.text(it.test)) }
        }

//        val eventManager = EntityEventManager(plugin)
//        eventManager.registerEvents(player, object : Listener {
//            @EventHandler
//            @TargetEntity(DefaultProvider.EntityDamageByEntity.Damager::class)
//            fun onEntityDamage(event: EntityDamageByEntityEvent) {
//                println("${event.damager.name}가 공격했습니다.")
//            }
//
//            @EventHandler
//            @TargetEntity(DefaultProvider.EntityDamageByEntity.Damagee::class)
//            fun onEntityDamaged(event: EntityDamageByEntityEvent) {
//                println("${event.entity.name}가 공격받았습니다.")
//            }
//        }

        return true
    }
}