package com.cout970.modeler.core.resource

import com.cout970.modeler.core.log.print
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
        if (this.isFile()) {
            try {
                val file = Paths.get(uri).toFile()
                if (file.exists() && file.isFile) {
                    return file.lastModified()
                }
            } catch (e: Exception) {
                //ignored
                e.print()
            }
        }
        return -1
    }

    fun isFile() = uri.scheme == "file"

    override fun toString(): String {
        return uri.toString()
    }

    fun toPath(): Path = Paths.get(uri)

    fun isValid(): Boolean {
        if (uri.scheme == "file") {
            val file = toPath().toFile()
            if (file.exists() && file.isFile) {
                return true
            }
        } else if (uri.scheme == "zip" || uri.scheme == "jar") {
            return true
        }
        return false
    }

    companion object {

        fun textureFromResourceLocation(str: String, path: ResourcePath): ResourcePath {
            if (str.contains(':')) {
                val domain = str.substringBefore(':')
                val file = fromResourceLocation(domain, "textures/" + str.substringAfter(':') + ".png")

                val strPath = path.uri.toASCIIString()
                if (strPath.contains(domain)) {
                    val index = strPath.indexOf(domain)
                    val root = strPath.substring(0 until index)
                    val relative = File(".").toURI().relativize(file.uri).toASCIIString()
                    return ResourcePath(URI(root + relative))
                }
                return file
            }
            return File("textures/$str.png").toResourcePath()
        }

        fun fromResourceLocation(str: String): ResourcePath {
            if (str.contains(':')) {
                return fromResourceLocation(str.substringBefore(':'), str.substringAfter(':'))
            }
            return File(str).toResourcePath()
        }

        fun fromResourceLocation(domain: String, path: String): ResourcePath = File("$domain/$path").toResourcePath()
    }
}