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

    init {
        require(faces.isEmpty() || pos.isNotEmpty() && tex.isNotEmpty())
    }

    override fun transformPos(selection: List<Int>, func: (Int, IVector3) -> IVector3): IMesh = this
    override fun transformTex(selection: List<Int>, func: (Int, IVector2) -> IVector2): IMesh = this

    override fun transform(trans: ITransformation): IMesh {
        return transformPos(pos.indices.toList()) { _, pos -> trans.matrix * pos }
    }

    override fun merge(other: IMesh): IMesh {
        return Mesh(pos + other.pos, tex + other.tex, faces + other.faces)
    }

    override fun optimize(): IMesh {
        if (faces.isEmpty()) return this
        if (pos.isEmpty()) return this
        if (tex.isEmpty()) return this

        val newPos = pos.distinct()
        val newTex = tex.distinct()

        val newFaces = faces.map { face ->
            val p = face.pos.map { newPos.indexOf(pos[it]) }
            val t = face.tex.map { newTex.indexOf(tex[it]) }

            FaceIndex(p, t)
        }
        return Mesh(newPos, newTex, newFaces)
    }
}