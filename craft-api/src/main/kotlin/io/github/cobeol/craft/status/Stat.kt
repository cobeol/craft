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

import org.bukkit.Material
import kotlin.math.floor
import kotlin.math.pow
import kotlin.random.Random

/**
 * [Status]에서 사용할, 능력치의 기초가 되는 클래스입니다.
 */
open class Stat {
    /**
     * [Stat]의 이름을 축약해서 보여주는 글자입니다
     *
     * ex) `Strength -> STR`
     */
    var symbol: String? = null
        protected set
        get() = (field ?: this.javaClass.simpleName).take(3).uppercase()

    /**
     * [Stat]을 상징하는 아이템의 종류를 정합니다
     */
    lateinit var icon: Material
        protected set

    var level: Int = 0
        set(value) {
            require(value >= 0) { "[level]은 음수일 수 없습니다." }
            if (isLevelLocked || value > maxExp)
                return

            field = value
        }

    var maxLevel: Int = 25
        set(value) {
            require(value >= 0) { "[maxLevel]은 음수일 수 없습니다." }

            field = value
            if (level == 0)
                level = calculateRandomLevel(value)
        }

    /**
     * [Stat.maxLevel]의 상승폭을 정하는 변수입니다. 최초 선언에만 사용하는 것을 추천합니다
     */
    var coefficient: Int = 4
        protected set

    var exp: Long = 0L
        set(value) {
            require(value >= 0L) { "[exp]는 음수일 수 없습니다." }
            if (isExpLocked)
                return

            field = value
            maxExp.let { maxExp ->
                if (value < maxExp)
                    return

                field -= maxExp
                if (level < maxLevel)
                    level += 1
            }
        }

    val maxExp: Long
        get() = calculateMaxExp(level, coefficient)

    /**
     * [Stat.level]의 값이 변하지 않도록 고정시킵니다
     */
    var isLevelLocked: Boolean = false
//        protected set

    /**
     * [Stat.exp]의 값이 변하지 않도록 고정시킵니다
     */
    var isExpLocked: Boolean = false
//        protected set

//    fun isLevelLocked(locked: Boolean) = { isLevelLocked = locked }

//    fun addLevel(level: Int) = { this.level += level }
//
//    fun setLevel(level: Int) = { this.level = level }

//    fun isExpLocked(locked: Boolean) = { isExpLocked = locked }

//    fun addExp(exp: Long) = { this.exp += exp }
//
//    fun setExp(exp: Long) = { this.exp = exp }

    lateinit var event: StatEventListener<out Stat>
}

fun Stat.calculateRandomLevel(maxLevel: Int): Int {
    require(maxLevel >= 0) { "[maxLevel]은 양수여야 합니다." }

    val random = Random.nextDouble()
    val weight = (1..maxLevel).sumOf { 1.0 / it.toDouble().pow(3) }

    var cumulativeProb = 0.0
    for (level in (1..maxLevel)) {
        cumulativeProb += (1.0 / level.toDouble().pow(3)) / weight
        if (random <= cumulativeProb)
            return level - 1
    }

    // 물론 여기까지 불가능 ㅎ
    return maxLevel
}

fun Stat.calculateMaxExp(level: Int, coefficient: Int = 1): Long {
    require(level >= 0) { "[level]은 음수일 수 없습니다." }
    return floor(((level + 2 * 50.0 / 49.0).pow(4.5) * coefficient)).toLong()
}