package com.cout970.modeler.core.resource

import java.io.File

/**
 * Created by cout970 on 2017/02/27.
 */

fun File.toResourcePath() = ResourcePath(toURI())

fun File.createIfNeeded(): File {
    if (!exists()) {
        createNewFile()
    }
    return this
}