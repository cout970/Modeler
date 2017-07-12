package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.`object`.IObjectTransformer
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.util.middle
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/05/07.
 */
data class Object(
        override val name: String,
        override val mesh: IMesh,
        override val transformation: ITransformation = TRSTransformation.IDENTITY,
        override val material: IMaterialRef = MaterialRef(-1)
) : IObject {

    override val transformedMesh: IMesh by lazy { mesh.transform(transformation) }
    override fun getCenter(): IVector3 = transformedMesh.middle()

    override val transformer: IObjectTransformer = object : IObjectTransformer {
        override fun withMesh(obj: IObject, newMesh: IMesh): IObject {
            return copy(mesh = newMesh)
        }

        override fun translate(obj: IObject, translation: IVector3): IObject {
            return copy(mesh = mesh.transform(TRSTransformation(translation)))
        }

        override fun rotate(obj: IObject, pivot: IVector3, rot: IQuaternion): IObject {
            return this@Object
        }

        override fun scale(obj: IObject, center: IVector3, axis: IVector3, offset: Float): IObject {
            return this@Object
        }

        override fun withMaterial(obj: IObject, materialRef: IMaterialRef): IObject {
            return copy(material = materialRef)
        }
    }
}