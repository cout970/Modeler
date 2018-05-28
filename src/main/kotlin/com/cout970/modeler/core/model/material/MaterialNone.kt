package com.cout970.modeler.core.model.material

import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import java.util.*


object MaterialNone : IMaterial {

    override val id: UUID = UUID.fromString("89672293-60d2-46ea-9b56-46c624dec60a")
    override val name: String = "No Texture"
    override val size: IVector2 = vec2Of(64)
    lateinit var whiteTexture: Texture
        private set

    override fun loadTexture(resourceLoader: ResourceLoader): Boolean {
        whiteTexture = resourceLoader.getTexture("assets/textures/debug.png")
        return false
    }

    override fun hasChanged(): Boolean = false

    override fun bind() {
        whiteTexture.bind()
    }
}