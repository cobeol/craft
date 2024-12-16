package io.github.cobeol.craft.tooltip

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import kotlin.collections.forEach

fun _text(content: String = "", decorations: HashMap<TextDecoration, Boolean> = hashMapOf(), color: NamedTextColor = NamedTextColor.WHITE) =
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