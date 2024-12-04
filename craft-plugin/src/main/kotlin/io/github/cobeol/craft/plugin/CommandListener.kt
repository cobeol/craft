package io.github.cobeol.craft.plugin

import io.github.cobeol.craft.commands.AvatarCommand
import io.github.cobeol.craft.commands.AvatarDeleteCommand
import io.github.cobeol.craft.commands.TestCommand

class CommandListener {
    fun registerCommands() {
        CraftPlugin.instance?.let {
            it.server.run {
                getPluginCommand("test")!!.setExecutor(TestCommand(it))
                getPluginCommand("avatar")!!.setExecutor(AvatarCommand(it))
                getPluginCommand("avatar_delete")!!.setExecutor(AvatarDeleteCommand(it))
            }
        }
    }
}