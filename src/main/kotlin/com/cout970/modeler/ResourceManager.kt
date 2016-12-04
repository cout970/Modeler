package com.cout970.modeler

import java.io.InputStream

/**
 * Created by cout970 on 2016/11/29.
 */
class ResourceManager {
    fun readResource(name: String): InputStream {
        return Thread.currentThread().contextClassLoader.getResourceAsStream(name)
    }
}