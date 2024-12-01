package io.github.cobeol.craft.sample

import io.github.cobeol.craft.gui.GUIBuilder
import io.github.cobeol.craft.status.Stat
import io.github.cobeol.craft.status.Stats
import io.github.cobeol.craft.status.Status
import org.bukkit.Material
import java.util.UUID

class SampleStatus: Status {
    override val stats: SampleStats = SampleStats()
    override val gui: GUIBuilder = GUIBuilder()
}

class SampleStats: Stats {
    val strength = StrengthStat()
    val agility = AgilityStat()
    val fortitude = FortitudeStat()
    val intelligence = IntelligenceStat()
}

class StrengthStat: Stat() {
    init {
        symbol = "STR"
        icon = Material.IRON_AXE
        maxLevel = 25
        randomLevel = 5
        expEvent = StrengthEventListener(this)
        uniqueId = UUID.fromString("17a80687-4d66-4f83-ad12-f83e06866876")
    }
}

class FortitudeStat: Stat() {
    init {
        symbol = "FTD"
        icon = Material.SHIELD
        maxLevel = 25
        randomLevel = 5
        expEvent = FortitudeEventListener(this)
        uniqueId = UUID.fromString("17a80687-4d66-4f83-ad12-f83e06866876")
    }
}

class AgilityStat: Stat() {
    init {
        symbol = "AGL"
        icon = Material.FEATHER
        maxLevel = 25
        randomLevel = 5
        expEvent = AgilityEventListener(this)
        uniqueId = UUID.fromString("17a80687-4d66-4f83-ad12-f83e06866876")
    }
}

class IntelligenceStat: Stat() {
    init {
        symbol = "INT"
        icon = Material.POTION
        maxLevel = 25
        randomLevel = 5
        expEvent = IntelligenceEventListener(this)
        uniqueId = UUID.fromString("17a80687-4d66-4f83-ad12-f83e06866876")
    }
}