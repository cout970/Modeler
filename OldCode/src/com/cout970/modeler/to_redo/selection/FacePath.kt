package com.cout970.modeler.to_redo.selection

import com.cout970.modeler.to_redo.model.Model
import com.cout970.modeler.to_redo.model.Quad
import com.cout970.modeler.to_redo.model.util.getElement

data class FacePath(val elementPath: ElementPath, val faceIndex: Int, val vertex: List<Int>) {

    fun toQuad(model: Model): Quad {
        return model.getElement(elementPath).getQuads()[faceIndex]
    }
}