package io.github.cobeol.craft.commands

import io.github.cobeol.craft.avatar.AvatarManager
import io.github.cobeol.craft.plugin.CraftPlugin
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player

class AvatarInventoryCommand(private val plugin: CraftPlugin): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("플레이어만 사용이 가능합니다.")
            return false
        }


        val player = sender.player!!
        val avatarManager = AvatarManager.create(plugin)

        val inventory = avatarManager.getInventory(player)
        if (inventory != null)
            if (args.isEmpty()) {
                player.openInventory(inventory)
                inventory.getItem(101)?.type?.let { player.sendMessage(it.name) }
            }
            else
                when (args[0]) {
                    "setInv" -> {
                        avatarManager.setAvatarInvToInv(player)
                        player.openInventory(inventory)
                    }
                    "setFakeInv" -> {
                        avatarManager.setInvToAvatarInv(player)
                        player.openInventory(inventory)
                    }
                }

        return true
    }
}