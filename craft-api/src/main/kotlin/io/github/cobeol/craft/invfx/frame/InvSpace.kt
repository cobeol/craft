/*
 * InvFX
 * Copyright (C) 2021 Monun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.cobeol.craft.invfx.frame

import io.github.cobeol.craft.invfx.InvDSL
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * [InvFrame]를 특정 좌표(x, y)로 감싸 슬롯을 관리합니다.
 */
@InvDSL
interface InvSpace {
    /**
     * [InventoryClickEvent]:: [InventoryClickEvent.rawSlot]을 좌표(x, y)로 감싼 이벤트
     *
     * ```
     * // Example
     * frame(6, Component.text("Test InvFrame")) {
     *     onClick { x, y, clickEvent -> TODO() }
     * }
     * ```
     */
    fun onClick(onClick: (x: Int, y: Int, event: InventoryClickEvent) -> Unit)

    /**
     * 특정 슬롯의 아이템을 가져옵니다.
     *
     * @param x (x) 좌표
     * @param y (y) 좌표
     */
    fun item(x: Int, y: Int): ItemStack?

    /**
     * 특정 슬롯에 아이템을 등록합니다.
     *
     * ```
     * // Example
     * frame(6, Component.text("Test InvFrame")) {
     *     item(4, 4, ItemStack(Material.STONE))
     * }
     * ```
     *
     * @param x (x) 좌표
     * @param y (y) 좌표
     * @param item 등록할 아이템
     */
    fun item(x: Int, y: Int, item: ItemStack?)
}