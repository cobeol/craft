package io.github.cobeol.craft.avatar.internal

import io.github.cobeol.craft.avatar.AvatarManager
import io.github.cobeol.craft.avatar.createFake
import io.github.cobeol.craft.avatar.deleteFake
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class AvatarManagerImpl(plugin: JavaPlugin): AvatarManager {
    override fun create(player: Player) {
        player.createFake()
    }

    override fun delete(player: Player) {
        player.deleteFake()
    }
}