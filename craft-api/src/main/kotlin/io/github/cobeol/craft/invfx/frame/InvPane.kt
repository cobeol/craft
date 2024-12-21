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

/**
 * [InvFrame]//[InvSlot]들을 그룹으로 묶습니다.
 *
 * 이벤트 핸들러들의 좌표는 모두 [InvFrame]을 기준으로 둡니다. ([InvPane]을 기준으로 시작하지 않습니다)
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
 */
@InvDSL
interface InvPane: InvRegion, InvSpace