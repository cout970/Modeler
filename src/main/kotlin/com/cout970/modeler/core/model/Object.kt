package com.cout970.modeler.core.model

import com.cout970.matrix.extensions.times
import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.mesh.IMesh

/**
 * Created by cout970 on 2017/05/07.
 */
class Object(
        val name: String,
        override val mesh: IMesh,
        override val transformation: ITransformation
) : IObject {

    override val transformedMesh: IMesh by lazy {
        mesh.transformPos(mesh.pos.indices.toList()) { _, pos -> transformation.matrix * pos }
    }
}