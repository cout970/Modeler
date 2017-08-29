package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.`object`.IObjectTransformer
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.util.*
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Quaterniond
import org.joml.Vector4d


/**
 * Created by cout970 on 2017/05/14.
 */
data class ObjectCube(
        override val name: String,

        override val pos: IVector3,
        override val subTransformation: TRTSTransformation,
        override val size: IVector3,

        override val material: IMaterialRef = MaterialRef(-1),

        override val textureOffset: IVector2 = Vector2.ORIGIN,
        override val textureSize: IVector2 = vec2Of(64),

        val mirrored: Boolean = false
) : IObjectCube {

    override val mesh: IMesh by lazy { generateMesh() }

    override fun getCenter(): IVector3 = mesh.middle()//subTransformation.preRotation

    fun generateMesh(): IMesh {
        val cube = MeshFactory.createCube(size, pos)
        val pos = cube.pos.map { Vector4d(it.xd, it.yd, it.zd, 1.0) }
                .map(subTransformation.matrix.toJOML()::transform)
                .map { it.toIVector() }
        return updateTextures(Mesh(pos, cube.tex, cube.faces), size, textureOffset, textureSize)
    }

    fun updateTextures(mesh: IMesh, size: IVector3, offset: IVector2, textureSize: IVector2): IMesh {
        val uvs = generateUVs(size, offset, textureSize)
        val newFaces = mesh.faces.mapIndexed { index, face ->
            val faceIndex = face as FaceIndex
            FaceIndex(faceIndex.index.mapIndexed { vertexIndex, pair ->
                pair.first to index * 4 + vertexIndex
            })
        }
        return Mesh(mesh.pos, uvs, newFaces)
    }

    private fun generateUVs(size: IVector3, offset: IVector2, textureSize: IVector2): List<IVector2> {
        val width = size.xd
        val height = size.yd
        val length = size.zd

        val offsetX = offset.xd
        val offsetY = offset.yd

        val texelSize = vec2Of(1) / textureSize

        return listOf(
                //-y
                vec2Of(offsetX + length + width + width, offsetY + length) * texelSize,
                vec2Of(offsetX + length + width + width, offsetY) * texelSize,
                vec2Of(offsetX + length + width, offsetY) * texelSize,
                vec2Of(offsetX + length + width, offsetY + length) * texelSize,
                //+y
                vec2Of(offsetX + length, offsetY + length) * texelSize,
                vec2Of(offsetX + length + width, offsetY + length) * texelSize,
                vec2Of(offsetX + length + width, offsetY) * texelSize,
                vec2Of(offsetX + length, offsetY) * texelSize,
                //-z
                vec2Of(offsetX + length + width + length + width, offsetY + length) * texelSize,
                vec2Of(offsetX + length + width + length, offsetY + length) * texelSize,
                vec2Of(offsetX + length + width + length, offsetY + length + height) * texelSize,
                vec2Of(offsetX + length + width + length + width, offsetY + length + height) * texelSize,
                //+z
                vec2Of(offsetX + length + width, offsetY + length + height) * texelSize,
                vec2Of(offsetX + length + width, offsetY + length) * texelSize,
                vec2Of(offsetX + length, offsetY + length) * texelSize,
                vec2Of(offsetX + length, offsetY + length + height) * texelSize,
                //-x
                vec2Of(offsetX + length, offsetY + length + height) * texelSize,
                vec2Of(offsetX + length, offsetY + length) * texelSize,
                vec2Of(offsetX, offsetY + length) * texelSize,
                vec2Of(offsetX, offsetY + length + height) * texelSize,
                //+x
                vec2Of(offsetX + length + width + length, offsetY + length) * texelSize,
                vec2Of(offsetX + length + width, offsetY + length) * texelSize,
                vec2Of(offsetX + length + width, offsetY + length + height) * texelSize,
                vec2Of(offsetX + length + width + length, offsetY + length + height) * texelSize
        )
    }

    override fun withSize(size: IVector3): IObjectCube = copy(size = size)

    override fun withPos(pos: IVector3): IObjectCube = copy(pos = pos)

    override fun withSubTransformation(transform: TRTSTransformation): IObjectCube = copy(subTransformation = transform)

    override fun withTextureOffset(tex: IVector2): IObjectCube = copy(textureOffset = tex)

    override val transformer: IObjectTransformer = object : IObjectTransformer {
        override fun withMesh(obj: IObject, newMesh: IMesh): IObject {
            return Object(name, newMesh, material)
        }

        override fun translate(obj: IObject, translation: IVector3): IObject {
            return copy(pos = pos + translation)
        }

        override fun rotate(obj: IObject, pivot: IVector3, rot: IQuaternion): IObject {

            val actual = quatOfAngles(subTransformation.rotation.toRadians()).toJOML()
            val trans = rot.toJOML()
            val comb = actual.mul(trans, Quaterniond())

            return copy(subTransformation = subTransformation.copy(
                    rotation = comb.toIQuaternion().toAxisRotations()
            ))
//            val other = TRTSTransformation.fromRotationPivot(pivot, rot)
//            return copy(subTransformation = subTransformation.merge(other))
        }

        override fun scale(obj: IObject, center: IVector3, axis: IVector3, offset: Float): IObject {
            val newSize = size + axis * offset
            return copy(size = newSize)
        }

        override fun withMaterial(obj: IObject, materialRef: IMaterialRef): IObject {
            return copy(material = materialRef)
        }
    }
}