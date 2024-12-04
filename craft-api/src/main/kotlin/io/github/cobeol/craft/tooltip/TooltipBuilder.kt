package io.github.cobeol.craft.tooltip

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.*
import net.kyori.adventure.text.format.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import kotlin.math.max

class TooltipBuilder {
    private var header: Component = text().build()
    private val body: MutableList<TooltipSection> = mutableListOf()
    private var icon: Pair<Material, Int> = Pair(Material.STONE, 0)
    private var length: Int = 16
    private var prefix: String = "•"

    fun header(component: Component) { header = component }

    fun body(section: TooltipSection) { body.add(section) }

    fun body(sections: MutableList<TooltipSection>) { body.addAll(sections) }

    fun icon(item: Pair<Material, Int>) { icon = item }

    fun length(int: Int) { length = int }

    fun prefix(str: String) { prefix = str }

    fun build(): ItemStack {
        val item = ItemStack(icon.first)
        val itemMeta = item.itemMeta

        require(icon.second >= 0) { "CustomModelData는 음수일 수 없습니다." }

        if (icon.second != 0)
            itemMeta.setCustomModelData(icon.second)

        val modifier = AttributeModifier(
            NamespacedKey("cobeol_craft", "none_text"),
            0.0,
            AttributeModifier.Operation.ADD_NUMBER
        )

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)

        itemMeta.addAttributeModifier(
            Attribute.GENERIC_ATTACK_DAMAGE,
            modifier
        )

        itemMeta.displayName(header)

        var maxHeaderLength = 0
        body.forEach { section -> maxHeaderLength = max(section.header.length, maxHeaderLength) }

        val lore = mutableListOf<Component>().apply {
            body.forEach { section ->
                add(text().build())

                add(contour(4)
                    .append(_text(" ${section.header} ", hashMapOf(TextDecoration.STRIKETHROUGH to false)))
                    .append(contour(length - (maxHeaderLength - section.header.length)))
                )

                add(text().build())

                section.body.forEach { component ->
                    add(_text("$prefix ").append(component))
                }

                add(text().build())
            }
        }

        itemMeta.lore(lore)
        item.itemMeta = itemMeta

        return item
    }
}

fun TooltipBuilder.contour(length: Int): Component =
    text()
        .decoration(TextDecoration.ITALIC, false)
        .decoration(TextDecoration.BOLD, false)
        .decoration(TextDecoration.STRIKETHROUGH, true)
        .also {
            (0..(length - 1)).forEach { _ ->
                it.append(space())
            }
        }
        .color(NamedTextColor.WHITE)
        .build()

class TooltipSection(
    val header: String,
    val body: MutableList<Component>
)

