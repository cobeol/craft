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

import io.github.cobeol.craft.util.LibraryLoader
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

/**
 * 여러 [Player]의 [Status]를 통합하여 관리하기 위한 인터페이스입니다.
 *
 * ```
 * // Example
 * // 사용하긴 위해서, 아래와 같이 선언해야 합니다.
 * val statusServer = StatusServer<Status>.create(plugin)
 * ```
 */
interface StatusServer<T: Status> {
    companion object : StatusInternal by LibraryLoader.loadImplement(StatusInternal::class.java)

    val status: HashMap<UUID, T>

    /**
     * 해당 [Player]의 [Status]를 추가합니다.
     *
     * @param player 대상
     */
    fun addPlayer(player: Player)

    /**
     * 모든 [Player]의 [Status]//[Stats] 이벤트들을 등록합니다.
     */
    fun registerStats()

    /**
     * 모든 [Player]의 [Status]//[Stats] 이벤트들을 제거합니다.
     */
    fun unregisterStats()

    /**
     * 특정 [Player]의 [Status]//[Stats] 이벤트들을 등록합니다.
     *
     * @param player 대상
     */
    fun registerStats(player: Player)

    /**
     * 특정 [Player]의 [Status]//[Stats] 이벤트들을 제거합니다.
     *
     * @param player 대상
     */
    fun unregisterStats(player: Player)
}

interface StatusInternal {
    fun <T: Status> create(plugin: JavaPlugin, clazz: Class<T>): StatusServer<T>
}