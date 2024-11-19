package io.github.cobeol.craft

import io.github.cobeol.craft.loader.LibraryLoader

interface Sample {
    companion object: Sample by LibraryLoader.loadImplement(Sample::class.java)

    fun printCoreMessage()
}