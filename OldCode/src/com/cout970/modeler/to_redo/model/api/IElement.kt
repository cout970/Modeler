package com.cout970.modeler.to_redo.model.api

import com.cout970.modeler.to_redo.model.Quad
import com.cout970.modeler.to_redo.model.Vertex

interface IElement {

    fun getQuads(): List<Quad>
    fun getVertices(): List<Vertex>
}