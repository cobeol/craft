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
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

@InvDSL
interface InvFrame: InvSpace {
    /**
     * 해당 좌표(x, y)인 슬롯에 이벤트를 등록합니다.
     *
     * ```
     * // Example
     * frame(6, Component.text("Test InvFrame")) {
     *     slot (0, 0) {
     *         item = ItemStack(Material.STONE)
     *         onClick { clickEvent ->
     *             // Execute
     *         }
     *     }
     * }
     * ```
     *
     * @param x (x) 좌표
     * @param y (y) 좌표
     * @param init 이벤트 핸들러
     */
    fun slot(x: Int, y: Int, init: InvSlot.() -> Unit): InvSlot

    /**
     * [InvFrame]//[InvSlot]들을 그룹으로 묶습니다.
     *
     * 이벤트 핸들러의 좌표는 모두 [InvFrame]을 기준으로 둡니다. ([InvPane]을 기준으로 0부터 시작하지 않습니다)
     *
     * ```
     * frame(6, Component.text("Test InvFrame")) {
     *     pane(0, 0, 4, 4) {
     *         item(0, 1, item)
     *         onClick { x, y, event ->
     *             event.player.sendMessage(`${x == 0 && y == 1}`)
     *         }
     *     }
     * }
     * ```
     *
     * @param minX 시작 (x) 좌표
     * @param minY 시작 (y) 좌표
     * @param maxX 너비
     * @param maxY 높이
     * @param init 이벤트 핸들러
     */
    fun pane(minX: Int, minY: Int, maxX: Int, maxY: Int, init: InvPane.() -> Unit): InvPane

    fun <T> list(
        minX: Int,
        minY: Int,
        maxX: Int,
        maxY: Int,
        trim: Boolean,
        item: () -> List<T>,
        init: (InvList<T>.() -> Unit)? = null
    ): InvList<T>

    /**
     * [InventoryOpenEvent];: [InvFrame] 처음 열리면 실행되는 이벤트
     *
     * ```
     * // Example
     * frame(6, Component.text("Test InvFrame")) {
     *     onOpen { openEvent ->
     *         // Execute
     *     }
     * }
     * ```
     */
    fun onOpen(onOpen: (InventoryOpenEvent) -> Unit)

    /**
     * [InventoryCloseEvent]:: [InvFrame] 종료 이벤트
     *
     * ```
     * // Example
     * frame(6, Component.text("Test InvFrame")) {
     *     onClose { closeEvent ->
     *         // Execute
     *     }
     * }
     * ```
     */
    fun onClose(onClose: (InventoryCloseEvent) -> Unit)

    /**
     * [InventoryClickEvent]:: [Player]//[Inventory] 클릭 이벤트
     *
     * ```
     * // Example
     * frame(6, Component.text("Test InvFrame")) {
     *     onClickBottom { clickBottomEvent ->
     *         // Execute
     *     }
     * }
     * ```
     */
    fun onClickBottom(onClickBottom: (InventoryClickEvent) -> Unit)

    /**
     * [InventoryCloseEvent]:: [InvFrame] 외부 클릭 이벤트
     *
     * ```
     * // Example
     * frame(6, Component.text("Test InvFrame")) {
     *     onClickOutside { clickOustideEvent ->
     *         // Execute
     *     }
     * }
     * ```
     */
    fun onClickOutside(onClickOutside: (InventoryClickEvent) -> Unit)
}