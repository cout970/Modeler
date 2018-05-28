package com.cout970.modeler.api.model.material

import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.vector.api.IVector2
import java.util.*

interface IMaterial {

    val id: UUID
    val name: String
    val size: IVector2
    fun bind()
    fun hasChanged(): Boolean
    fun loadTexture(resourceLoader: ResourceLoader): Boolean
}

