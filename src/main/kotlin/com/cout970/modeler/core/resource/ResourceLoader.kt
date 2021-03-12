package com.cout970.modeler.core.resource

import com.cout970.glutilities.texture.Texture
import com.cout970.glutilities.texture.TextureLoader
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import org.liquidengine.legui.image.StbBackedLoadableImage
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
        log(Level.FINEST) { "[ResourceLoader] Loading texture: '$name'" }
        val aux = TextureLoader.loadTexture(readResource(name))
        return TextureLoader.uploadTexture2D(aux)
    }

    fun getTextureCubeMap(prefix: String): Texture {
        log(Level.FINEST) { "[ResourceLoader] Loading cubemap texture: '$prefix'" }
        val top = TextureLoader.loadTexture(readResource("${prefix}_top.png"))
        val bottom = TextureLoader.loadTexture(readResource("${prefix}_bottom.png"))
        val left = TextureLoader.loadTexture(readResource("${prefix}_left.png"))
        val right = TextureLoader.loadTexture(readResource("${prefix}_right.png"))
        val front = TextureLoader.loadTexture(readResource("${prefix}_front.png"))
        val back = TextureLoader.loadTexture(readResource("${prefix}_back.png"))

        return TextureLoader.uploadTextureCubeMap(listOf(
                right,  // GL_TEXTURE_CUBE_MAP_POSITIVE_X
                left,   // GL_TEXTURE_CUBE_MAP_NEGATIVE_X
                top,    // GL_TEXTURE_CUBE_MAP_POSITIVE_Y
                bottom, // GL_TEXTURE_CUBE_MAP_NEGATIVE_Y
            back,   // GL_TEXTURE_CUBE_MAP_POSITIVE_Z
            front   // GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
        )
        )
    }

    fun getTexture(stream: InputStream): Texture {
        val aux = TextureLoader.loadTexture(stream)
        return TextureLoader.uploadTexture2D(aux)
    }

    fun getImage(path: String): StbBackedLoadableImage {
        return StbBackedLoadableImage(path)
    }
}