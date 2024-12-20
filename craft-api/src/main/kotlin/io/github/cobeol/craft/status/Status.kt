package io.github.cobeol.craft.status

interface Status {
    val stats: Stats
}

/**
 * 여러 [Stat]을 담기 위한 인터페이스입니다.
 *
 * ```
 * // Example
 * class SimpleStat: Stats {
 *     val strength: StrengthStat
 *     val agility: AgilityStat
 * }
 * ```
 */
interface Stats