package com.cout970.modeler.selection

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.util.getElement

data class FacePath(val elementPath: ElementPath, val faceIndex: Int, val vertex: List<Int>) {

    fun toQuad(model: Model): Quad {
        return model.getElement(elementPath).getQuads()[faceIndex]
    }
}