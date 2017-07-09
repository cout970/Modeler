package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.IObjectTransformer
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3

/**
 * Created by cout970 on 2017/07/09.
 */
object ObjectNone : IObject {
    override val name: String = "none"
    override val mesh: IMesh = Mesh()
    override val transformation: ITransformation = TRSTransformation()
    override val material: IMaterialRef = MaterialRef(-1)
    override val transformedMesh: IMesh = mesh

    override val transformer: IObjectTransformer = object : IObjectTransformer {
        override fun withMesh(obj: IObject, newMesh: IMesh): IObject = this@ObjectNone
        override fun translate(obj: IObject, translation: IVector3): IObject = this@ObjectNone
        override fun rotate(obj: IObject, pivot: IVector3, rot: IQuaternion): IObject = this@ObjectNone
        override fun scale(obj: IObject, center: IVector3, axis: IVector3, offset: Float): IObject = this@ObjectNone
    }

    override fun getCenter(): IVector3 = Vector3.ORIGIN
}