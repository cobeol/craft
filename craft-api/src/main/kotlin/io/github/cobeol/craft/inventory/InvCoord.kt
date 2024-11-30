package io.github.cobeol.craft.inventory

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


/**
 * 아이템을 해당 좌표에 넣습니다.
 *
 * [x, y]: x -> 오른쪽 증가, y -> 아래쪽 증가
 *
 * @param coordinate 아이템을 넣을 위치(x, y)를 정합니다.
 */
fun Inventory.setItemCoord(coordinate: Array<Int>, item: ItemStack) {
    require(coordinate.size == 2) { "좌표는 무조건 2자리 길이여야 합니다." }

    val (x, y) = coordinate
    require(x in 0..8) { "(x)좌표는 음수거나 너비보다 클 수 없습니다." }

    val height = this.size / 9
    require(y in 0..(height - 1)) { "(y)좌표는 음수거나 높이보다 클 수 없습니다." }

    this.setItem(((y * 9) + x), item)
}

/**
 * 좌표로 해당 위치에 있는 아이템을 가져옵니다.
 *
 * [x, y]: x -> 오른쪽 증가, y -> 아래쪽 증가
 *
 * @param coordinate 아이템을 가져올 위치(x, y)를 정합니다.
 */
fun Inventory.getItemCoord(coordinate: Array<Int>): ItemStack? {
    require(coordinate.size == 2) { "좌표는 무조건 2자리 길이여야 합니다." }

    val (x, y) = coordinate
    require(x in 0..8) { "(x)좌표는 음수거나 너비보다 클 수 없습니다." }

    val height = this.size / 9
    require(y in 0..height) { "(y)좌표는 음수거나 높이보다 클 수 없습니다." }

    return this.getItem(x + (y * 9))
}