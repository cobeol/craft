package io.github.cobeol.craft.gui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent

class GUIPageEventListener<T: GUIPage>(private val page: T): Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val holder = event.inventory.holder
        if (!page.javaClass.isInstance(holder))
            return

        if (event.click == ClickType.SHIFT_LEFT || event.click == ClickType.SHIFT_RIGHT)
            if (event.rawSlot >= event.inventory.size)
                    event.isCancelled = true

        val cursor = event.whoClicked.itemOnCursor
        if (event.click == ClickType.DOUBLE_CLICK && cursor.type != Material.AIR)
            if (event.inventory.contents.any { it != null && it.type == cursor.type && it.isSimilar(cursor) })
                event.isCancelled = true

        if (event.rawSlot >= event.inventory.size)
            return

        event.isCancelled = true
        page.onInventoryClick(event.rawSlot, event.whoClicked as Player)
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val holder = event.inventory.holder
        if (!page.javaClass.isInstance(holder))
            return

        if (event.rawSlots.any { it < event.inventory.size })
            event.isCancelled = true
    }

    @EventHandler
    fun onInventoryCloseEvent(event: InventoryCloseEvent) {
        val holder = event.inventory.holder
        if (!page.javaClass.isInstance(holder))
            return

        HandlerList.unregisterAll(this)
    }
}