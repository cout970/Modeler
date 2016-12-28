package com.cout970.modeler

import com.cout970.glutilities.texture.Texture
import com.cout970.glutilities.texture.TextureLoader
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * Created by cout970 on 2016/11/29.
 */
class ResourceManager {

    fun readResource(name: String): InputStream {
        return Thread.currentThread().contextClassLoader.getResourceAsStream(name) ?: throw FileNotFoundException(name)
    }

    fun getTexture(name: String): Texture {
        val aux = TextureLoader.loadTexture(readResource(name))
        return TextureLoader.uploadTexture2D(aux)
    }
}