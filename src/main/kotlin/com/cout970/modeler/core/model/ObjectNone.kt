package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.transformer.IObjectTransformer
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.Vector3

/**
 * Created by cout970 on 2017/07/09.
 */
object ObjectNone : IObject {
    override val name: String = "none"
    override val mesh: IMesh = Mesh()
    override val material: IMaterialRef = MaterialRef(-1)

    override fun withMesh(newMesh: IMesh): IObject = this

    override fun withMaterial(materialRef: IMaterialRef): IObject = this

    override fun withName(name: String): IObject = this

    override val transformer: IObjectTransformer = object : IObjectTransformer {
        override fun translate(obj: IObject, translation: IVector3): IObject = this@ObjectNone
        override fun rotate(obj: IObject, pivot: IVector3, rot: IQuaternion): IObject = this@ObjectNone
        override fun scale(obj: IObject, center: IVector3, axis: IVector3, offset: Float): IObject = this@ObjectNone
    }

    override fun getCenter(): IVector3 = Vector3.ORIGIN
}

object ObjectCubeNone : IObjectCube {
    override val name: String = "none"
    override val mesh: IMesh = Mesh()
    override val material: IMaterialRef = MaterialRef(-1)
    override val transformation: TRSTransformation = TRSTransformation.IDENTITY
    override val textureOffset: IVector2 = Vector2.ORIGIN
    override val textureSize: IVector2 = Vector2.ONE

    override fun withSize(size: IVector3): IObjectCube = this

    override fun withPos(pos: IVector3): IObjectCube = this

    override fun withTransformation(transform: TRSTransformation): IObjectCube = this

    override fun withTextureOffset(tex: IVector2): IObjectCube = this

    override fun withTextureSize(size: IVector2): IObjectCube = this

    override fun withMesh(newMesh: IMesh): IObject = this

    override fun withMaterial(materialRef: IMaterialRef): IObject = this

    override fun withName(name: String): IObject = this

    override val transformer: IObjectTransformer = object : IObjectTransformer {
        override fun translate(obj: IObject, translation: IVector3): IObject = this@ObjectCubeNone
        override fun rotate(obj: IObject, pivot: IVector3, rot: IQuaternion): IObject = this@ObjectCubeNone
        override fun scale(obj: IObject, center: IVector3, axis: IVector3, offset: Float): IObject = this@ObjectCubeNone
    }

    override fun getCenter(): IVector3 = Vector3.ORIGIN
}