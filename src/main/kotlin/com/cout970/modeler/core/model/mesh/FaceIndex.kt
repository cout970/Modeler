package com.cout970.modeler.core.model.mesh

import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/05/07.
 */
class FaceIndex(
        override val pos: List<Int>,
        override val tex: List<Int>
) : IFaceIndex {

    override val vertexCount: Int get() = pos.size

    init {
        require(pos.size == tex.size){"Sizes don't match: pos = ${pos.size}, tex = ${tex.size}"}
    }
}

fun IFaceIndex.getModelVertex(mesh: IMesh): List<IVector3> = pos.map { mesh.pos[it] }
fun IFaceIndex.getTextureVertex(mesh: IMesh): List<IVector2> = tex.map { mesh.tex[it] }