package com.cout970.modeler.resource

import com.cout970.glutilities.texture.Texture
import com.cout970.glutilities.texture.TextureLoader
import org.liquidengine.legui.image.Image
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * Created by cout970 on 2016/11/29.
 */
class ResourceLoader {

    fun readResource(name: String): InputStream {
        return Thread.currentThread().contextClassLoader.getResourceAsStream(name) ?: throw FileNotFoundException(name)
    }

    fun getTexture(name: String): Texture {
        val aux = TextureLoader.loadTexture(readResource(name))
        return TextureLoader.uploadTexture2D(aux)
    }

    fun getTexture(stream: InputStream): Texture {
        val aux = TextureLoader.loadTexture(stream)
        return TextureLoader.uploadTexture2D(aux)
    }

    fun getImage(path: String): Image {
        return Image(path)
    }
}