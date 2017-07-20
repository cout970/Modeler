package com.cout970.modeler.api.model.`object`

import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/05/14.
 */
interface IObjectCube : IObject {
    val size: IVector3
    val pos: IVector3
    val rotation: IQuaternion

    fun withSize(size: IVector3): IObjectCube
    fun withPos(pos: IVector3): IObjectCube
    fun withRotation(rot: IQuaternion): IObjectCube
}