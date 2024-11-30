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
import io.github.cobeol.craft.tooltip._text

class TooltipBuilder {
    private var header: Component = text().build()
    private val body: MutableList<TooltipSection> = mutableListOf()
    private var icon: Material = Material.STONE
    private var length: Int = 16
    private var prefix: String = "•"

    fun header(component: Component) { header = component }

    fun body(section: TooltipSection) { body.add(section) }

    fun body(sections: MutableList<TooltipSection>) { body.addAll(sections) }

    fun icon(material: Material) { icon = material }

    fun length(int: Int) { length = int }

    fun prefix(str: String) { prefix = str }

    fun build(): ItemStack {
        val item = ItemStack(icon)
        val itemMeta = item.itemMeta

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
            for (i in 0..(length - 1))
                it.append(space())
        }
        .color(NamedTextColor.WHITE)
        .build()

//fun TooltipBuilder._text(content: String, decorations: HashMap<TextDecoration, Boolean> = hashMapOf(), color: NamedTextColor = NamedTextColor.WHITE) =
//    text()
//        .decoration(TextDecoration.ITALIC, false)
//        .also {
//            decorations.forEach { decoration ->
//                it.decoration(decoration.key, decoration.value)
//            }
//        }
//        .content(content)
//        .color(color)
//        .build()

class TooltipSection(
    val header: String,
    val body: MutableList<Component>
)

fun _text(content: String, decorations: HashMap<TextDecoration, Boolean> = hashMapOf(), color: NamedTextColor = NamedTextColor.WHITE) =
    text()
        .decoration(TextDecoration.ITALIC, false)
        .also {
            decorations.forEach { decoration ->
                it.decoration(decoration.key, decoration.value)
            }
        }
        .content(content)
        .color(color)
        .build()