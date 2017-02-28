package com.cout970.modeler.resource

import java.io.File

/**
 * Created by cout970 on 2017/02/27.
 */

fun File.toResourcePath() = ResourcePath(toURI())