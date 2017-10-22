package com.cout970.modeler.core.tool

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.core.model.Object
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.selection.FaceRef
import com.cout970.modeler.core.model.selection.ObjectRef
import com.cout970.modeler.util.toAxisRotations
import com.cout970.modeler.util.toJOML
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times
import com.cout970.vector.extensions.vec3Of
import org.joml.Vector4d

/**
 * Created by cout970 on 2017/02/11.
 */
object EditTool {

    //
    // TRANSFORM
    //
    fun translate(source: IModel, sel: ISelection, translation: IVector3): IModel = when (sel.selectionType) {
        SelectionType.OBJECT -> source.modifyObjects({ sel.isSelected(it) }) { _, it ->
            it.transformer.translate(it, translation)
        }
        SelectionType.FACE -> transformFaces(source, sel, TRSTransformation(translation))
        SelectionType.EDGE -> transformEdges(source, sel, TRSTransformation(translation))
        SelectionType.VERTEX -> transformVertex(source, sel, TRSTransformation(translation))
    }


    fun rotate(source: IModel, sel: ISelection, pivot: IVector3, rotation: IQuaternion): IModel {
        val transform = TRSTransformation.fromRotationPivot(pivot, rotation.toAxisRotations())
        return when (sel.selectionType) {
            SelectionType.OBJECT -> source.modifyObjects({ sel.isSelected(it) }) { _, it ->
                it.transformer.rotate(it, pivot, rotation)
            }
            SelectionType.FACE -> transformFaces(source, sel, transform)
            SelectionType.EDGE -> transformEdges(source, sel, transform)
            SelectionType.VERTEX -> transformVertex(source, sel, transform)
        }
    }

    fun scale(source: IModel, sel: ISelection, center: IVector3, axis: IVector3, offset: Float): IModel {
        val transform = TRSTransformation(scale = Vector3.ONE + axis * offset)
        return when (sel.selectionType) {
            SelectionType.OBJECT -> source.modifyObjects({ sel.isSelected(it) }) { _, it ->
                it.transformer.scale(it, center, axis, offset)
            }
            SelectionType.FACE -> transformFaces(source, sel, transform)
            SelectionType.EDGE -> transformEdges(source, sel, transform)
            SelectionType.VERTEX -> transformVertex(source, sel, transform)
        }
    }

    fun transformFaces(source: IModel, sel: ISelection, transform: TRSTransformation): IModel {
        val objRefs = sel.refs.filterIsInstance<IFaceRef>().map { ObjectRef(it.objectIndex) }.distinct()
        return source.modifyObjects(objRefs) { ref, obj ->
            val indices: Set<Int> = sel.refs
                    .filterIsInstance<IFaceRef>()
                    .filter { it.objectIndex == ref.objectIndex }
                    .map { obj.mesh.faces[it.faceIndex] }
                    .flatMap { it.pos }
                    .toSet()

            val newMesh = transformMesh(obj, indices, transform)
            obj.withMesh(newMesh)
        }
    }

    fun transformEdges(source: IModel, sel: ISelection, transform: TRSTransformation): IModel {
        val objRefs = sel.refs.filterIsInstance<IEdgeRef>().map { ObjectRef(it.objectIndex) }.distinct()
        return source.modifyObjects(objRefs) { ref, obj ->
            val indices: Set<Int> = sel.refs
                    .filterIsInstance<IEdgeRef>()
                    .filter { it.objectIndex == ref.objectIndex }
                    .flatMap { listOf(it.firstIndex, it.secondIndex) }
                    .toSet()

            val newMesh = transformMesh(obj, indices, transform)
            obj.withMesh(newMesh)
        }
    }

    fun transformVertex(source: IModel, sel: ISelection, transform: TRSTransformation): IModel {
        val objRefs = sel.refs.filterIsInstance<IPosRef>().map { ObjectRef(it.objectIndex) }.distinct()
        return source.modifyObjects(objRefs) { ref, obj ->
            val indices: Set<Int> = sel.refs
                    .filterIsInstance<IPosRef>()
                    .filter { it.objectIndex == ref.objectIndex }
                    .map { it.posIndex }
                    .toSet()

            val newMesh = transformMesh(obj, indices, transform)
            obj.withMesh(newMesh)
        }
    }

    fun transformMesh(obj: IObject, indices: Set<Int>, transform: TRSTransformation): IMesh {
        val matrix = transform.matrix.toJOML()

        val newPos: List<IVector3> = obj.mesh.pos.mapIndexed { index, it ->
            if (index in indices) {
                val vec4 = matrix.transform(Vector4d(it.xd, it.yd, it.zd, 1.0))
                vec3Of(vec4.x, vec4.y, vec4.z)
            } else it
        }
        return Mesh(pos = newPos, tex = obj.mesh.tex, faces = obj.mesh.faces)
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
                            edited += ObjectRef(objIndex) to obj.withMesh(modifyMesh)
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
