package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.model.material.IMaterial
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3

/**
 * Created by cout970 on 2017/05/07.
 */
data class Object(
        override val name: String,
        override val mesh: IMesh,
        override val transformation: ITransformation = TRSTransformation.IDENTITY,
        override val material: IMaterial = MaterialNone
) : IObject {

    override val transformedMesh: IMesh by lazy { mesh.transform(transformation) }

    override fun withMesh(newMesh: IMesh): IObject {
        return copy(mesh = newMesh)
    }

    //TODO
    override fun getCenter(): IVector3 = Vector3.ORIGIN

    override fun translate(translation: IVector3): IObject = copy(
            mesh = this.mesh.transform(TRSTransformation(translation)))

    override fun rotate(pivot: IVector3, rot: IQuaternion): IObject = this

    override fun scale(center: IVector3, axis: IVector3, offset: Float): IObject = this
}