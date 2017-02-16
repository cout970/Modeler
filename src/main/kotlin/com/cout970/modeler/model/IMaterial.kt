package com.cout970.modeler.model

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.log.print
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.resource.ResourcePath
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import com.google.gson.annotations.Expose
import javax.swing.JOptionPane

interface IMaterial {
    val name: String
    val size: IVector2
    fun bind()
    fun hasChanged(): Boolean
    fun loadTexture(resourceLoader: ResourceLoader)
}

class TexturedMaterial(@Expose override val name: String, val path: ResourcePath) : IMaterial {
    var texture: Texture? = null
    private var lastModified = -1L
    override val size: IVector2 get() = texture?.size ?: vec2Of(1)

    override fun loadTexture(resourceLoader: ResourceLoader) {
        try {
            texture = resourceLoader.getTexture(path.inputStream()).apply {
                magFilter = Texture.PIXELATED
                minFilter = Texture.PIXELATED
            }
            lastModified = path.lastModifiedTime()
        } catch (e: Exception) {
            e.print()
            JOptionPane.showMessageDialog(null, "Error loading texture: Missing resource ($path)")
            texture = null
        }
    }

    override fun hasChanged(): Boolean {
        return lastModified != path.lastModifiedTime()
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

object MaterialNone : IMaterial {
    override val name: String = "noTexture"
    override val size: IVector2 = vec2Of(64)
    lateinit var whiteTexture: Texture
        private set

    override fun loadTexture(resourceLoader: ResourceLoader) {
        whiteTexture = resourceLoader.getTexture("assets/textures/debug.png")
    }

    override fun hasChanged(): Boolean = false

    override fun bind() {
        whiteTexture.bind()
    }
}