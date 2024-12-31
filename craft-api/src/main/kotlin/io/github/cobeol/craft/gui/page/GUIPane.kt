package io.github.cobeol.craft.gui.page

/**
 * 좌표평면([GUIPane.width], [GUIPane.height])에서 [GUIWidget]을 해당 좌표평면 안에 집어넣는 기능입니다.
 */
@Suppress("unused")
internal class GUIPane(val width: Int, val height: Int) {
    private val grid = Array(height) { BooleanArray(width) }

    private var slotX = 0
    private var slotY = 0

    fun isPlaceable(widget: GUIWidget): Boolean {
        val width = widget.width + widget.leftMargin + widget.rightMargin
        val height = widget.height + widget.topMargin + widget.bottomMargin

        require(width <= this.width && height <= this.height) { "`GUIWidget`은 `GUIPage`의 높이 or 너비를 넘을 수 없습니다." }

        val placed = find(width, height)
        if (placed == null)
            return false

        fill(width, height, placed.first, placed.second)
        return true
    }

    private fun isPlaceable(width: Int, height: Int, x: Int, y: Int): Boolean {
        for (i in 0 until height) {
            for (j in 0 until width) {
                if (grid[y + i][x + j])
                    return false
            }
        }

        return true
    }

    private fun find(width: Int, height: Int): Pair<Int, Int>? {
        for (y in slotY until height) {
            val xStart = if (y == slotY) slotX else 0
            val xEnd = this.width - width + 1

            for (x in xStart until xEnd) {
                if (isPlaceable(width, height, x, y)) {
                    setSlot(x + width, y)
                    return x to y
                }
            }

            if (y + height > height)
                break
        }

        return null
    }

    private fun fill(height: Int, width: Int, x: Int, y: Int) {
        for (i in 0 until height) {
            grid[y + i].fill(true, x, x + width)
        }
    }

    private fun setSlot(x: Int, y: Int) {
        slotX = x % width
        slotY = y + x / width
    }

    fun clear() {
        for (row in grid) {
            row.fill(false)
        }

        slotX = 0
        slotY = 0
    }
}