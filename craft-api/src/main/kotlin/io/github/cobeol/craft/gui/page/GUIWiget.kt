package io.github.cobeol.craft.gui.page

import org.bukkit.inventory.Inventory
import org.bukkit.event.inventory.InventoryClickEvent

@Suppress("unused")
class GUIWidget {
    companion object {
        /**
         * 직관적인 [GUIWidget] 생성을 위한 함수입니다.
         *
         * widget을 생성하고 apply를 적용하는 방식과 동일합니다.
         *
         * ```
         * // Example
         * val widget = GUIWidget.create {
         *     width(3)
         *     margin(2)
         *     onClick { clickEvent -> TODO() }
         * }
         * ```
         */
        fun create(init: GUIWidget.() -> Unit): GUIWidget {
            return GUIWidget().apply(init)
        }
    }

    /**
     * 위젯이 차지할 높이를 나타냅니다.
     *
     * [Inventory]에 들어가기 때문에, 6을 넘을 수 없습니다.
     */
    var height: Int = 1
        private set

    /**
     * 위젯이 차지할 너비를 나타냅니다.
     *
     * [Inventory]에 들어가기 때문에, 9을 넘을 수 없습니다.
     */
    var width: Int = 1
        private set

    /**
     * 위젯 사이의 여백을 정합니다.
     *
     * [Inventory]에 들어가기에, (여백 + $높이/$너비)가 [Inventory] 크기 제한을 넘을 수 없습니다.
     */
    val margin: MutableList<Int>
        get() = _margin.toMutableList()

    private var _margin: MutableList<Int> = MutableList(4) { 0 }

    /**
     * 여백의 윗부분을 정합니다.
     */
    var topMargin: Int
        get() = _margin[0]
        set(value) { _margin[0] = value }

    /**
     * 여백의 아랫부분을 정합니다.
     */
    var bottomMargin: Int
        get() = _margin[1]
        set(value) { _margin[1] = value }

    /**
     * 여백의 왼쪽 부분을 정합니다.
     */
    var leftMargin: Int
        get() = _margin[2]
        set(value) {
            require(isValidWidth(width, value, rightMargin)) { "(width\$$width + margin(\$$leftMargin) + \$$margin)(은)는 9를 넘을 수 없습니다." }
            _margin[2] = value
        }

    /**
     * 여백의 오른쪽 부분을 정합니다.
     */
    var rightMargin: Int
        get() = _margin[3]
        set(value) {
            require(isValidWidth(width, leftMargin, value)) { "(width\$$width + margin(\$$leftMargin) + \$$margin)(은)는 9를 넘을 수 없습니다." }
            _margin[3] = value
        }

    /**
     * 위젯을 클릭했을 때, 발생하는 이벤트를 정합니다.
     */
    lateinit var onClick: (InventoryClickEvent) -> Unit
        private set

    /**
     * 위젯의 높이를 정의합니다.
     *
     * @param height 높이
     */
    fun height(height: Int) { this.height = height }

    /**
     * 위젯의 너비를 정의합니다.
     *
     * @param width 너비
     */
    fun width(width: Int) { this.width = width }

    /**
     * 위젯의 전체 여백을 정의합니다.
     *
     * @param margin 여백
     */
    fun margin(margin: Int) { this._margin = MutableList(4) { margin } }

    /**
     * 위젯의 위쪽 여백을 정의합니다.
     *
     * @param margin 여백
     */
    fun topMargin(margin: Int) { this.topMargin = margin }

    /**
     * 위젯의 아래쪽 여백을 정의합니다.
     *
     * @param margin 여백
     */
    fun bottomMargin(margin: Int) { this.bottomMargin = margin }

    /**
     * 위젯의 왼쪽 여백을 정의합니다.
     *
     * @param margin 여백
     */
    fun leftMargin(margin: Int) { this.leftMargin = margin }

    /**
     * 위젯의 오른쪽 여백을 정의합니다.
     *
     * @param margin 여백
     */
    fun rightMargin(margin: Int) { this.rightMargin = margin }

    fun onClick(onClick: (InventoryClickEvent) -> Unit) { this.onClick = onClick }

    private fun isValidHeight(height: Int, topMargin: Int, bottomMargin: Int): Boolean {
        return (height + topMargin + bottomMargin) <= 6
    }

    private fun isValidHeight(height: Int, margin: MutableList<Int>): Boolean {
        require(margin.size == 4) { "올바르지 않은 여백입니다." }
        return (height + margin[2] + margin[3]) <= 6
    }

    private fun isValidWidth(width: Int, leftMargin: Int, rightMargin: Int): Boolean {
        return (width + leftMargin + rightMargin) <= 9
    }

    private fun isValidWidth(width: Int, margin: MutableList<Int>): Boolean {
        require(margin.size == 4) { "올바르지 않은 여백입니다." }
        return (width + margin[0] + margin[1]) <= 9
    }

    private fun isValid(height: Int, width: Int, margin: MutableList<Int>): Boolean {
        return isValidHeight(height, margin) && isValidWidth(width, margin)
    }
}