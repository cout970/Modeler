package com.cout970.modeler.api.model

import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.core.model.material.IMaterial
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/05/07.
 */
interface IObject {

    val name: String
    val mesh: IMesh
    val transformation: ITransformation
    val material: IMaterial

    val transformedMesh: IMesh

    fun transform(func: (IMesh) -> IMesh): IObject

    fun translate(translation: IVector3): IObject
    fun rotate(pivot: IVector3, rot: IQuaternion): IObject
    fun scale(center: IVector3, axis: IVector3, offset: Float): IObject
}