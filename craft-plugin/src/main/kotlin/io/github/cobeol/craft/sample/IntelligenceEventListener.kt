package io.github.cobeol.craft.sample

import io.github.cobeol.craft.status.StatEventListener
import io.papermc.paper.advancement.AdvancementDisplay
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import kotlin.math.round

class IntelligenceEventListener(private val stat: IntelligenceStat): StatEventListener<IntelligenceStat>(stat) {
    @EventHandler
    fun onAdvancementDone(event: PlayerAdvancementDoneEvent) {
        val player = event.player
        if (player.uniqueId != stat.uniqueId)
            return

        val display = event.advancement.display
        if (display == null)
            return

        when (display.frame()) {
            AdvancementDisplay.Frame.TASK ->
                stat.addExp(1000L)

            AdvancementDisplay.Frame.GOAL ->
                stat.addLevel(round(stat.level.toDouble() * 1/4).toInt())

            AdvancementDisplay.Frame.CHALLENGE ->
                stat.addLevel(round(stat.level.toDouble() * 1/2).toInt())
        }
    }

    @EventHandler
    fun onEntityDamageByPlayerEvent(event: EntityDamageByEntityEvent) {
        val player = event.damager as? Player ?: return
        if (player.uniqueId != stat.uniqueId) return

        val entity = event.entity as? LivingEntity ?: return

        val damage = event.damage
        val maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: return

        var playerRatio = if(entity is Player) 0.2 else 0.5
        val damageRatio = ((maxHealth / (maxHealth - damage)) + damage * 5) / 10

        val exp = round(maxHealth * playerRatio * damageRatio * 1/10).toLong()
        if (exp < 0L) return

        stat.addExp(exp)
    }
}