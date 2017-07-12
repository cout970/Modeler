package com.cout970.modeler.api.model.`object`

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/07/09.
 */
interface IObjectTransformer {

    fun withMesh(obj: IObject, newMesh: IMesh): IObject
    fun translate(obj: IObject, translation: IVector3): IObject
    fun rotate(obj: IObject, pivot: IVector3, rot: IQuaternion): IObject
    fun scale(obj: IObject, center: IVector3, axis: IVector3, offset: Float): IObject
    fun withMaterial(obj: IObject, materialRef: IMaterialRef): IObject
}