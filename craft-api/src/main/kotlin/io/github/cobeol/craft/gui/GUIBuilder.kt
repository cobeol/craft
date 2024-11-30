package io.github.cobeol.craft.gui

import org.bukkit.Bukkit

import org.bukkit.plugin.java.JavaPlugin

open class GUIBuilder {
    lateinit var page: GUIPage

    private val pages: ArrayList<GUIPage> = ArrayList()

    fun page(guiPage: GUIPage) { pages.add(guiPage) }

    fun pages(guiPages: ArrayList<GUIPage>) { pages.addAll(guiPages) }

    fun registerEvent(plugin: JavaPlugin) = pages.forEach { page -> Bukkit.getPluginManager().registerEvents(page.guiEvent, plugin) }

    fun build(height: Int = 6) {
        val headers: ArrayList<GUIWidget> = ArrayList()
        pages.forEach { page -> headers.add(page.header)  }
        pages.forEach { page ->
            page.also { it ->
                it.height(height)
                it.build()
            }
        }

        page = pages.first()
    }
}