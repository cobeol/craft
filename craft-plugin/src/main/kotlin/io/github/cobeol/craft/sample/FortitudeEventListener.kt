package io.github.cobeol.craft.sample

import io.github.cobeol.craft.status.StatEventListener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import kotlin.math.round

class FortitudeEventListener(private val stat: FortitudeStat): StatEventListener<FortitudeStat>(stat) {
    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return
        if (player.uniqueId != stat.uniqueId)
            return

        var damage = event.damage
        stat.addExp((damage * 10).toLong())
    }

    @EventHandler
    fun onPlayerDamageByEntity(event: EntityDamageByEntityEvent) {
        val player = event.entity as? Player ?: return
        if (player.uniqueId != stat.uniqueId)
            return

        val entity = event.damager
        var playerRatio = if(entity is Player) 0.2 else 0.5

        val damage = event.damage
        stat.addExp(round(damage * playerRatio * 10).toLong())
    }
}