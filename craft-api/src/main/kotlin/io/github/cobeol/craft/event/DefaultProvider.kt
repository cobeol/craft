/*
 * Copyright (C) 2024 Cobeol
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * For the full license text, see the LICENSE file in the project root.
 */

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