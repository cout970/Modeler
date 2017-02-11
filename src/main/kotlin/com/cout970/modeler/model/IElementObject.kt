package com.cout970.modeler.model

import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

interface IElementObject : IElement {

    val positions: List<IVector3>
    val textures: List<IVector2>
    val vertex: List<VertexIndex>
    val faces: List<QuadIndex>

    fun updateVertex(newVertex: List<Vertex>): IElementObject
}