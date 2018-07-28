package com.cout970.modeler.api.model.`object`

import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import java.util.*

/**
 * Created by cout970 on 2017/05/07.
 */
interface IObject {

    val id: UUID
    val name: String
    val mesh: IMesh
    val transformation: ITransformation
    val material: IMaterialRef
    val visible: Boolean

    fun makeCopy(): IObject

    fun withTransformation(transform: ITransformation): IObject
    fun withVisibility(visible: Boolean): IObject
    fun withMesh(newMesh: IMesh): IObject
    fun withMaterial(materialRef: IMaterialRef): IObject
    fun withName(name: String): IObject
    fun withId(id: UUID): IObject
}