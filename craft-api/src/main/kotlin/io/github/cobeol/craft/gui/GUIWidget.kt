package io.github.cobeol.craft.gui

import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

open class GUIWidget(
    val width: Int,
    val height: Int,
    val icon: ItemStack,
    val holder: InventoryHolder? = null,
    val handler: GUIWidgetHandler<out GUIPage>? = null,
    padding: Int = 0,
    padTop: Int = -1,
    padBottom: Int = -1,
    padLeft: Int = -1,
    padRight: Int = -1,
) {
    var padTop = if (padTop == -1) padding else padTop
        private set(value) {
            require(value > 0) { "패딩은 양수여야 합니다." }
            field = value
        }

    var padBottom = if (padBottom == -1) padding else padBottom
        private set(value) {
            require(value > 0) { "패딩은 양수여야 합니다." }
            field = value
        }

    var padLeft = if (padLeft == -1) padding else padLeft
        private set(value) {
            require(value > 0) { "패딩은 양수여야 합니다." }
            require((value + padRight + width) <= 9) { "패딩(Left + Right) + 너비는 9를 넘을 수 없습니다." }
            field = value
        }

    var padRight = if (padRight == -1) padding else padRight
        private set(value) {
            require(value > 0) { "패딩은 양수여야 합니다." }
            require((value + padRight + width) <= 9) { "패딩(Left + Right) + 너비는 9를 넘을 수 없습니다." }
            field = value
        }

    fun padding(value: Int) {
        padTop = value
        padBottom = value
        padLeft = value
        padRight = value
    }
}

abstract class GUIWidgetHandler<T: GUIPage>(page: T) {
    abstract fun execute(slot: Int, player: Player)
}