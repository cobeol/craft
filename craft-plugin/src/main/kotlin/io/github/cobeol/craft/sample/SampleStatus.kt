package io.github.cobeol.craft.sample

import io.github.cobeol.craft.status.Stats
import io.github.cobeol.craft.status.Status

class SampleStatus: Status {
    override val stats: SampleStats = SampleStats()
}

class SampleStats: Stats {
    val test: String
        get() = "Hello, World!"
}