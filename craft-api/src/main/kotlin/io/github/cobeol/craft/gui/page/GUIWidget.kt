package io.github.cobeol.craft.gui.page

import org.bukkit.inventory.Inventory
import org.bukkit.event.inventory.InventoryClickEvent

@Suppress("unused")
open class GUIWidget {
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
         *     onClick { clickEvent ->
         *         // Execute
         *     }
         * }
         * ```
         */
        fun create(init: GUIWidget.() -> Unit): GUIWidget {
            return GUIWidget().apply(init)
        }
    }

    /**
     * [GUIWidget]이 차지할 너비를 나타냅니다.
     *
     * [Inventory]에 들어가기 때문에, 9을 넘을 수 없습니다.
     */
    var width: Int = 1
        protected set(value) {
            require(isValidWidth(value, leftMargin, rightMargin)) { "{width\$$value + margin(\$$leftMargin + \$$rightMargin)}(은)는 9를 넘을 수 없습니다." }
            field = value
        }

    /**
     * [GUIWidget]이 차지할 높이를 나타냅니다.
     *
     * [Inventory]에 들어가기 때문에, 6을 넘을 수 없습니다.
     */
    var height: Int = 1
        protected set(value) {
            require(isValidHeight(value, topMargin, bottomMargin)) { "{height\$$value + margin(\$$topMargin + \$$bottomMargin)}(은)는 6을 넘을 수 없습니다." }
            field = value
        }

    /**
     * [GUIWidget]의 전체 마진입니다.
     *
     * [Inventory]에 들어가기에, (여백 + $높이 or $너비)가 [Inventory] 크기 제한을 넘을 수 없습니다.
     */
    var margin: MutableList<Int>
        get() = _margin
        protected set(value) {
            require(value.size == 4 && isValid(width, height, value)) { "올바르지 않은 마진입니다." }
            _margin = value
        }

    // .toMutableList:: copy
    private var _margin: MutableList<Int> = MutableList(4) { 0 }
        get() = field.toMutableList()
        set(value) {
            field = value.toMutableList()
        }

    /**
     * [GUIWidget]의 위쪽 마진입니다.
     */
    var topMargin: Int
        get() = _margin[0]
        protected set(value) {
            require(isValidHeight(height, value, bottomMargin)) { "{height\$$width + margin(\$$value + \$$bottomMargin)}(은)는 6을 넘을 수 없습니다." }
            _margin[0] = value
        }

    /**
     * [GUIWidget]의 아래쪽 마진입니다.
     */
    var bottomMargin: Int
        get() = _margin[1]
        protected set(value) {
            require(isValidHeight(height, topMargin, value)) { "{height\$$width + margin(\$$topMargin + \$$value)}(은)는 6을 넘을 수 없습니다." }
            _margin[1] = value
        }

    /**
     * [GUIWidget]의 왼쪽 마진입니다.
     */
    var leftMargin: Int
        get() = _margin[2]
        protected set(value) {
            require(isValidWidth(width, value, rightMargin)) { "{width\$$width + margin(\$$leftMargin + \$$value)}(은)는 9를 넘을 수 없습니다." }
            _margin[2] = value
        }

    /**
     * [GUIWidget]의 오른쪽 마진입니다.
     */
    var rightMargin: Int
        get() = _margin[3]
        protected set(value) {
            require(isValidWidth(width, leftMargin, value)) { "{width\$$width + margin(\$$value + \$$rightMargin)}(은)는 9를 넘을 수 없습니다." }
            _margin[3] = value
        }

    /**
     * [GUIWidget]을 클릭했을 때, 발생하는 이벤트입니다.
     */
    lateinit var onClick: (InventoryClickEvent) -> Unit
        protected set

    /**
     * [GUIWidget.width]를 정의합니다.
     *
     * @param width 너비
     */
    fun width(width: Int) { this.width = width }

    /**
     * [GUIWidget.height]를 정의합니다.
     *
     * @param height 높이
     */
    open fun height(height: Int) { this.height = height }

    /**
     *  [GUIWidget.margin]을 정의합니다.
     *
     * @param margin 여백
     */
    open fun margin(margin: Int) { this.margin = MutableList(4) { margin } }

    /**
     * [GUIWidget.topMargin]을 정의합니다.
     *
     * @param margin 여백
     */
    open fun topMargin(margin: Int) { this.topMargin = margin }

    /**
     * [GUIWidget.bottomMargin]을 정의합니다.
     *
     * @param margin 여백
     */
    open fun bottomMargin(margin: Int) { this.bottomMargin = margin }

    /**
     * [GUIWidget.leftMargin]을 정의합니다.
     *
     * @param margin 여백
     */
    open fun leftMargin(margin: Int) { this.leftMargin = margin }

    /**
     * [GUIWidget.rightMargin]을 정의합니다.
     *
     * @param margin 여백
     */
    open fun rightMargin(margin: Int) { this.rightMargin = margin }

    fun onClick(onClick: (InventoryClickEvent) -> Unit) { this.onClick = onClick }

    private fun isValidHeight(height: Int, topMargin: Int, bottomMargin: Int): Boolean {
        return (height + topMargin + bottomMargin) <= 6
    }

    private fun isValidHeight(height: Int, margin: MutableList<Int>): Boolean {
        require(margin.size == 4) { "올바르지 않은 마진입니다." }
        return (height + margin[2] + margin[3]) <= 6
    }

    private fun isValidWidth(width: Int, leftMargin: Int, rightMargin: Int): Boolean {
        return (width + leftMargin + rightMargin) <= 9
    }

    private fun isValidWidth(width: Int, margin: MutableList<Int>): Boolean {
        require(margin.size == 4) { "올바르지 않은 마진입니다." }
        return (width + margin[0] + margin[1]) <= 9
    }

    private fun isValid(width: Int, height: Int, margin: MutableList<Int>): Boolean {
        require(margin.size == 4) { "올바르지 않은 마진입니다." }
        return isValidWidth(width, margin) && isValidHeight(height, margin)
    }
}