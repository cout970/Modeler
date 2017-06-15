package com.cout970.modeler.core.resource

import java.io.File

/**
 * Created by cout970 on 2017/02/27.
 */

fun String.fromClasspath(): ResourcePath {
    val res = Thread.currentThread().contextClassLoader.getResource(this)
    requireNotNull(res) { "Unable to find resource at '$this'" }
    return ResourcePath(res.toURI())
}

fun File.toResourcePath() = ResourcePath(toURI())

fun File.createIfNeeded(): File {
    if (!exists()) {
        createNewFile()
    }
    return this
}