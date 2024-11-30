package io.github.cobeol.craft.gui

import io.github.cobeol.craft.inventory.setItemCoord
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import kotlin.math.max

open class GUIPage: GUIfHolder {
    /**
     * [build]를 호출하여 초기화합니다.
     */
    lateinit var _inventory: Inventory
        protected set

    private lateinit var title: String

    lateinit var header: GUIWidget
        private set

    private val body: ArrayList<GUIWidget> = ArrayList()

    private val headers: ArrayList<GUIWidget> = ArrayList()

    private var height: Int = 6

    val guiEvent: GUIPageEventListener<in GUIPage> = GUIPageEventListener(this)

    private val interactions: HashMap<Array<Int>, InventoryHolder?> = HashMap()

    fun title(str: String) { title = str }

    fun header(widget: GUIWidget) { header = widget }

    fun body(widget: GUIWidget) = body.add(widget)

    fun headers(widgets: ArrayList<GUIWidget>) { headers.addAll(widgets) }

    fun height(int: Int) { height = int }

    fun build() {
        require(height <= 6) { "페이지의 높이는 6을 초과할 수 없습니다." }

        var lastX = 0
        var lastY = 0
        var maxHeight = 0

        val width = 9
        val guiF = Bukkit.createInventory(this, height * width, Component.text(title))
        for ((index, widget) in (headers + body).withIndex()) {
            if (widget == this.header) continue

            require((widget.padTop + widget.height + widget.padBottom) <= height) { "패딩(Top + Bottom) + 높이는 ${height}를 넘을 수 없습니다." }

            if (index == (headers + body).size)
                lastX = 0

            lastX += widget.padLeft
            if ((lastX + widget.width + widget.padRight) > 9) {
                lastX = widget.padLeft
                lastY += maxHeight
            }

            lastY += widget.padTop
            require((lastY + widget.height + widget.padBottom) <= height) { "위젯이 너비를 초과했습니다." }

            for (y in 0..(widget.height - 1)) {
                for (x in 0..(widget.width - 1)) {
                    val coordinate: Array<Int> = arrayOf(lastX + x, lastY + y)
                    if (widget.holder != null)
                        interactions[coordinate] = widget.holder

                    guiF.setItemCoord(coordinate, widget.icon)
                }
            }

            lastX += widget.width + widget.padRight
            lastY -= widget.padTop

            maxHeight = max((widget.padTop + widget.height + widget.padBottom), maxHeight)
        }

        _inventory = guiF
    }

    override fun onInventoryClick(slot: Int, player: Player) {
        val x = (slot % 9) - 1
        val y = (slot / 9) - 1

        val coordinate: Array<Int> = arrayOf(x, y)
        if (!interactions.containsKey(coordinate))
            return

        val holder: InventoryHolder? = interactions.getValue(coordinate)
        inventory.close()

        if (holder == null)
            TODO("여기는 뭐 커스텀 핸들러")
        else
            player.openInventory(holder.inventory)
    }

    override fun getInventory(): Inventory = _inventory
}

interface GUIfHolder: InventoryHolder {
    fun onInventoryClick(slot: Int, player: Player)
}