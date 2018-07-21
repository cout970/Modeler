package com.cout970.modeler.core.model.mesh

import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/05/07.
 */
class FaceIndex(
        val size: Int,

        val pos0: Int,
        val pos1: Int,
        val pos2: Int,
        val pos3: Int,

        val tex0: Int,
        val tex1: Int,
        val tex2: Int,
        val tex3: Int
) : IFaceIndex {

    override val vertexCount: Int get() = size

    override val pos: List<Int>
        get() = when (size) {
            0 -> emptyList()
            1 -> listOf(pos0)
            2 -> listOf(pos0, pos1)
            3 -> listOf(pos0, pos1, pos2)
            else -> listOf(pos0, pos1, pos2, pos3)
        }

    override val tex: List<Int>
        get() = when (size) {
            0 -> emptyList()
            1 -> listOf(tex0)
            2 -> listOf(tex0, tex1)
            3 -> listOf(tex0, tex1, tex2)
            else -> listOf(tex0, tex1, tex2, tex3)
        }

    companion object {
        fun from(pos: List<Int>, tex: List<Int>): FaceIndex {
            require(pos.size == tex.size) { "Sizes don't match: pos = ${pos.size}, tex = ${tex.size}" }
            return FaceIndex(pos.size,
                    pos.getOrNull(0) ?: 0,
                    pos.getOrNull(1) ?: 0,
                    pos.getOrNull(2) ?: 0,
                    pos.getOrNull(3) ?: 0,

                    tex.getOrNull(0) ?: 0,
                    tex.getOrNull(1) ?: 0,
                    tex.getOrNull(2) ?: 0,
                    tex.getOrNull(3) ?: 0
            )
        }
    }
}

fun IFaceIndex.getModelVertex(mesh: IMesh): List<IVector3> = pos.map { mesh.pos[it] }
fun IFaceIndex.getTextureVertex(mesh: IMesh): List<IVector2> = tex.map { mesh.tex[it] }