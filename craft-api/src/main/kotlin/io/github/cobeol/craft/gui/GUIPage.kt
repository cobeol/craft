package io.github.cobeol.craft.gui

import io.github.cobeol.craft.inventory.setItemInSlot
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
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

    private var title: String = ""
        set(value) {
            field = "\uE000${value}"
        }

    lateinit var header: GUIWidget
        protected set

    private val body: ArrayList<GUIWidget> = ArrayList()

    private val headers: ArrayList<GUIWidget> = ArrayList()

    private var height: Int = 6

    val event: GUIPageEventListener<out GUIPage> = GUIPageEventListener(this)

    private val handlers: HashMap<String, GUIWidgetHandler<out GUIPage>> = HashMap()

    private val holders: HashMap<String, InventoryHolder> = HashMap()

    fun title(str: String) { title = str }

    fun header(widget: GUIWidget) { header = widget }

    fun headers(widgets: ArrayList<GUIWidget>) = headers.addAll(widgets)

    fun body(widget: GUIWidget) = body.add(widget)

    fun height(int: Int) { height = int }

    fun build() {
        require(height <= 6) { "페이지의 높이는 6을 초과할 수 없습니다." }

        var lastX = 0
        var lastY = 0

        val width = 9
        var maxHeight = 0

        val guiF = Bukkit.createInventory(this, height * width, Component.text(title).color(NamedTextColor.WHITE))

        val widgets = (headers + body)
        for ((index, widget) in widgets.withIndex()) {
            if (widget == this.header) continue

            require((widget.padTop + widget.height + widget.padBottom) <= height) { "패딩(Top + Bottom) + 높이는 ${height}를 넘을 수 없습니다." }

            if (index == headers.size) {
                lastX = 0
                if (index != 0)
                    lastY += 1
            }

            lastX += widget.padLeft
            if ((lastX + widget.width + widget.padRight) > 9) {
                lastX = widget.padLeft
                lastY += maxHeight
            }

            lastY += widget.padTop
            require((lastY + widget.height + widget.padBottom) <= height) { "위젯이 높이를 초과했습니다." }

            for (y in 0 until widget.height) {
                for (x in 0 until widget.width) {
                    val slot: Array<Int> = arrayOf(lastX + x, lastY + y)
                    if (widget.holder != null)
                        holders[slot.joinToString("_")] = widget.holder

                    if (widget.handler != null)
                        handlers[slot.joinToString("_")] = widget.handler

                    guiF.setItemInSlot(slot, widget.icon)
                }
            }

            lastX += (widget.width + widget.padRight)
            lastY -= widget.padTop

            maxHeight = max((widget.padTop + widget.height + widget.padBottom), maxHeight)
        }

        _inventory = guiF
    }

    override fun onInventoryClick(index: Int, player: Player) {
        val x = (index % 9)
        val y = (index / 9)

        val slot = "${x}_$y"
        if (holders.containsKey(slot)) {
            val holder: InventoryHolder? = holders.getValue(slot)
            if (holder != null) {
                inventory.close()
                player.openInventory(holder.inventory)
            }
        }

        if (handlers.containsKey(slot)) {
            val handler: GUIWidgetHandler<out GUIPage>? = handlers.getValue(slot)
            handler?.execute(index, player)
        }
    }

    override fun getInventory(): Inventory = _inventory
}

interface GUIfHolder: InventoryHolder {
    fun onInventoryClick(slot: Int, player: Player)
}

//abstract class GUIPageCustomHandler<T: GUIPage>(page: T) {
//    abstract fun execute(slot: Int, player: Player)
//}