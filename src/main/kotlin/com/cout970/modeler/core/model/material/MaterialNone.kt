package com.cout970.modeler.to_redo.model.material

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of

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