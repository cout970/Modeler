package com.cout970.modeler.core.model.mesh

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.util.toJOML
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
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
        return transform(trans.matrix)
    }

    override fun transform(matrix: IMatrix4): IMesh {
        val mat = matrix.toJOML()
        return Mesh(
                pos = pos.map {
                    mat.transform(Vector4d(it.xd, it.yd, it.zd, 1.0))
                }.map { vec3Of(it.x, it.y, it.z) },
                tex = tex,
                faces = faces
        )
    }

    override fun transformTexture(trans: ITransformation): IMesh {
        val matrix = trans.matrix.toJOML()
        return Mesh(
                pos = pos,
                tex = tex.map {
                    matrix.transform(Vector4d(it.xd, it.yd, 0.0, 1.0))
                }.map { vec2Of(it.x, it.y) },
                faces = faces
        )
    }

    override fun merge(other: IMesh): IMesh {
        val newPos = (pos + other.pos)
        val newTex = (tex + other.tex)

        val newFaces = faces + other.faces.map { face ->
            val p = face.pos.map { it + pos.size }
            val t = face.tex.map { it + tex.size }

            FaceIndex.from(p, t)
        }

        return Mesh(newPos, newTex, newFaces)
    }

    override fun optimize(): IMesh {
        if (faces.isEmpty()) return this
        if (pos.isEmpty()) return this
        if (tex.isEmpty()) return this

        val newPos = pos.filterIndexed { index, _ -> faces.any { index in it.pos } }.distinct()
        val newTex = tex.filterIndexed { index, _ -> faces.any { index in it.tex } }.distinct()

        val newFaces = faces.map { face ->
            val p = face.pos.map { newPos.indexOf(pos[it]) }
            val t = face.tex.map { newTex.indexOf(tex[it]) }

            FaceIndex.from(p, t)
        }
        return Mesh(newPos, newTex, newFaces)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mesh) return false

        if (pos != other.pos) return false
        if (tex != other.tex) return false
        if (faces != other.faces) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pos.hashCode()
        result = 31 * result + tex.hashCode()
        result = 31 * result + faces.hashCode()
        return result
    }
}