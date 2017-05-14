package com.cout970.modeler.core.model

import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.api.model.IObjectCube
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/05/14.
 */
data class ObjectCube(
        override val name: String,

        override val size: IVector3,
        override val pos: IVector3,
        override val rotation: IQuaternion,

        override val transformation: ITransformation
) : IObjectCube {

    override val mesh: IMesh by lazy { generateMesh() }
    override val transformedMesh: IMesh by lazy { mesh.transform(transformation) }

    fun generateMesh(): IMesh {
        TODO("Create a mesh with cube parameters")
    }

    override fun transform(func: (IMesh) -> IMesh): IObject {
        return Object(name, mesh, transformation)
    }
}