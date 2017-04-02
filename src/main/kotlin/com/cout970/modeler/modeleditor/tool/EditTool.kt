package com.cout970.modeler.modeleditor.tool

import com.cout970.modeler.model.ElementLeaf
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.api.IElement
import com.cout970.modeler.model.api.IElementGroup
import com.cout970.modeler.model.api.QuadIndex
import com.cout970.modeler.model.util.applyElementLeaves
import com.cout970.modeler.model.util.applyVertexPos
import com.cout970.modeler.model.util.getElement
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.action.ActionModifyModelShape
import com.cout970.modeler.modeleditor.splitUV
import com.cout970.modeler.selection.*
import com.cout970.modeler.selection.subselection.SubSelectionFace
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

    //
    // TRANSFORM
    //
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

    //
    // DELETE
    //
    fun deleteElements(source: Model, selection: ElementSelection): Model {
        return source.applyElementLeaves(selection) { path, elem -> null }
    }

    fun deleteFaces(source: Model, selection: VertexPosSelection): Model {
        val handler = selection.subPathHandler
        if (handler is SubSelectionFace) {
            return source.applyElementLeaves(selection.toElementSelection()) { path, elem ->
                val facesToRemove = handler.paths.filter { it.elementPath == path }
                if (facesToRemove.isEmpty()) {
                    elem
                } else if (facesToRemove.size == elem.faces.size) {
                    null
                } else {
                    elem.removeFaces(facesToRemove.map { it.faceIndex })
                }
            }
        }
        return source
    }

    //
    // PASTE
    //
    fun pasteElement(currentModel: Model, oldModel: Model, oldSelection: ElementSelection): Model {
        val newElements = oldSelection.paths.map { oldModel.getElement(it) }
        var model = currentModel
        for (elem in newElements) {
            model = insertElement(model, elem)
        }
        return model
    }

    fun pasteFaces(currentModel: Model, oldModel: Model, oldSelection: VertexPosSelection): Model {
        val handler = oldSelection.subPathHandler
        if (handler is SubSelectionFace) {
            val newFaces = handler.paths.map { it.toQuad(oldModel) }
            val pos = newFaces.flatMap { it.vertex.map { it.pos } }.distinct()
            val tex = newFaces.flatMap { it.vertex.map { it.tex } }.distinct()
            val faces = newFaces.map {
                QuadIndex(
                        it.a.toIndex(pos, tex),
                        it.b.toIndex(pos, tex),
                        it.c.toIndex(pos, tex),
                        it.d.toIndex(pos, tex)
                )
            }
            val element = ElementLeaf(pos, tex, faces)
            return insertElement(currentModel, element)
        } else {
            return currentModel
        }
    }

    //
    // INSERT
    //
    fun insertElementLeaf(source: Model, elem: ElementLeaf): Model {
        val newElem = elem.copy(positions = elem.positions.map { it + insertPosition })
        return insertElement(source, newElem)
    }

    fun insertElement(source: Model, elem: IElement, path: ElementPath = insertPath): Model {
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

    //
    // SPLIT TEXTURE
    //
    fun splitTextures(editor: ModelEditor) {
        if (editor.selectionManager.vertexTexSelection != VertexTexSelection.EMPTY) {
            val newModel = editor.model.splitUV(editor.selectionManager.vertexTexSelection)
            editor.historyRecord.doAction(ActionModifyModelShape(editor, newModel))
        }
    }
}
