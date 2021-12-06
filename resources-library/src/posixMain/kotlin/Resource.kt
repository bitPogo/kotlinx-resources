package com.goncalossilva.resource

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.F_OK
import platform.posix.access
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen

actual public class Resource actual constructor(private val path: String) {
    actual public fun exists(): Boolean = access(path, F_OK) != -1

    actual public fun readText(): String = buildString {
        val file = fopen(path, "r") ?: throw RuntimeException("Cannot open file $path")
        try {
            memScoped {
                val buffer = allocArray<ByteVar>(BUFFER_SIZE)
                do {
                    val line = fgets(buffer, BUFFER_SIZE, file)?.also { append(it.toKString()) }
                } while (line != null)
            }
        } finally {
            fclose(file)
        }
    }

    private companion object {
        private const val BUFFER_SIZE = 8 * 1024
    }
}
