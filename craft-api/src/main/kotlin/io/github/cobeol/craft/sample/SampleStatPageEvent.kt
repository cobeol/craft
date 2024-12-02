package io.github.cobeol.craft.sample

import io.github.cobeol.craft.gui.GUIPageCustomHandler
import org.bukkit.entity.Player

class SampleStatPageEvent(statPage: SampleStatPage): GUIPageCustomHandler<SampleStatPage>(statPage) {
    override fun execute(slot: Int, player: Player) {
        val x = (slot % 9)
        val y = (slot / 9)

        player.sendMessage("Interaction#1($x, $y)")
    }
}