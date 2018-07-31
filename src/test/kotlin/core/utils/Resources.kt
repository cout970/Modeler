package core.utils

import com.cout970.modeler.core.resource.ResourcePath

fun getPath(path: String): ResourcePath {
    val url = Thread.currentThread().contextClassLoader.getResource(path)
    return ResourcePath(url.toURI())
}