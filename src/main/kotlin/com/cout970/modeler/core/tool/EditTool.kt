package com.cout970.modeler.core.tool

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.Object
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.selection.FaceRef
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/02/11.
 */
object EditTool {

    //
    // TRANSFORM
    //
    fun translate(source: IModel, ref: List<IObjectRef>, translation: IVector3): IModel {
        return source.modifyObjects(ref) { _, it -> it.transformer.translate(it, translation) }
    }

    fun rotate(source: IModel, ref: List<IObjectRef>, pivot: IVector3, rotation: IQuaternion): IModel {
        return source.modifyObjects(ref) { _, it -> it.transformer.rotate(it, pivot, rotation) }
    }

    fun scale(source: IModel, ref: List<IObjectRef>, center: IVector3, axis: IVector3, offset: Float): IModel {
        return source.modifyObjects(ref) { _, it -> it.transformer.scale(it, center, axis, offset) }
    }

    //
    // DELETE
    //
    fun delete(source: IModel, selection: ISelection): IModel {
        if (selection.selectionTarget != SelectionTarget.MODEL) return source
        return when (selection.selectionType) {
            SelectionType.OBJECT -> {
                source.removeObjects(source.getSelectedObjectRefs(selection))
            }
            SelectionType.FACE -> {
                val toRemove = mutableListOf<IObjectRef>()
                val edited = mutableMapOf<IObjectRef, IObject>()

                source.objects.forEachIndexed { objIndex, obj ->
                    if (obj is Object) {
                        val modifyMesh = obj.mesh.let {
                            val newFaces = it.faces.mapIndexedNotNull { index, iFaceIndex ->
                                val ref = FaceRef(objIndex, index)
                                if (selection.isSelected(ref)) null else iFaceIndex
                            }
                            if (newFaces == it.faces) it else Mesh(it.pos, it.tex, newFaces)
                        }

                        if (modifyMesh.faces.isNotEmpty()) {
                            edited += ObjectRef(objIndex) to obj.copy(mesh = modifyMesh)
                        } else {
                            toRemove += ObjectRef(objIndex)
                        }

                    } else if (obj is ObjectCube) {
                        val modifyMesh = obj.mesh.let {
                            val newFaces = it.faces.mapIndexedNotNull { index, iFaceIndex ->
                                val ref = FaceRef(objIndex, index)
                                if (selection.isSelected(ref)) null else iFaceIndex
                            }
                            if (newFaces == it.faces) it else Mesh(it.pos, it.tex, newFaces)
                        }

                        if (modifyMesh.faces.isNotEmpty()) {
                            edited += ObjectRef(objIndex) to obj.transformer.withMesh(obj, modifyMesh)
                        } else {
                            toRemove += ObjectRef(objIndex)
                        }
                    }
                }
                source.modifyObjects(edited.keys.toList()) { ref, _ -> edited[ref]!! }
            }
            SelectionType.EDGE, SelectionType.VERTEX -> source
        }
    }
//    fun deleteElements(source: IModel, selection: List<ObjectSelection>): IModel {
//        return source.applyElementLeaves(selection) { path, elem -> null }
//    }
//
//    fun deleteFaces(source: IModel, selection: VertexPosSelection): IModel {
//        val handler = selection.subPathHandler
//        if (handler is SubSelectionFace) {
//            return source.applyElementLeaves(selection.toElementSelection()) { path, elem ->
//                val facesToRemove = handler.paths.filter { it.elementPath == path }
//                if (facesToRemove.isEmpty()) {
//                    elem
//                } else if (facesToRemove.size == elem.faces.size) {
//                    null
//                } else {
//                    elem.removeFaces(facesToRemove.map { it.faceIndex })
//                }
//            }
//        }
//        return source
//    }
//
//    //
//    // PASTE
//    //
//    fun pasteElement(currentModel: Model, oldModel: Model, oldSelection: ElementSelection): Model {
//        val newElements = oldSelection.paths.map { oldModel.getElement(it) }
//        var model = currentModel
//        for (elem in newElements) {
//            model = insertElement(model, elem)
//        }
//        return model
//    }
//
//    fun pasteFaces(currentModel: Model, oldModel: Model, oldSelection: VertexPosSelection): Model {
//        val handler = oldSelection.subPathHandler
//        if (handler is SubSelectionFace) {
//            val newFaces = handler.paths.map { it.toQuad(oldModel) }
//            val pos = newFaces.flatMap { it.vertex.map { it.pos } }.distinct()
//            val tex = newFaces.flatMap { it.vertex.map { it.tex } }.distinct()
//            val faces = newFaces.map {
//                QuadIndex(
//                        it.a.toIndex(pos, tex),
//                        it.b.toIndex(pos, tex),
//                        it.c.toIndex(pos, tex),
//                        it.d.toIndex(pos, tex)
//                )
//            }
//            val element = ElementLeaf(pos, tex, faces)
//            return insertElement(currentModel, element)
//        } else {
//            return currentModel
//        }
//    }
//
//    //
//    // INSERT
//    //
//    fun insertElementLeaf(source: Model, elem: ElementLeaf): Model {
//        val newElem = elem.copy(positions = elem.positions.map { it + insertPosition })
//        return insertElement(source, newElem)
//    }
//
//    fun insertElement(source: Model, elem: IElement, path: ElementPath = insertPath): Model {
//        return source.copy(elements = insert(source.elements, elem, path, 0))
//    }
//
//    private fun insert(list: List<IElement>, elem: IElement, path: ElementPath, level: Int): List<IElement> {
//        if (insertPath.indices.size == level) {
//            return list + elem
//        } else {
//            val group = list[insertPath.indices[level]] as IElementGroup
//            return insert(group.elements, elem, path, level + 1)
//        }
//    }
//
//    //
//    // SPLIT TEXTURE
//    //
//    fun splitTextures(editor: ModelEditor) {
//        if (editor.selectionManager.vertexTexSelection != VertexTexSelection.EMPTY) {
//            val newModel = editor.model.splitUV(editor.selectionManager.vertexTexSelection)
//            editor.historyRecord.doAction(ActionModifyModelShape(editor, newModel))
//        }
//    }
//
//    fun translateTexture(model: Model, selection: VertexTexSelection, vec: IVector2): Model {
//        return model
//    }
}
