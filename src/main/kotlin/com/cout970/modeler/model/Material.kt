package com.cout970.modeler.model

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.ResourceManager
import com.cout970.modeler.log.print
import com.cout970.vector.extensions.vec2Of
import com.google.gson.annotations.Expose
import org.lwjgl.opengl.GL11
import java.nio.file.Path

sealed class Material(@Expose val name: String) {
    abstract fun loadTexture(resourceManager: ResourceManager)
}

class TexturedMaterial(name: String, val path: Path) : Material(name) {
    var texture: Texture? = null

    override fun loadTexture(resourceManager: ResourceManager) {
        try {
            val url = path.toUri().toURL()
            texture = resourceManager.getTexture(url.openStream()).apply {
                magFilter = Texture.PIXELATED
                minFilter = Texture.PIXELATED
            }
        } catch (e: Exception) {
            e.print()
            texture = Texture(0, vec2Of(1), GL11.GL_TEXTURE_2D)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TexturedMaterial) return false

        if (path != other.path) return false
        if (texture != other.texture) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + (texture?.hashCode() ?: 0)
        return result
    }


}

object MaterialNone : Material("noTexture") {
    override fun loadTexture(resourceManager: ResourceManager) = Unit
}