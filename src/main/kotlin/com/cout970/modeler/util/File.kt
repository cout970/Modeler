package com.cout970.modeler.util

import java.io.File

/**
 * Created by cout970 on 2017/02/04.
 */

fun File.createIfNeeded(): File {
    if (!exists()) {
        createNewFile()
    }
    return this
}