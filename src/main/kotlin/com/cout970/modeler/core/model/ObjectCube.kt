package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.api.model.IObjectCube
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.model.material.IMaterial
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.util.rotateAround
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/05/14.
 */
data class ObjectCube(
        override val name: String,

        override val pos: IVector3,
        override val rotation: IQuaternion,
        override val size: IVector3,
        override val transformation: ITransformation = TRSTransformation.IDENTITY,
        override val material: IMaterial = MaterialNone,

        val rotationPivot: IVector3 = Vector3.ORIGIN,
        val textureOffset: IVector2 = Vector2.ORIGIN,
        val textureSize: IVector2 = vec2Of(64),
        val mirrored: Boolean = false
) : IObjectCube {

    override val mesh: IMesh by lazy { generateMesh() }
    override val transformedMesh: IMesh by lazy { mesh.transform(transformation) }

    fun generateMesh(): IMesh {
        val cube = MeshFactory.createCube(size, pos)
        val pos = cube.pos.map { it.rotateAround(rotationPivot, rotation) }
        return updateTextures(Mesh(pos, cube.tex, cube.faces), size, textureOffset, textureSize)
    }

    override fun transform(func: (IMesh) -> IMesh): IObject {
        return Object(name, func(mesh), transformation, material)
    }

//    fun Quad.setTexture0(uv0: IVector2, uv1: IVector2): Quad {
//        return Quad(
//                a.copy(tex = vec2Of(uv1.x, uv0.y)),
//                b.copy(tex = vec2Of(uv0.x, uv0.y)),
//                c.copy(tex = vec2Of(uv0.x, uv1.y)),
//                d.copy(tex = vec2Of(uv1.x, uv1.y))
//        )
//    }
//
//    fun Quad.setTexture1(uv0: IVector2, uv1: IVector2): Quad {
//        return Quad(
//                a.copy(tex = vec2Of(uv1.x, uv1.y)),
//                b.copy(tex = vec2Of(uv1.x, uv0.y)),
//                c.copy(tex = vec2Of(uv0.x, uv0.y)),
//                d.copy(tex = vec2Of(uv0.x, uv1.y))
//        )
//    }

//    fun ElementLeaf.setUVFromCuboid(size: IVector3, offset: IVector2, textureSize: IVector2): ElementLeaf {
//        val uvs = generateUVs(size, offset, textureSize)
//        return Meshes.quadsToMesh(getQuads().mapIndexed { index, quad ->
//            val flag = when (index) {
//                0 -> false; 1 -> true
//                2 -> true; 3 -> false
//                4 -> false; 5 -> true
//                else -> false
//            }
//            if (flag) {
//                quad.setTexture0(uvs[index * 2], uvs[index * 2 + 1])
//            } else {
//                quad.setTexture1(uvs[index * 2], uvs[index * 2 + 1])
//            }
//        })
//    }

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

    //TODO
    override fun translate(translation: IVector3): IObject = this

    override fun rotate(pivot: IVector3, rot: IQuaternion): IObject = this

    override fun scale(center: IVector3, axis: IVector3, offset: Float): IObject = this
}