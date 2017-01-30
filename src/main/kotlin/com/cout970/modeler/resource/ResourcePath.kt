package com.cout970.modeler.resource

import com.cout970.modeler.log.print
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

    fun lastModifiedTime(): Long {
        try {
            val file = Paths.get(uri).toFile()
            if (file.exists()) {
                return file.lastModified()
            }
        } catch (e: Exception) {
            //ignored
            e.print()
        }
        return -1
    }

    override fun toString(): String {
        return uri.toString()
    }

    fun toPath(): Path = Paths.get(uri)
}


fun File.toResourcePath() = ResourcePath(toURI())