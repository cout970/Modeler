package com.cout970.modeler.model

import com.cout970.modeler.model.api.IElement
import com.cout970.modeler.model.api.IElementGroup

data class ElementGroup(
        override val elements: List<IElement>,
        override val name: String = "Group_${groupCount++}"
) : IElementGroup {

    companion object {
        private var groupCount = 0
    }

    override fun getQuads(): List<Quad> = elements.flatMap { it.getQuads() }
    override fun getVertices(): List<Vertex> = elements.flatMap { it.getVertices() }

    override fun deepCopy(elements: List<IElement>): IElementGroup {
        return ElementGroup(elements)
    }
}