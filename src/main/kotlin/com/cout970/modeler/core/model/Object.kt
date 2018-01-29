package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.transformer.IObjectTransformer
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.util.middle
import com.cout970.modeler.util.scale
import com.cout970.modeler.util.toAxisRotations
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.toVector3
import com.cout970.vector.extensions.vec3Of
import java.util.*

/**
 * Created by cout970 on 2017/05/07.
 */
data class Object(
        override val name: String,
        override val mesh: IMesh,
        override val material: IMaterialRef = MaterialRefNone,
        override val visible: Boolean = true,
        override val id: UUID = UUID.randomUUID()
) : IObject {

    @Suppress("unused")
    private constructor() : this("", Mesh())

    override fun getCenter(): IVector3 = mesh.middle()

    override fun withVisibility(visible: Boolean): IObject = copy(visible = visible)

    override fun withMesh(newMesh: IMesh): IObject = copy(mesh = newMesh)

    override fun withMaterial(materialRef: IMaterialRef): IObject = copy(material = materialRef)

    override fun withName(name: String): IObject = copy(name = name)

    override fun makeCopy(): IObject = copy(id = UUID.randomUUID())

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

        override fun translateTexture(obj: IObject, translation: IVector2): IObject {
            return copy(mesh = mesh.transformTexture(TRSTransformation(translation.toVector3(0.0))))
        }

        override fun rotateTexture(obj: IObject, center: IVector2, angle: Double): IObject {
            val trans = TRSTransformation.fromRotationPivot(center.toVector3(0.0), vec3Of(0.0, 0.0, 1.0))
            return copy(mesh = mesh.transformTexture(trans))
        }

        override fun scaleTexture(obj: IObject, center: IVector2, axis: IVector2, offset: Float): IObject {
            return copy(mesh = Mesh(mesh.pos, mesh.tex.map { it.scale(center, axis, offset) }, mesh.faces))
        }
    }
}