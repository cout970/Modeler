package com.cout970.modeler.render.tool

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.BufferPTNC
import com.cout970.glutilities.tessellator.DrawMode
import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.api.model.mesh.IFaceIndex
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.core.model.mesh.FaceIndex
import com.cout970.modeler.core.model.mesh.Mesh
import com.cout970.modeler.core.model.selection.PosRef
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL33

/**
 * Created by cout970 on 2017/05/25.
 */


fun measureMilisGPU(func: () -> Unit): Double {
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

fun IFaceIndex.getEdges(): List<Pair<Int, Int>> {
    return (0 until vertexCount).map { index ->
        val next = (index + 1) % vertexCount
        pos[index] to pos[next]
    }
}

fun IMesh.getPosRefs(obj: IObjectRef) = pos.indices.map { PosRef(obj.objectIndex, it) }

fun IMesh.removeFace(id: Int): IMesh {
    return Mesh(this.pos, this.tex, this.faces.filterIndexed { index, _ -> index != id }).optimize()
}

fun IMesh.removeFaces(refs: List<Int>): IMesh {
    return Mesh(this.pos, this.tex, this.faces.filterIndexed { index, _ -> index !in refs }).optimize()
}

fun IMesh.getFacePos(index: Int): List<IVector3> {
    return faces[index].pos.map { pos[it] }
}

fun IMesh.addFace(vertex: List<IVector3>): IMesh {
    val newFace = FaceIndex(listOf(0, 1, 2, 3).map { it + this.pos.size }, (1..4).map { this.tex.size })
    return Mesh(this.pos + vertex, this.tex + Vector2.ZERO, this.faces + newFace)
}