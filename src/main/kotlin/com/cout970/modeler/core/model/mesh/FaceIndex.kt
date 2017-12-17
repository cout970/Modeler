package com.cout970.modeler.core.model.mesh

import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.util.join
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/05/07.
 */
class FaceIndex(val index: List<Pair<Int, Int>>) : IFaceIndex {

    override val pos: List<Int> get() = index.map { it.first }
    override val tex: List<Int> get() = index.map { it.second }
    override val vertexCount: Int get() = index.size

    constructor(pos: List<Int>, tex: List<Int>) : this(pos join tex)
}

fun IFaceIndex.getModelVertex(mesh: IMesh): List<IVector3> = pos.map { mesh.pos[it] }
fun IFaceIndex.getTextureVertex(mesh: IMesh): List<IVector2> = tex.map { mesh.tex[it] }