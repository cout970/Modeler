package com.cout970.modeler.util

import com.cout970.modeler.core.resource.ResourcePath
import java.io.File

fun File.createParentsIfNeeded(isFolder: Boolean = false) {
    val parts = path.split(File.separator)
    val indices = if (isFolder) parts.indices else parts.indices.take(parts.size - 1)

    indices.forEach { index ->
        val subPath = parts.subList(0, index + 1).joinToString(File.separator)
        File(subPath).let { if (!it.exists()) it.mkdir() }
    }
}

fun File.toResourcePath() = ResourcePath(toURI())

fun String.fromClasspath(): ResourcePath {
    val res = Thread.currentThread().contextClassLoader.getResource(this)
    requireNotNull(res) { "Unable to find resource at '$this'" }
    return ResourcePath(res.toURI())
}

