package com.cout970.modeler.core.model.material

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.core.resource.ResourcePath
import com.cout970.modeler.gui.event.pushNotification
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import java.io.FileNotFoundException
import java.util.*

class TexturedMaterial(override val name: String, val path: ResourcePath,
                       override val id: UUID = UUID.randomUUID()) : IMaterial {

    var texture: Texture? = null
    var loadingError = false
    var tries = 0

    private var lastModified = -1L
    override val size: IVector2 get() = texture?.size ?: vec2Of(1)

    override fun loadTexture(resourceLoader: ResourceLoader): Boolean {
        try {
            texture?.close()
            texture = resourceLoader.getTexture(path.inputStream()).apply {
                magFilter = Texture.PIXELATED
                minFilter = Texture.PIXELATED
                wrapT = Texture.CLAMP_TO_EDGE
                wrapS = Texture.CLAMP_TO_EDGE
            }
            lastModified = path.lastModifiedTime()
            loadingError = false
            tries = 0
        } catch (e: FileNotFoundException) {
            log(Level.ERROR) { "Unable to find material, name: $name, path: $path" }
            pushNotification("Material not found", "Unable to find material $name at path '$path'")
            texture = null
            loadingError = true
            tries = 0
        } catch (e: Exception) {
            log(Level.ERROR) { "Error loading material, name: $name, path: $path, try: $tries" }
            tries++
            Thread.sleep(50)
            if (tries > 60) {
                pushNotification("Material load error", "Unable to load material $name at path '$path'")
                e.print()
                texture = null
                loadingError = true
            } else {
                return true
            }
        }
        return false
    }

    override fun hasChanged(): Boolean {
        return !loadingError && lastModified != path.lastModifiedTime()
    }

    override fun bind() {
        texture?.bind() ?: MaterialNone.whiteTexture.bind()
    }

    fun copy(name: String = this.name, path: ResourcePath = this.path) =
            TexturedMaterial(name, path, id)

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