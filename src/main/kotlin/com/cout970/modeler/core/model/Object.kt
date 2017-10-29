package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.transformer.IObjectTransformer
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.util.middle
import com.cout970.modeler.util.scale
import com.cout970.modeler.util.toAxisRotations
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/05/07.
 */
data class Object(
        override val name: String,
        override val mesh: IMesh,
        override val material: IMaterialRef = MaterialRef(-1)
) : IObject {

    @Suppress("unused")
    private constructor() : this("", Mesh())

    override fun getCenter(): IVector3 = mesh.middle()

    override fun withMesh(newMesh: IMesh): IObject = copy(mesh = newMesh)

    override fun withMaterial(materialRef: IMaterialRef): IObject = copy(material = materialRef)

    override fun withName(name: String): IObject = copy(name = name)

    override val transformer: IObjectTransformer = object : IObjectTransformer {
        override fun translate(obj: IObject, translation: IVector3): IObject {
            return copy(mesh = mesh.transform(TRSTransformation(translation)))
        }

        override fun rotate(obj: IObject, pivot: IVector3, rot: IQuaternion): IObject {
            return copy(mesh = mesh.transform(TRSTransformation.fromRotationPivot(pivot, rot.toAxisRotations())))
        }

        override fun scale(obj: IObject, center: IVector3, axis: IVector3, offset: Float): IObject {
            return copy(mesh = Mesh(mesh.pos.map { it.scale(center, axis, offset) }, mesh.tex, mesh.faces))
        }
    }
}