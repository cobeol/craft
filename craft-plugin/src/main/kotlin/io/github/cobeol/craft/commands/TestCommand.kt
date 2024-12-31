package io.github.cobeol.craft.commands

import io.github.cobeol.craft.invfx.InvFX
import io.github.cobeol.craft.invfx.openFrame
import io.github.cobeol.craft.plugin.CraftPlugin
import io.github.cobeol.craft.sample.SampleStatus
import io.github.cobeol.craft.status.StatusServer
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestCommand(private val plugin: CraftPlugin): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: return false
        val statusServer = StatusServer.create<SampleStatus>(plugin, SampleStatus::class.java)

        statusServer.addPlayer(player).let {
            val stats = statusServer.status[player.uniqueId]?.stats
            stats?.let { player.sendMessage(Component.text(it.test)) }
        }

        val invFrame = InvFX.frame(1, Component.text("SAMPLE_INVENTORY")) {
            onOpen { openEvent ->
                openEvent.player.sendMessage("You opened the inventory")
            }

            onClick { x, y, clickEvent ->
                player.sendMessage("x$x, y$y")
            }
        }
        player.openFrame(invFrame)

        return true
    }
}