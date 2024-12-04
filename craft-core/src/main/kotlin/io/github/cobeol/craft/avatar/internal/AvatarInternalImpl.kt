package io.github.cobeol.craft.avatar.internal

import io.github.cobeol.craft.avatar.AvatarInternal
import io.github.cobeol.craft.avatar.AvatarManager
import org.bukkit.plugin.java.JavaPlugin

class AvatarInternalImpl: AvatarInternal {
    override fun create(plugin: JavaPlugin): AvatarManager {
        return AvatarManagerImpl(plugin)
    }
}