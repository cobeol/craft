package io.github.cobeol.craft.sample

import io.github.cobeol.craft.gui.GUIPage
import io.github.cobeol.craft.gui.GUIWidget
import io.github.cobeol.craft.gui.GUIWidgetHandler
import io.github.cobeol.craft.monun.loader.getChildInstancesOfType
import io.github.cobeol.craft.status.Stat
import io.github.cobeol.craft.tooltip.TooltipBuilder
import io.github.cobeol.craft.tooltip.TooltipSection
import io.github.cobeol.craft.tooltip._text
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SampleStatPage(stats: SampleStats, isExpHidden: Boolean = false): GUIPage() {
    init {
        title("\uE001")

        header(GUIWidget(1, 1, ItemStack(Material.STONE), null, padTop = 0))

        val stats = stats.getChildInstancesOfType<Stat>()
        for (stat in stats) {
            val tooltip = TooltipBuilder().apply {
                stat.icon?.let { icon(it) }

                header(
                    _text("${stat.symbol}:", hashMapOf(TextDecoration.BOLD to true))
                        .append(_text("　Lv.${stat.level}", hashMapOf(TextDecoration.BOLD to false)))
                )

                body(
                    mutableListOf(
                        TooltipSection("특성", mutableListOf(_text("특성 없음"), _text("특성 없음"), _text("특성 없음"))),
                        TooltipSection("스킬", mutableListOf(_text("스킬 없음"), _text("스킬 없음"))),
                    )
                )
            }

            val statBodyWidget = GUIWidget(1, 1, tooltip.build(), null, padding = 1, padTop = 0, padRight = 0)
            body(statBodyWidget)
        }

        val statFooterWidget = GUIWidget(1, 1, ItemStack(Material.STONE), null, handler = SampleStatPageEvent(this), padTop = 0, padLeft = 7)
        body(statFooterWidget)
    }
}

class SampleStatPageEvent(statPage: SampleStatPage): GUIWidgetHandler<SampleStatPage>(statPage) {
    override fun execute(slot: Int, player: Player) {
        val x = (slot % 9)
        val y = (slot / 9)

        player.sendMessage("Interaction#1($x, $y)")
    }
}