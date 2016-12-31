package com.cout970.modeler.util

import com.cout970.glutilities.tessellator.Tessellator
import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.Transformation
import com.cout970.vector.api.IQuaternion
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Quaterniond
import org.joml.Vector3d

/**
 * Created by cout970 on 2016/12/09.
 */
object RenderUtil {

    fun renderBar(tessellator: Tessellator, a_: IVector3, b_: IVector3, d: Double = 0.1, col: IVector3 = vec3Of(1, 1, 0)) {

        val a = a_
        val b = b_

        val dir = (b - a).normalize()

        val q = Quaterniond().rotationTo(dir.toJoml3d(), Vector3d(1.0, 0.0, 0.0))
        val rot: IQuaternion = if (b != a) quatOf(q.x, q.y, q.z, q.w) else Quaternion.IDENTITY

        val matrix = Transformation(a, rot, vec3Of(1)).matrix
        val mesh = Mesh.createCube(vec3Of(a.distance(b) + d, d, d), offset = vec3Of(-d / 2), centered = false)

        tessellator.apply {
            for ((pos) in mesh.getQuads().map { it.transform(matrix) }.flatMap(Quad::vertex)) {
                set(0, pos.x, pos.y, pos.z).set(1, col.x, col.y, col.z).endVertex()
            }
        }
    }
}

fun Quad.center(): IVector3 {
    val ab = (b.pos + a.pos) / 2
    val cd = (d.pos + c.pos) / 2
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