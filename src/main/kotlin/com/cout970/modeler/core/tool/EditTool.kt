package com.cout970.modeler.core.tool

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.core.model.*
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.mesh.getTextureVertex
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
        val objRefs = sel.faces.map { ObjectRef(it.objectId) }.toSet()
        return source.modifyObjects(objRefs) { ref, obj ->
            val indices: Set<Int> = sel.faces
                    .filter { it.objectId == ref.objectId }
                    .map { obj.mesh.faces[it.faceIndex] }
                    .flatMap { it.pos }
                    .toSet()

            val newMesh = transformMesh(obj, indices, transform)
            obj.withMesh(newMesh)
        }
    }

    fun transformEdges(source: IModel, sel: ISelection, transform: (IVector3) -> IVector3): IModel {
        val objRefs = sel.edges.map { ObjectRef(it.objectId) }.toSet()
        return source.modifyObjects(objRefs) { ref, obj ->
            val indices: Set<Int> = sel.edges
                    .filter { it.objectId == ref.objectId }
                    .flatMap { listOf(it.firstIndex, it.secondIndex) }
                    .toSet()

            val newMesh = transformMesh(obj, indices, transform)
            obj.withMesh(newMesh)
        }
    }

    fun transformVertex(source: IModel, sel: ISelection, transform: (IVector3) -> IVector3): IModel {
        val objRefs = sel.pos.map { ObjectRef(it.objectId) }.toSet()
        return source.modifyObjects(objRefs) { ref, obj ->
            val indices: Set<Int> = sel.pos
                    .filter { it.objectId == ref.objectId }
                    .map { it.posIndex }
                    .toSet()

            val newMesh = transformMesh(obj, indices, transform)
            obj.withMesh(newMesh)
        }
    }

    fun transformTextureFaces(source: IModel, sel: ISelection, transform: (IVector2) -> IVector2): IModel {
        val objRefs = sel.faces.map { ObjectRef(it.objectId) }.toSet()
        return source.modifyObjects(objRefs) { ref, obj ->
            val indices: Set<Int> = sel.faces
                    .filter { it.objectId == ref.objectId }
                    .map { obj.mesh.faces[it.faceIndex] }
                    .flatMap { it.tex }
                    .toSet()

            val newMesh = transformMeshTexture(obj, indices, transform)
            obj.withMesh(newMesh)
        }
    }

    fun transformTextureEdges(source: IModel, sel: ISelection, transform: (IVector2) -> IVector2): IModel {
        val objRefs = sel.edges.map { ObjectRef(it.objectId) }.toSet()
        return source.modifyObjects(objRefs) { ref, obj ->
            val indices: Set<Int> = sel.edges
                    .filter { it.objectId == ref.objectId }
                    .flatMap { listOf(it.firstIndex, it.secondIndex) }
                    .toSet()

            val newMesh = transformMeshTexture(obj, indices, transform)
            obj.withMesh(newMesh)
        }
    }

    fun transformTextureVertex(source: IModel, sel: ISelection, transform: (IVector2) -> IVector2): IModel {
        val objRefs = sel.pos.map { ObjectRef(it.objectId) }.toSet()
        return source.modifyObjects(objRefs) { ref, obj ->
            val indices: Set<Int> = sel.pos
                    .filter { it.objectId == ref.objectId }
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

                source.objectMap.forEach { objIndex, obj ->
                    if (obj is Object) {
                        val modifyMesh = obj.mesh.let {
                            val newFaces = it.faces.mapIndexedNotNull { index, iFaceIndex ->
                                val ref = objIndex.toFaceRef(index)
                                if (selection.isSelected(ref)) null else iFaceIndex
                            }
                            if (newFaces == it.faces) it else Mesh(it.pos, it.tex, newFaces)
                        }

                        if (modifyMesh.faces.isNotEmpty()) {
                            edited += objIndex to obj.copy(mesh = modifyMesh)
                        } else {
                            toRemove += objIndex
                        }

                    } else if (obj is ObjectCube) {
                        val modifyMesh = obj.mesh.let {
                            val newFaces = it.faces.mapIndexedNotNull { index, iFaceIndex ->
                                val ref = objIndex.toFaceRef(index)
                                if (selection.isSelected(ref)) null else iFaceIndex
                            }
                            if (newFaces == it.faces) it else Mesh(it.pos, it.tex, newFaces)
                        }

                        if (modifyMesh.faces.isNotEmpty()) {
                            edited += objIndex to obj.withMesh(modifyMesh)
                        } else {
                            toRemove += objIndex
                        }
                    }
                }
                source.modifyObjects(edited.keys) { ref, _ -> edited[ref]!! }
            }
            SelectionType.EDGE, SelectionType.VERTEX -> source
        }
    }

    fun splitTextures(model: IModel, selection: ISelection): IModel {

        return when (selection.selectionType) {
            SelectionType.OBJECT -> {
                model.modifyObjects(selection::isSelected) { _, obj ->
                    val faces = obj.mesh.faces
                    val pos = obj.mesh.pos

                    val newTex = faces.flatMap { it.getTextureVertex(obj.mesh) }

                    val newFaces = faces.mapIndexed { index, face ->
                        val startIndex = (index * 4)
                        val endIndex = ((index + 1) * 4)
                        val newTexIndices = (startIndex until endIndex).toList()

                        FaceIndex(face.pos, newTexIndices)
                    }

                    val newMesh = Mesh(pos, newTex, newFaces)
                    obj.withMesh(newMesh)
                }
            }
            else -> model
        }
    }
}
