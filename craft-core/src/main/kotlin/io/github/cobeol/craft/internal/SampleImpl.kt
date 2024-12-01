package io.github.cobeol.craft.internal

import io.github.cobeol.craft.monun.loader.LibraryLoader
import io.github.cobeol.craft.Sample

class SampleImpl: Sample {
    private val version = LibraryLoader.loadNMS(Version::class.java)

    override fun printCoreMessage() {
        println("This is core, version = ${version.value}")
    }
}

interface Version {
    val value: String
}