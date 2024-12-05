package io.github.cobeol.craft.plugin

import io.github.cobeol.craft.commands.AvatarCreateCommand
import io.github.cobeol.craft.commands.AvatarDeleteCommand
import io.github.cobeol.craft.commands.AvatarInventoryCommand
import io.github.cobeol.craft.commands.TestCommand

class CommandListener {
    fun registerCommands() {
        CraftPlugin.instance?.let {
            it.server.run {
                getPluginCommand("test")!!.setExecutor(TestCommand(it))
                getPluginCommand("avatar_create")!!.setExecutor(AvatarCreateCommand(it))
                getPluginCommand("avatar_delete")!!.setExecutor(AvatarDeleteCommand(it))
                getPluginCommand("avatar_inventory")!!.setExecutor(AvatarInventoryCommand(it))
            }
        }
    }
}