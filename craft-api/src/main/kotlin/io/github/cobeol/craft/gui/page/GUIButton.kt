package io.github.cobeol.craft.gui.page

import org.bukkit.inventory.ItemStack

/**
 * [GUIPage]에서 [GUIPage.headers]와 [GUIPage.footers]에만 들어가는, [GUIWidget]입니다.
 *
 * [GUIPage.bodies]엔 추천하지 않습니다.
 */
@Suppress("unused")
class GUIButton: GUIWidget() {
    /**
     * 위젯이 표시할 아이콘입니다.
     */
    val icon: ItemStack
        get() = _icon.clone()

    private var _icon: ItemStack = ItemStack.empty()

    override fun height(height: Int) { require(height == 1) { "`GUIButton`의 높이는 무조건 1이여야 합니다." } }

    override fun margin(margin: Int) { require(margin == 0) { "`GUIButton`의 여백은 무조건 0이여야 합니다." } }

    override fun topMargin(margin: Int) { require(margin == 0) { "`GUIButton`의 여백은 무조건 0이여야 합니다." } }

    override fun bottomMargin(margin: Int) { require(margin == 0) { "`GUIButton`의 여백은 무조건 0이여야 합니다." } }

    override fun leftMargin(margin: Int) { require(margin == 0) { "`GUIButton`의 여백은 무조건 0이여야 합니다." } }

    override fun rightMargin(margin: Int) { require(margin == 0) { "`GUIButton`의 여백은 무조건 0이여야 합니다." } }

    /**
     * 위젯이 표시할 아이콘을 정합니다.
     */
    fun icon(icon: ItemStack) { this._icon = icon.clone() }
}