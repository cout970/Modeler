package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.mesh.IMesh

/**
 * Created by cout970 on 2017/05/07.
 */
data class Object(
        override val name: String,
        override val mesh: IMesh,
        override val transformation: ITransformation
) : IObject {

    override val transformedMesh: IMesh by lazy { mesh.transform(transformation) }

    override fun transform(func: (IMesh) -> IMesh): IObject {
        return copy(mesh = func(mesh))
    }
}