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
    private lateinit var _inventory: Inventory

    private lateinit var title: String

    lateinit var header: GUIWidget
        protected set

    private val body: ArrayList<GUIWidget> = ArrayList()

    private val footers: ArrayList<GUIWidget> = ArrayList()

    private val headers: ArrayList<GUIWidget> = ArrayList()

    private var height: Int = 6

    val event: GUIPageEventListener<out GUIPage> = GUIPageEventListener(this)

    private var handler: GUIPageCustomHandler<out GUIPage>? = null

    private val interactions: HashMap<String, InventoryHolder?> = HashMap()

    fun title(str: String) { title = str }

    fun header(widget: GUIWidget) { header = widget }

    fun headers(widgets: ArrayList<GUIWidget>) = headers.addAll(widgets)

    fun body(widget: GUIWidget) = body.add(widget)

    fun footer(widget: GUIWidget) = footers.add(widget)

    fun footers(widgets: ArrayList<GUIWidget>) = footers.addAll(widgets)

    fun height(int: Int) { height = int }

    fun handler(customHandler: GUIPageCustomHandler<out GUIPage>) { handler = customHandler }

    fun build() {
        require(height <= 6) { "페이지의 높이는 6을 초과할 수 없습니다." }

        var lastX = 0
        var lastY = 0

        val width = 9
        var maxHeight = 0

        val guiF = Bukkit.createInventory(this, height * width, Component.text(title))

        val widgets = (headers + body + footers)
        for ((index, widget) in widgets.withIndex()) {
            if (widget == this.header) continue

            require((widget.padTop + widget.height + widget.padBottom) <= height) { "패딩(Top + Bottom) + 높이는 ${height}를 넘을 수 없습니다." }

            if (index == (widgets.size - footers.size) || index == widgets.size) {
                lastX = 0
                lastY += 1
            }

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
                    if (index < headers.size)
                        interactions[coordinate.joinToString("_")] = widget.holder
                    else if (index > ((headers + body).size - 1)) {
                        interactions[coordinate.joinToString("_")] = null
                    }

                    guiF.setItemCoord(coordinate, widget.icon)
                }
            }

            lastX += (widget.width + widget.padRight)
            lastY -= widget.padTop

            maxHeight = max((widget.padTop + widget.height + widget.padBottom), maxHeight)
        }

        _inventory = guiF
    }

    override fun onInventoryClick(slot: Int, player: Player) {
        val x = (slot % 9)
        val y = (slot / 9)

        val coordinate: Array<Int> = arrayOf(x, y)
        if (!interactions.containsKey(coordinate.joinToString("_")))
            return

        val holder: InventoryHolder? = interactions.getValue(coordinate.joinToString("_"))
        if (holder != null) {
            inventory.close()
            player.openInventory(holder.inventory)
        }
        else if (handler != null)
                handler!!.execute(slot, player)
    }

    override fun getInventory(): Inventory = _inventory
}

interface GUIfHolder: InventoryHolder {
    fun onInventoryClick(slot: Int, player: Player)
}

abstract class GUIPageCustomHandler<T: GUIPage>(page: T) {
    abstract fun execute(slot: Int, player: Player)
}