package com.cout970.modeler.modeleditor.tool

import com.cout970.modeler.model.ElementLeaf
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.api.IElement
import com.cout970.modeler.model.api.IElementGroup
import com.cout970.modeler.model.api.IElementLeaf
import com.cout970.modeler.model.structure.zipVertexPaths
import com.cout970.modeler.model.util.applyElementLeaves
import com.cout970.modeler.model.util.applyVertexPos
import com.cout970.modeler.model.util.getElement
import com.cout970.modeler.selection.ElementPath
import com.cout970.modeler.selection.ElementSelection
import com.cout970.modeler.selection.VertexPosSelection
import com.cout970.modeler.util.rotateAround
import com.cout970.modeler.util.scale
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/02/11.
 */
class EditTool : IModelTranslate, IModelRotate, IModelScale {

    var insertPath: ElementPath = ElementPath(intArrayOf())
    var insertPosition = vec3Of(0, 0, 0)

    // TRANSFORM

    override fun translate(source: Model, selection: VertexPosSelection, translation: IVector3): Model {
        return source.applyVertexPos(selection) { path, vertex ->
            vertex + translation
        }
    }

    override fun rotate(source: Model, selection: VertexPosSelection, pivot: IVector3, rotation: IQuaternion): Model {
        return source.applyVertexPos(selection) { path, vertex ->
            vertex.rotateAround(pivot, rotation)
        }
    }

    override fun scale(source: Model, selection: VertexPosSelection, center: IVector3, axis: SelectionAxis,
                       offset: Float): Model {
        return source.applyVertexPos(selection) { path, vertex ->
            vertex.scale(center, axis, offset)
        }
    }

    // DELETE

    fun deleteElements(source: Model, selection: ElementSelection): Model {
        return source.applyElementLeaves(selection) { path, elem -> null }
    }

    fun deleteFaces(source: Model, selection: VertexPosSelection): Model {
        val structure = source.zipVertexPaths(selection)

        val faceIndices = structure.quads.map { (vertex) ->
            val elem = source.getElement(vertex[0].first.elementPath) as IElementLeaf

            val indices = vertex.map { it.first.vertexIndex }
            val quadIndex = elem.faces.indexOfFirst { it.pos.toSet() == indices.toSet() }

            elem to quadIndex
        }

        return source.applyElementLeaves(selection.toElementSelection()) { path, elem ->
            val quadsList = elem.faces.mapIndexed { index, quadIndex -> elem to index }
            if (faceIndices.containsAll(quadsList)) {
                null
            } else if (faceIndices.any { it in quadsList }) {
                elem.removeFaces(quadsList.filter { it in faceIndices }.map { it.second })
            } else {
                elem
            }
        }
    }

    // PASTE

    fun pasteElement(currentModel: Model, oldModel: Model, oldSelection: ElementSelection): Model {
        //TODO
        return currentModel
    }

    fun pasteFaces(currentModel: Model, oldModel: Model, oldSelection: VertexPosSelection): Model {
        //TODO
        return currentModel
    }

    // INSERT

    fun insertElementLeaf(source: Model, elem: ElementLeaf): Model {
        val newElem = elem.copy(positions = elem.positions.map { it + insertPosition })
        return insertElement(source, newElem)
    }

    fun insertElement(source: Model, elem: IElement, path: ElementPath = insertPath): Model {
        if (source.getElement(path) !is IElementGroup) throw IllegalArgumentException("Invalid insertion path")
        return source.copy(elements = insert(source.elements, elem, path, 0))
    }

    private fun insert(list: List<IElement>, elem: IElement, path: ElementPath, level: Int): List<IElement> {
        if (insertPath.indices.size == level) {
            return list + elem
        } else {
            val group = list[insertPath.indices[level]] as IElementGroup
            return insert(group.elements, elem, path, level + 1)
        }
    }
}