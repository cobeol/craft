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
 * 특정 슬롯의 이벤트를 관리할 수 있습니다.
 *
 * ```
 * // Example
 * frame(6, Component.text("Test InvFrame")) {
 *     slot (0, 0) {
 *         item = ItemStack(Material.STONE)
 *         onClick { clickEvent -> TODO() }
 *     }
 * }
 * ```
 */
@InvDSL
interface InvSlot {
    val x: Int
    val y: Int
    var item: ItemStack?

    /**
     * [InventoryClickEvent]:: 해당 좌표([x], [y])인 슬롯의 전용 클릭 이벤트
     */
    fun onClick(onClick: (InventoryClickEvent) -> Unit)
}