package com.cout970.modeler.model

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.log.print
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.resource.ResourcePath
import com.cout970.vector.extensions.xi
import com.google.gson.annotations.Expose
import javax.swing.JOptionPane

sealed class Material(@Expose val name: String) {
    abstract val size: Int
    abstract fun bind()
    abstract fun loadTexture(resourceLoader: ResourceLoader)
}

class TexturedMaterial(name: String, val path: ResourcePath) : Material(name) {
    var texture: Texture? = null
    override val size: Int get() = texture?.size?.xi ?: 1

    override fun loadTexture(resourceLoader: ResourceLoader) {
        try {
            texture = resourceLoader.getTexture(path.inputStream()).apply {
                magFilter = Texture.PIXELATED
                minFilter = Texture.PIXELATED
            }
        } catch (e: Exception) {
            e.print()
            JOptionPane.showMessageDialog(null, "Error loading texture: Missing resource ($path)")
            texture = null
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
    override val size: Int = 1
    lateinit var whiteTexture: Texture
        private set

    override fun loadTexture(resourceLoader: ResourceLoader) {
        whiteTexture = resourceLoader.getTexture("assets/textures/debug.png")
    }

    override fun bind() {
        whiteTexture.bind()
    }
}