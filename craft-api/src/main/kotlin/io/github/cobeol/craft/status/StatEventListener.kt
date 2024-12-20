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