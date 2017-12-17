package com.cout970.modeler.render.tool

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.BufferPTNC
import com.cout970.glutilities.tessellator.DrawMode
import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.cross
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.normalize
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL33

/**
 * Created by cout970 on 2017/05/25.
 */


fun measureMilisGPU(func: () -> Unit): Double{
    val queryId = GL15.glGenQueries()
    val frameGpuTime = IntArray(1)

    GL15.glBeginQuery(GL33.GL_TIME_ELAPSED, queryId)

    func()

    GL15.glEndQuery(GL33.GL_TIME_ELAPSED)
    GL15.glGetQueryObjectiv(queryId, GL15.GL_QUERY_RESULT, frameGpuTime)

    GL15.glDeleteQueries(queryId)

    return frameGpuTime[0] / 1_000_000.0
}

fun GLStateMachine.useBlend(amount: Float, func: () -> Unit) {
    blend.enable()
    blendFunc = GLStateMachine.BlendFunc.CONSTANT_ALPHA to GLStateMachine.BlendFunc.ONE_MINUS_CONSTANT_ALPHA
    GL14.glBlendColor(1f, 1f, 1f, amount)
    func()
    blendFunc = GLStateMachine.BlendFunc.SRC_ALPHA to GLStateMachine.BlendFunc.ONE_MINUS_SRC_ALPHA
    blend.disable()
}

data class Vertex(val pos: IVector3, val tex: IVector2, val norm: IVector3)

inline fun IMesh.forEachVertex(func: (Vertex) -> Unit) {
    faces.forEach { face ->

        val (a, b, c, d) = face.pos.map { pos[it] }
        val ac = c - a
        val bd = d - b
        val normal = (ac cross bd).normalize()

        for (index in 0 until face.vertexCount) {
            func(Vertex(pos[face.pos[index]], tex[face.tex[index]], normal))
        }
    }
}

inline fun IMesh.forEachEdge(func: (Pair<Vertex, Vertex>) -> Unit) {
    val list = mutableListOf<Pair<Vertex, Vertex>>()
    faces.forEach { face ->

        val (a, b, c, d) = face.pos.map { pos[it] }
        val ac = c - a
        val bd = d - b
        val normal = (ac cross bd).normalize()

        for (index in 0 until face.vertexCount) {
            val next = (index + 1) % face.vertexCount
            list += (Vertex(pos[face.pos[index]], tex[face.tex[index]], normal) to
                    Vertex(pos[face.pos[next]], tex[face.tex[next]], normal))
        }
    }
    list.distinct().forEach(func)
}

fun IMesh.mapFaceToEdges(f: IFaceIndex): List<Pair<Vertex, Vertex>> {
    val (a, b, c, d) = f.pos.map { pos[it] }
    val ac = c - a
    val bd = d - b
    val norm = (ac cross bd).normalize()

    return (0 until f.vertexCount).map { index ->
        val next = (index + 1) % f.vertexCount
        (Vertex(pos[f.pos[index]], tex[f.tex[index]], norm) to Vertex(pos[f.pos[next]], tex[f.tex[next]], norm))
    }
}

fun IMesh.append(buffer: BufferPTNC, color: IVector3 = Vector3.ONE) {
    forEachVertex { (pos, tex, norm) ->
        buffer.add(pos, tex, norm, color)
    }
}

fun IMesh.createVao(buffer: BufferPTNC, color: IVector3 = Vector3.ONE): VAO {
    return buffer.build(DrawMode.QUADS) {
        append(buffer, color)
    }
}
