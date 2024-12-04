package io.github.cobeol.craft.avatar

import io.github.cobeol.craft.monun.loader.LibraryLoader
import org.bukkit.Location
import org.bukkit.entity.Player

interface AvatarSupport {
    fun createFakePlayer(player: Player, location: Location)

    fun deleteFakePlayer(name: String)
}

fun Player.createFake() {
    AvatarSupportNMS.createFakePlayer(this, location)
}

fun Player.deleteFake() {
    AvatarSupportNMS.deleteFakePlayer(name)
}

internal val AvatarSupportNMS = LibraryLoader.loadNMS(AvatarSupport::class.java)
