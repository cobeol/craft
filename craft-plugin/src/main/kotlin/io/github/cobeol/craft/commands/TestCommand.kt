package io.github.cobeol.craft.commands

import io.github.cobeol.craft.plugin.CraftPlugin
import io.github.cobeol.craft.sample.SampleStatBuilder
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player

class TestCommand(private val plugin: CraftPlugin): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("플레이어만 사용이 가능합니다.")
            return false
        }

        val player = sender.player!!

        val statBuilder = SampleStatBuilder(plugin.status.stats).apply {
            build(height = 3)
            registerEvent(plugin)
        }

        player.openInventory(statBuilder.page.inventory)

        return true
    }
}