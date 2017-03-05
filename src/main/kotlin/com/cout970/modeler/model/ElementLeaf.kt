package com.cout970.modeler.model

import com.cout970.modeler.model.api.IElement
import com.cout970.modeler.model.api.IElementLeaf
import com.cout970.modeler.model.api.QuadIndex
import com.cout970.modeler.selection.VertexPath
import com.cout970.modeler.selection.VertexPosSelection
import com.cout970.modeler.selection.VertexTexSelection
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

data class ElementLeaf(
        override val positions: List<IVector3>,
        override val textures: List<IVector2>,
        override val faces: List<QuadIndex>
) : IElementLeaf {

    override fun getVertices(): List<Vertex> = getQuads().flatMap(Quad::vertex).distinct()
    override fun getQuads(): List<Quad> = faces.map { it.toQuad(this) }

    override fun transformPos(selection: VertexPosSelection,
                              func: (VertexPath, IVector3) -> IVector3): IElement {

        val newPos = positions.mapIndexed { index, pos ->
            val path = selection.pathList.find { it.vertexIndex == index }
            if (path != null) {
                func(path, pos)
            } else pos
        }
        return ElementLeaf(newPos, textures, faces)
    }

    override fun transformTex(selection: VertexTexSelection,
                              func: (VertexPath, IVector2) -> IVector2): IElement {
        val newTex = textures.mapIndexed { index, pos ->
            val path = selection.pathList.find { it.vertexIndex == index }
            if (path != null) {
                func(path, pos)
            } else pos
        }
        return ElementLeaf(positions, newTex, faces)
    }

    override fun removeFaces(faces: List<Int>): IElementLeaf {
        return ElementLeaf(positions, textures, this.faces.filterIndexed { index, _ -> index !in faces })
    }
}