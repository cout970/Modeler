package com.cout970.modeler.model

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.ResourceManager
import com.cout970.modeler.log.print
import com.cout970.modeler.util.ResourcePath
import com.cout970.vector.extensions.vec2Of
import com.google.gson.annotations.Expose
import org.lwjgl.opengl.GL11

sealed class Material(@Expose val name: String) {

    abstract fun bind()
    abstract fun loadTexture(resourceManager: ResourceManager)
}

class TexturedMaterial(name: String, val path: ResourcePath) : Material(name) {
    var texture: Texture? = null

    override fun loadTexture(resourceManager: ResourceManager) {
        try {
            texture = resourceManager.getTexture(path.inputStream()).apply {
                magFilter = Texture.PIXELATED
                minFilter = Texture.PIXELATED
            }
        } catch (e: Exception) {
            e.print()
            texture = Texture(0, vec2Of(1), GL11.GL_TEXTURE_2D)
        }
    }

    override fun bind() {
        texture?.bind() ?: MaterialNone.whiteTexture.bind()
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

    lateinit var whiteTexture: Texture
        private set

    override fun loadTexture(resourceManager: ResourceManager) {
        whiteTexture = resourceManager.getTexture("assets/textures/debug.png")
    }

    override fun bind() {
        whiteTexture.bind()
    }
}