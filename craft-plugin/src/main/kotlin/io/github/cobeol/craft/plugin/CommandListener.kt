package io.github.cobeol.craft.plugin

import io.github.cobeol.craft.commands.TestCommand

class CommandListener {
    fun registerCommands() {
        CraftPlugin.instance?.let {
            it.server.run {
                getPluginCommand("test")!!.setExecutor(TestCommand(it))
            }
        }
    }
}