package io.github.cobeol.craft.sample

import io.github.cobeol.craft.gui.GUIBuilder
import io.github.cobeol.craft.gui.GUIPage
import io.github.cobeol.craft.gui.GUIWidget
import io.github.cobeol.craft.monun.loader.getChildInstancesOfType
import io.github.cobeol.craft.status.Stat
import io.github.cobeol.craft.tooltip.TooltipBuilder
import io.github.cobeol.craft.tooltip.TooltipSection
import io.github.cobeol.craft.tooltip._text
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class SampleStatPage(stats: SampleStats, isExpHidden: Boolean = false): GUIPage() {
    init {
        title("test")

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

            val statHeaderWidget = GUIWidget(1, 1, tooltip.build(), null, padding = 1, padTop = 0, padRight = 0)
            body(statHeaderWidget)
        }

        header(GUIWidget(1, 1, ItemStack(Material.STONE), null, padTop = 0))
    }
}

class SampleStatBuilder(stats: SampleStats): GUIBuilder() {
    init {
        page(SampleStatPage(stats))
    }
}

//class StatHeaderWidget(
//    width: Int,
//    height: Int,
//    icon: ItemStack,
//    holder: InventoryHolder?,
//    padding: Int = 0
//): GUIWidget(width, height, icon, holder, padding) {
////    init {
////        width
////    }
//}