package com.cout970.modeler.view.render.tool

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.modeler.view.render.tool.shader.UniversalShader
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.cross
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.normalize
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/05/25.
 */

fun GLStateMachine.useBlend(amount: Float, func: () -> Unit) {
    GLStateMachine.blend.enable()
    GLStateMachine.blendFunc = GLStateMachine.BlendFunc.CONSTANT_ALPHA to GLStateMachine.BlendFunc.ONE_MINUS_CONSTANT_ALPHA
    org.lwjgl.opengl.GL14.glBlendColor(1f, 1f, 1f, amount)
    func()
    GLStateMachine.blendFunc = GLStateMachine.BlendFunc.SRC_ALPHA to GLStateMachine.BlendFunc.ONE_MINUS_SRC_ALPHA
    GLStateMachine.blend.disable()
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

fun IMesh.append(buffer: UniversalShader.Buffer, color: IVector3 = Vector3.ONE) {
    forEachVertex { (pos, tex, norm) ->
        buffer.add(pos, tex, norm, color)
    }
}

fun IMesh.createVao(buffer: UniversalShader.Buffer, color: IVector3 = Vector3.ONE): VAO {
    return buffer.build(GL11.GL_QUADS) {
        append(buffer)
    }
}