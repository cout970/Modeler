package com.cout970.modeler.core.model.mesh

import com.cout970.matrix.extensions.times
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/05/07.
 */
class Mesh(
        override val pos: List<IVector3> = emptyList(),
        override val tex: List<IVector2> = emptyList(),
        override val faces: List<IFaceIndex> = emptyList()
) : IMesh {

    override fun transformPos(selection: List<Int>, func: (Int, IVector3) -> IVector3): IMesh = this
    override fun transformTex(selection: List<Int>, func: (Int, IVector2) -> IVector2): IMesh = this

    override fun transform(trans: ITransformation): IMesh {
        return transformPos(pos.indices.toList()) { _, pos -> trans.matrix * pos }
    }
}