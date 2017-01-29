package com.cout970.modeler.util

import com.cout970.glutilities.tessellator.ITessellator
import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.Transformation
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Quaterniond
import org.joml.Vector3d

/**
 * Created by cout970 on 2016/12/09.
 */
object RenderUtil {

    fun renderBar(tessellator: ITessellator, a_: IVector3, b_: IVector3, size: Double = 0.1,
                  color: IVector3 = vec3Of(1, 1, 0)) {

        val a = a_
        val b = b_

        val dir = (b - a).normalize()

        val q = Quaterniond().rotationTo(dir.toJoml3d(), Vector3d(1.0, 0.0, 0.0))
        val rot: IQuaternion = if (b != a) quatOf(q.x, q.y, q.z, q.w) else Quaternion.IDENTITY

        val matrix = Transformation(a, rot, vec3Of(1)).matrix
        val mesh = Mesh.createCube(vec3Of(a.distance(b) + size, size, size), offset = vec3Of(-size / 2))

        tessellator.apply {
            for ((pos) in mesh.getQuads().map { it.transform(matrix) }.flatMap(Quad::vertex)) {
                set(0, pos.x, pos.y, pos.z).set(1, color.x, color.y, color.z).endVertex()
            }
        }
    }

    fun renderCircle(t: ITessellator, center: IVector3, axis: SelectionAxis, radius: Double, size: Double = 0.05,
                     color: IVector3 = vec3Of(1, 1, 1)) {
        val quality = 16
        for (i in 0..360 / quality) {
            val angle0 = Math.toRadians(i.toDouble() * quality)
            val angle1 = Math.toRadians((i.toDouble() + 1) * quality)

            val start = if (axis == SelectionAxis.Y) {
                vec3Of(Math.sin(angle0), Math.cos(angle0), 0)
            } else if (axis == SelectionAxis.Z) {
                vec3Of(0, Math.sin(angle0), Math.cos(angle0))
            } else {
                vec3Of(Math.sin(angle0), 0, Math.cos(angle0))
            }

            val end = if (axis == SelectionAxis.Y) {
                vec3Of(Math.sin(angle1), Math.cos(angle1), 0)
            } else if (axis == SelectionAxis.Z) {
                vec3Of(0, Math.sin(angle1), Math.cos(angle1))
            } else {
                vec3Of(Math.sin(angle1), 0, Math.cos(angle1))
            }

            renderBar(t, start * radius + center, end * radius + center, size, color)
        }
    }
}

fun Quad.center3D(): IVector3 {
    val ab = (b.pos + a.pos) / 2
    val cd = (d.pos + c.pos) / 2
    return (ab + cd) / 2
}

fun Quad.center2D(): IVector2 {
    val ab = (b.tex + a.tex) / 2
    val cd = (d.tex + c.tex) / 2
    return (ab + cd) / 2
}

fun Iterable<IVector3>.middle(): IVector3 {
    var sum: IVector3? = null
    var count = 0
    for (i in this) {
        if (sum == null) sum = i else sum += i
        count++
    }
    if (sum == null) return vec3Of(0)
    return sum / count
}

fun Iterable<IVector2>.middle(): IVector2 {
    var sum: IVector2? = null
    var count = 0
    for (i in this) {
        if (sum == null) sum = i else sum += i
        count++
    }
    if (sum == null) return vec2Of(0)
    return sum / count
}

fun getSide(side: Int, size: IVector3 = vec3Of(1), offset: IVector3 = vec3Of(0)): List<IVector3> {
    val n: IVector3 = offset
    val p: IVector3 = size + offset
    return when (side) {
        0 -> listOf(vec3Of(n.x, n.y, p.z), vec3Of(n.x, p.y, p.z), vec3Of(n.x, p.y, n.z), vec3Of(n.x, n.y, n.z))//negX
        1 -> listOf(vec3Of(p.x, p.y, n.z), vec3Of(p.x, p.y, p.z), vec3Of(p.x, n.y, p.z), vec3Of(p.x, n.y, n.z))//posX
        2 -> listOf(vec3Of(p.x, n.y, n.z), vec3Of(p.x, n.y, p.z), vec3Of(n.x, n.y, p.z), vec3Of(n.x, n.y, n.z))//negY
        3 -> listOf(vec3Of(n.x, p.y, p.z), vec3Of(p.x, p.y, p.z), vec3Of(p.x, p.y, n.z), vec3Of(n.x, p.y, n.z))//posY
        4 -> listOf(vec3Of(n.x, p.y, n.z), vec3Of(p.x, p.y, n.z), vec3Of(p.x, n.y, n.z), vec3Of(n.x, n.y, n.z))//negZ
        else -> listOf(vec3Of(p.x, n.y, p.z), vec3Of(p.x, p.y, p.z), vec3Of(n.x, p.y, p.z), vec3Of(n.x, n.y, p.z))//posZ
    }
}