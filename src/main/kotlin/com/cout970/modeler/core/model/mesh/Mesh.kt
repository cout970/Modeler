package com.cout970.modeler.core.model.mesh

import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJOML
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.xd
import com.cout970.vector.extensions.yd
import com.cout970.vector.extensions.zd
import org.joml.Vector4d

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

    override fun transformPos(selection: List<Int>, func: (Int, IVector3) -> IVector3): IMesh {
        return Mesh(pos.mapIndexed { index, vec -> if (index in selection) func(index, vec) else vec }, tex, faces)
    }

    override fun transformTex(selection: List<Int>, func: (Int, IVector2) -> IVector2): IMesh {
        return Mesh(pos, tex.mapIndexed { index, vec -> if (index in selection) func(index, vec) else vec }, faces)
    }

    override fun transform(trans: ITransformation): IMesh {
        val matrix = trans.matrix.toJOML()
        return Mesh(pos.map { matrix.transform(Vector4d(it.xd, it.yd, it.zd, 1.0)).toIVector() },
                tex, faces)
    }

    override fun merge(other: IMesh): IMesh {
        val newPos = (pos + other.pos).distinct()
        val newTex = (tex + other.tex).distinct()

        val newFaces = faces.map { face ->
            val p = face.pos.map { newPos.indexOf(pos[it]) }
            val t = face.tex.map { newTex.indexOf(tex[it]) }

            FaceIndex(p, t)
        } + other.faces.map { face ->
            val p = face.pos.map { newPos.indexOf(other.pos[it]) }
            val t = face.tex.map { newTex.indexOf(other.tex[it]) }

            FaceIndex(p, t)
        }

        return Mesh(newPos, newTex, newFaces)
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