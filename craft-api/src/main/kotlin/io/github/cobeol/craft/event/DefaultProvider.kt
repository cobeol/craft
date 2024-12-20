package io.github.cobeol.craft.event

import org.bukkit.event.entity.EntityDamageByEntityEvent

object DefaultProvider {
    object EntityDamageByEntity {
        object Damager : EntityProvider<EntityDamageByEntityEvent> {
            override fun getTarget(event: EntityDamageByEntityEvent) = event.damager
        }
        object Damagee : EntityProvider<EntityDamageByEntityEvent> {
            override fun getTarget(event: EntityDamageByEntityEvent) = event.entity
        }
    }
}