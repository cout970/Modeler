package com.cout970.modeler.model.material

import com.cout970.modeler.resource.ResourceLoader
import com.cout970.vector.api.IVector2

interface IMaterial {
    val name: String
    val size: IVector2
    fun bind()
    fun hasChanged(): Boolean
    fun loadTexture(resourceLoader: ResourceLoader)
}

