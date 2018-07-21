package com.cout970.modeler.core.model.`object`

import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.transformer.IObjectTransformer
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.util.toAxisRotations
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import java.util.*


/**
 * Created by cout970 on 2017/05/14.
 */
data class ObjectCube(
        override val name: String,
        override val transformation: ITransformation,
        override val material: IMaterialRef = MaterialRefNone,
        override val textureOffset: IVector2 = Vector2.ORIGIN,
        override val textureSize: IVector2 = vec2Of(64),
        override val visible: Boolean = true,
        val mirrored: Boolean = false,
        override val id: UUID = UUID.randomUUID()
) : IObjectCube {

    override val mesh: IMesh by lazy { generateMesh() }

    val trs
        get() = when (transformation) {
            is TRSTransformation -> transformation.toTRTS()
            is TRTSTransformation -> transformation
            else -> error("Invalid type: $transformation")
        }

    val size: IVector3 get() = trs.scale
    val pos: IVector3 get() = trs.translation

    fun generateMesh(): IMesh {
        val cube = MeshFactory.createCube(Vector3.ONE, Vector3.ORIGIN)
        return updateTextures(Mesh(cube.pos, cube.tex, cube.faces), size, textureOffset, textureSize)
    }

    fun updateTextures(mesh: IMesh, size: IVector3, offset: IVector2, textureSize: IVector2): IMesh {
        val uvs = generateUVs(size, offset, textureSize)
        val newFaces = mesh.faces.mapIndexed { index, face ->
            val faceIndex = face as FaceIndex
            FaceIndex.from(faceIndex.pos, faceIndex.tex.mapIndexed { vertexIndex, _ -> index * 4 + vertexIndex })
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

    override fun withVisibility(visible: Boolean): IObject = copy(visible = visible)

    override fun withSize(size: IVector3): IObjectCube = copy(transformation = trs.copy(scale = size))

    override fun withPos(pos: IVector3): IObjectCube = copy(transformation = trs.copy(translation = pos))

    override fun withTransformation(transform: ITransformation): IObjectCube = copy(transformation = transform)

    override fun withTextureOffset(tex: IVector2): IObjectCube = copy(textureOffset = tex)

    override fun withTextureSize(size: IVector2): IObjectCube = copy(textureSize = size)

    override fun withMesh(newMesh: IMesh): IObject = Object(name, newMesh, material, TRSTransformation.IDENTITY, visible, id)

    override fun withMaterial(materialRef: IMaterialRef): IObject = copy(material = materialRef)

    override fun withName(name: String): IObject = copy(name = name)

    override fun withId(id: UUID): IObject = copy(id = id)

    override fun makeCopy(): IObjectCube = copy(id = UUID.randomUUID())

    override val transformer: IObjectTransformer = object : IObjectTransformer {

        override fun translate(obj: IObject, translation: IVector3): IObject {
            val newPos = pos + translation
            return copy(transformation = trs.copy(translation = newPos))
        }

        override fun rotate(obj: IObject, pivot: IVector3, rot: IQuaternion): IObject {
            val newRot = TRSTransformation.fromRotationPivot(pivot, rot.toAxisRotations())
            return copy(transformation = transformation + newRot)
        }

        override fun scale(obj: IObject, center: IVector3, axis: IVector3, offset: Float): IObject {
            val newSize = size + axis * offset
            return copy(transformation = trs.copy(scale = newSize))
        }

        override fun translateTexture(obj: IObject, translation: IVector2): IObject {
            val newOffset = textureOffset + translation * textureSize
            return copy(textureOffset = newOffset)
        }

        override fun rotateTexture(obj: IObject, center: IVector2, angle: Double): IObject {
            return obj
        }

        override fun scaleTexture(obj: IObject, center: IVector2, axis: IVector2, offset: Float): IObject {
            val newSize = textureSize + axis * offset
            return copy(textureSize = newSize)
        }
    }
}