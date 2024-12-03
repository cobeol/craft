package io.github.cobeol.craft.sample

import io.github.cobeol.craft.status.StatEventListener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.math.ceil
import kotlin.math.round

class AgilityEventListener(private val stat: AgilityStat): StatEventListener<AgilityStat>(stat) {
    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return
        if (player.uniqueId != stat.uniqueId)
            return

        if (event.cause != EntityDamageEvent.DamageCause.FALL)
            return

        val fallDistance = player.fallDistance
        val rawDamage = ceil(fallDistance - 3).toInt()

        stat.addExp((rawDamage * 6).toLong())
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        if (player.uniqueId != stat.uniqueId)
            return

        val from = event.from
        val to = event.to

        if (from.x == to.x && from.z == to.z)
            return

        stat.addExp(round(player.velocity.length() * (3/2)).toLong())
    }
}