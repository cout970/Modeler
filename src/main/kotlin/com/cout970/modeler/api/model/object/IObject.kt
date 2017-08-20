package com.cout970.modeler.api.model.`object`

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/05/07.
 */
interface IObject {

    val name: String
    val mesh: IMesh
    val material: IMaterialRef

    val transformer: IObjectTransformer

    fun getCenter(): IVector3
}