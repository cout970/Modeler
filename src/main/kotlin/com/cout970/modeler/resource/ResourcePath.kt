package com.cout970.modeler.resource

import java.io.File
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by cout970 on 2017/01/19.
 */
class ResourcePath(val uri: URI) {

    val parent: ResourcePath? get() = ResourcePath(Paths.get(uri).parent.toUri())

    fun resolve(path: String) = ResourcePath(uri.resolve(path))

    fun enterZip(file: String) = ResourcePath(URI("jar:$uri!/$file"))

    fun inputStream() = uri.toURL().openStream()!!

    override fun toString(): String {
        return uri.toString()
    }

    fun toPath(): Path = Paths.get(uri)
}


fun File.createPath() = ResourcePath(toURI())