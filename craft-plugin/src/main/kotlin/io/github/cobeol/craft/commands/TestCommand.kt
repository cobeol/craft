package io.github.cobeol.craft.commands

import io.github.cobeol.craft.gui.GUIPage
import io.github.cobeol.craft.gui.GUIWidget
import io.github.cobeol.craft.inventory.setItemCoord
import io.github.cobeol.craft.plugin.CraftPlugin
import io.github.cobeol.craft.sample.SampleStatBuilder
import io.github.cobeol.craft.sample.SampleStatPage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.space
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.advancement.Advancement
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

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