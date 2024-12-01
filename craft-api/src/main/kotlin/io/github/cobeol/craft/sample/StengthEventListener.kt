package io.github.cobeol.craft.sample

import io.github.cobeol.craft.monun.loader.LibraryLoader
import io.github.cobeol.craft.status.StatEventListener
import org.bukkit.attribute.Attribute
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import kotlin.math.max

interface BlockSupport {
    fun getHardness(block: Block): Float
}

internal val BlockSupportNMS = LibraryLoader.loadNMS(BlockSupport::class.java)

class StrengthEventListener(private val stat: StrengthStat): StatEventListener<StrengthStat>(stat) {
    @EventHandler
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        if (event.player.uniqueId != stat.uniqueId) return
        val hardness = BlockSupportNMS.getHardness(event.block)

        val _min = if (stat.level <= 5) 1L else 0L
        val _max = hardness.toLong() / 2L
        // 5렙 이하 -> 최소 경험치 1 else 0
        stat.addExp(max(_min, _max))
    }

    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        if (event.player.uniqueId != stat.uniqueId) return
        val hardness = BlockSupportNMS.getHardness(event.block)

        val _min = if (stat.level <= 10) 1L else 0L
        val _max = hardness.toLong() / 2L
        // 10렙 이하 -> 최소 경험치 1 else 0
        stat.addExp(max(_min, _max))
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

        val exp = (maxHealth * playerRatio * damageRatio).toLong()
        if (exp < 0L) return

        stat.addExp(exp)
    }
}