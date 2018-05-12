package com.cout970.modeler.api.model.`object`

import com.cout970.modeler.api.model.ITransformation
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/05/14.
 */
interface IObjectCube : IObject {
    val transformation: ITransformation
    val textureOffset: IVector2
    val textureSize: IVector2

    override fun makeCopy(): IObjectCube

    fun withSize(size: IVector3): IObjectCube
    fun withPos(pos: IVector3): IObjectCube

    fun withTransformation(transform: ITransformation): IObjectCube
    fun withTextureOffset(tex: IVector2): IObjectCube
    fun withTextureSize(size: IVector2): IObjectCube
}