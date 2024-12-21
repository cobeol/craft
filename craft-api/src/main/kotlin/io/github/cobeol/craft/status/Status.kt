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