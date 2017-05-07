package com.cout970.modeler.api.model

import com.cout970.modeler.api.model.mesh.IMesh

/**
 * Created by cout970 on 2017/05/07.
 */
interface IObject {

    val mesh: IMesh
    val transformation: ITransformation
    val transformedMesh: IMesh
}