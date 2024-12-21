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

package io.github.cobeol.craft.status

import org.bukkit.event.Listener

/**
 * [Stat] 증가/감소를 위한 이벤트리스너 입니다.
 *
 * ```
 * // Example
 * class StregnthEventListener(stat: StrengthStat): StatEventListener<StrengthStat>(stat) {
 *     @EventHandler
 *     fun onPlayerJoinEvent(event: PlayerJoinEvent) {
 *         stat.exp += 1
 *     }
 * }
 * ```
 *
 * @param stat 관리할 능력치
 */
open class StatEventListener<T: Stat>(stat: T): Listener