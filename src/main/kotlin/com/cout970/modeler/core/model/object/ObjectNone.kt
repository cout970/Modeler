package com.cout970.modeler.core.model.`object`

import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.transformer.IObjectTransformer
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector2
import java.util.*

/**
 * Created by cout970 on 2017/07/09.
 */
object ObjectNone : IObject {
    override val id: UUID = UUID.fromString("89672293-60d2-46ea-9b56-46c624dec60a")
    override val name: String = "none"
    override val mesh: IMesh = Mesh()
    override val transformation: ITransformation = TRSTransformation.IDENTITY
    override val material: IMaterialRef = MaterialRefNone
    override val visible: Boolean = true

    override fun withTransformation(transform: ITransformation): IObject = this

    override fun withVisibility(visible: Boolean): IObject = this

    override fun makeCopy(): IObject = ObjectNone

    override fun withMesh(newMesh: IMesh): IObject = this

    override fun withMaterial(materialRef: IMaterialRef): IObject = this

    override fun withName(name: String): IObject = this

    override fun withId(id: UUID): IObject = this

    override val transformer: IObjectTransformer = object : IObjectTransformer {
        override fun translate(obj: IObject, translation: IVector3): IObject = this@ObjectNone
        override fun rotate(obj: IObject, pivot: IVector3, rot: IQuaternion): IObject = this@ObjectNone
        override fun scale(obj: IObject, center: IVector3, axis: IVector3, offset: Float): IObject = this@ObjectNone

        override fun translateTexture(obj: IObject, translation: IVector2): IObject = this@ObjectNone
        override fun rotateTexture(obj: IObject, center: IVector2, angle: Double): IObject = this@ObjectNone
        override fun scaleTexture(obj: IObject, center: IVector2, axis: IVector2,
                                  offset: Float): IObject = this@ObjectNone
    }

}

object ObjectCubeNone : IObjectCube {
    override val id: UUID = UUID.fromString("89672293-60d2-46ea-9b56-46c624dec60a")
    override val name: String = "none"
    override val mesh: IMesh = Mesh()
    override val material: IMaterialRef = MaterialRefNone
    override val transformation: TRSTransformation = TRSTransformation.IDENTITY
    override val textureOffset: IVector2 = Vector2.ORIGIN
    override val textureSize: IVector2 = Vector2.ONE
    override val visible: Boolean = true

    override fun withVisibility(visible: Boolean): IObject = this

    override fun makeCopy(): IObjectCube = this

    override fun withSize(size: IVector3): IObjectCube = this

    override fun withPos(pos: IVector3): IObjectCube = this

    override fun withTransformation(transform: ITransformation): IObjectCube = this

    override fun withTextureOffset(tex: IVector2): IObjectCube = this

    override fun withTextureSize(size: IVector2): IObjectCube = this

    override fun withMesh(newMesh: IMesh): IObject = this

    override fun withMaterial(materialRef: IMaterialRef): IObject = this

    override fun withName(name: String): IObject = this

    override fun withId(id: UUID): IObject = this

    override val transformer: IObjectTransformer = object : IObjectTransformer {
        override fun translate(obj: IObject, translation: IVector3): IObject = this@ObjectCubeNone
        override fun rotate(obj: IObject, pivot: IVector3, rot: IQuaternion): IObject = this@ObjectCubeNone
        override fun scale(obj: IObject, center: IVector3, axis: IVector3, offset: Float): IObject = this@ObjectCubeNone

        override fun translateTexture(obj: IObject, translation: IVector2): IObject = this@ObjectCubeNone
        override fun rotateTexture(obj: IObject, center: IVector2, angle: Double): IObject = this@ObjectCubeNone
        override fun scaleTexture(obj: IObject, center: IVector2, axis: IVector2,
                                  offset: Float): IObject = this@ObjectCubeNone
    }

}