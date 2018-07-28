package com.cout970.modeler.core.model.`object`

import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.mesh.Mesh
import java.util.*

/**
 * Created by cout970 on 2017/05/07.
 */
data class Object(
        override val name: String,
        override val mesh: IMesh,
        override val material: IMaterialRef = MaterialRefNone,
        override val transformation: ITransformation = TRSTransformation.IDENTITY,
        override val visible: Boolean = true,
        override val id: UUID = UUID.randomUUID()
) : IObject {

    @Suppress("unused")
    private constructor() : this("", Mesh())

    override fun withTransformation(transform: ITransformation): IObject = copy(transformation = transform)

    override fun withVisibility(visible: Boolean): IObject = copy(visible = visible)

    override fun withMesh(newMesh: IMesh): IObject = copy(mesh = newMesh)

    override fun withMaterial(materialRef: IMaterialRef): IObject = copy(material = materialRef)

    override fun withName(name: String): IObject = copy(name = name)

    override fun withId(id: UUID): IObject = copy(id = id)

    override fun makeCopy(): IObject = copy(id = UUID.randomUUID())

}