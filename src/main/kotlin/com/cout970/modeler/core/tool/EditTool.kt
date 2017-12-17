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
import com.cout970.modeler.util.scale
import com.cout970.modeler.util.toAxisRotations
import com.cout970.modeler.util.toJOML
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.toVector3
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import org.joml.Matrix4d
import org.joml.Vector4d

/**
 * Created by cout970 on 2017/02/11.
 */
object EditTool {

    //
    // TRANSFORM
    //
    fun translate(source: IModel, sel: ISelection, translation: IVector3): IModel {
        val matrix = TRSTransformation(translation).matrix.toJOML()
        val transform = { it: IVector3 -> matrix.transformVertex(it) }
        return when (sel.selectionType) {
            SelectionType.OBJECT -> source.modifyObjects({ sel.isSelected(it) }) { _, it ->
                it.transformer.translate(it, translation)
            }
            SelectionType.FACE -> transformFaces(source, sel, transform)
            SelectionType.EDGE -> transformEdges(source, sel, transform)
            SelectionType.VERTEX -> transformVertex(source, sel, transform)
        }
    }

    fun translateTexture(source: IModel, sel: ISelection, translation: IVector2): IModel {
        val matrix = TRSTransformation(translation.toVector3(0.0)).matrix.toJOML()
        val transform = { it: IVector2 -> matrix.transformVertex(it) }
        return when (sel.selectionType) {
            SelectionType.OBJECT -> source.modifyObjects({ sel.isSelected(it) }) { _, it ->
                it.transformer.translateTexture(it, translation)
            }
            SelectionType.FACE -> transformTextureFaces(source, sel, transform)
            SelectionType.EDGE -> transformTextureEdges(source, sel, transform)
            SelectionType.VERTEX -> transformTextureVertex(source, sel, transform)
        }
    }

    fun rotate(source: IModel, sel: ISelection, pivot: IVector3, rotation: IQuaternion): IModel {
        val matrix = TRSTransformation.fromRotationPivot(pivot, rotation.toAxisRotations()).matrix.toJOML()
        val transform = { it: IVector3 -> matrix.transformVertex(it) }
        return when (sel.selectionType) {
            SelectionType.OBJECT -> source.modifyObjects({ sel.isSelected(it) }) { _, it ->
                it.transformer.rotate(it, pivot, rotation)
            }
            SelectionType.FACE -> transformFaces(source, sel, transform)
            SelectionType.EDGE -> transformEdges(source, sel, transform)
            SelectionType.VERTEX -> transformVertex(source, sel, transform)
        }
    }

    fun rotateTexture(source: IModel, sel: ISelection, pivot: IVector2, rotation: Double): IModel {
        val matrix = TRSTransformation.fromRotationPivot(pivot.toVector3(0.0), vec3Of(0.0, 0.0, rotation))
                .matrix.toJOML()
        val transform = { it: IVector2 -> matrix.transformVertex(it) }
        return when (sel.selectionType) {
            SelectionType.OBJECT -> source.modifyObjects({ sel.isSelected(it) }) { _, it ->
                it.transformer.rotateTexture(it, pivot, rotation)
            }
            SelectionType.FACE -> transformTextureFaces(source, sel, transform)
            SelectionType.EDGE -> transformTextureEdges(source, sel, transform)
            SelectionType.VERTEX -> transformTextureVertex(source, sel, transform)
        }
    }

    fun scale(source: IModel, sel: ISelection, center: IVector3, axis: IVector3, offset: Float): IModel {
        val transform = { it: IVector3 -> it.scale(center, axis, offset) }
        return when (sel.selectionType) {
            SelectionType.OBJECT -> source.modifyObjects({ sel.isSelected(it) }) { _, it ->
                it.transformer.scale(it, center, axis, offset)
            }
            SelectionType.FACE -> transformFaces(source, sel, transform)
            SelectionType.EDGE -> transformEdges(source, sel, transform)
            SelectionType.VERTEX -> transformVertex(source, sel, transform)
        }
    }

    fun scaleTexture(source: IModel, sel: ISelection, center: IVector2, axis: IVector2, offset: Float): IModel {
        val transform = { it: IVector2 -> it.scale(center, axis, offset) }
        return when (sel.selectionType) {
            SelectionType.OBJECT -> source.modifyObjects({ sel.isSelected(it) }) { _, it ->
                it.transformer.scaleTexture(it, center, axis, offset)
            }
            SelectionType.FACE -> transformTextureFaces(source, sel, transform)
            SelectionType.EDGE -> transformTextureEdges(source, sel, transform)
            SelectionType.VERTEX -> transformTextureVertex(source, sel, transform)
        }
    }

    fun transformFaces(source: IModel, sel: ISelection, transform: (IVector3) -> IVector3): IModel {
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

    fun transformEdges(source: IModel, sel: ISelection, transform: (IVector3) -> IVector3): IModel {
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

    fun transformVertex(source: IModel, sel: ISelection, transform: (IVector3) -> IVector3): IModel {
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

    fun transformTextureFaces(source: IModel, sel: ISelection, transform: (IVector2) -> IVector2): IModel {
        val objRefs = sel.refs.filterIsInstance<IFaceRef>().map { ObjectRef(it.objectIndex) }.distinct()
        return source.modifyObjects(objRefs) { ref, obj ->
            val indices: Set<Int> = sel.refs
                    .filterIsInstance<IFaceRef>()
                    .filter { it.objectIndex == ref.objectIndex }
                    .map { obj.mesh.faces[it.faceIndex] }
                    .flatMap { it.pos }
                    .toSet()

            val newMesh = transformMeshTexture(obj, indices, transform)
            obj.withMesh(newMesh)
        }
    }

    fun transformTextureEdges(source: IModel, sel: ISelection, transform: (IVector2) -> IVector2): IModel {
        val objRefs = sel.refs.filterIsInstance<IEdgeRef>().map { ObjectRef(it.objectIndex) }.distinct()
        return source.modifyObjects(objRefs) { ref, obj ->
            val indices: Set<Int> = sel.refs
                    .filterIsInstance<IEdgeRef>()
                    .filter { it.objectIndex == ref.objectIndex }
                    .flatMap { listOf(it.firstIndex, it.secondIndex) }
                    .toSet()

            val newMesh = transformMeshTexture(obj, indices, transform)
            obj.withMesh(newMesh)
        }
    }

    fun transformTextureVertex(source: IModel, sel: ISelection, transform: (IVector2) -> IVector2): IModel {
        val objRefs = sel.refs.filterIsInstance<IPosRef>().map { ObjectRef(it.objectIndex) }.distinct()
        return source.modifyObjects(objRefs) { ref, obj ->
            val indices: Set<Int> = sel.refs
                    .filterIsInstance<IPosRef>()
                    .filter { it.objectIndex == ref.objectIndex }
                    .map { it.posIndex }
                    .toSet()

            val newMesh = transformMeshTexture(obj, indices, transform)
            obj.withMesh(newMesh)
        }
    }

    fun transformMesh(obj: IObject, indices: Set<Int>, transform: (IVector3) -> IVector3): IMesh {

        val newPos: List<IVector3> = obj.mesh.pos.mapIndexed { index, it ->
            if (index in indices) transform(it) else it
        }
        return Mesh(pos = newPos, tex = obj.mesh.tex, faces = obj.mesh.faces)
    }

    fun transformMeshTexture(obj: IObject, indices: Set<Int>, transform: (IVector2) -> IVector2): IMesh {

        val newTex: List<IVector2> = obj.mesh.tex.mapIndexed { index, it ->
            if (index in indices) transform(it) else it
        }
        return Mesh(pos = obj.mesh.pos, tex = newTex, faces = obj.mesh.faces)
    }

    fun Matrix4d.transformVertex(it: IVector3): IVector3 {
        val vec4 = transform(Vector4d(it.xd, it.yd, it.zd, 1.0))
        return vec3Of(vec4.x, vec4.y, vec4.z)
    }

    fun Matrix4d.transformVertex(it: IVector2): IVector2 {
        val vec4 = transform(Vector4d(it.xd, it.yd, 0.0, 1.0))
        return vec2Of(vec4.x, vec4.y)
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
