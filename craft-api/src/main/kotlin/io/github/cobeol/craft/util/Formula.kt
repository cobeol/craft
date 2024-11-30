package io.github.cobeol.craft.util

import kotlin.math.floor
import kotlin.math.pow

class Formula {
    companion object {
        /**
         * 레벨 계산 공식
         */
        fun calculateMaxExp(level: Int, coefficient: Int = 1): Long {
            return floor(((level + 2 * 50.0 / 49.0).pow(4.5) * coefficient)).toLong()
        }
    }
}