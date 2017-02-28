package com.cout970.modeler.model.api

import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.Vertex

interface IElement {

    fun getQuads(): List<Quad>
    fun getVertices(): List<Vertex>
}