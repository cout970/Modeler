package com.cout970.modeler.model.util

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.model.AABB
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.api.IElement
import com.cout970.modeler.model.api.IElementGroup
import com.cout970.modeler.model.api.IElementLeaf
import com.cout970.modeler.util.FakeRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/02/11.
 */

fun IElementLeaf.isCuboid(): Boolean {
    if (positions.size != 8) return false
    if (faces.size != 6) return false
    (0..1).forEach {
        if (faces[it].toQuad(this).vertex.map { it.pos.yd }.distinct().size != 1) {
            return false
        }
    }
    (2..3).forEach {
        if (faces[it].toQuad(this).vertex.map { it.pos.zd }.distinct().size != 1) {
            return false
        }
    }
    (4..5).forEach {
        if (faces[it].toQuad(this).vertex.map { it.pos.xd }.distinct().size != 1) {
            return false
        }
    }
    return true
}

fun IElementLeaf.getCuboidSize(): IVector3 {
    if (isCuboid()) {
        var a = faces[0].toQuad(this).vertex
        var b = faces[1].toQuad(this).vertex
        val y = b[0].pos.yd - a[0].pos.yd

        a = faces[2].toQuad(this).vertex
        b = faces[3].toQuad(this).vertex
        val z = b[0].pos.zd - a[0].pos.zd

        a = faces[4].toQuad(this).vertex
        b = faces[5].toQuad(this).vertex
        val x = b[0].pos.xd - a[0].pos.xd
        return vec3Of(x, y, z)
    }
    return Vector3.ORIGIN
}

fun IElementLeaf.toAABB(): AABB {
    if (faces.isEmpty()) return AABB(Vector3.ORIGIN, Vector3.ORIGIN)
    var min: IVector3 = positions[0]
    var max: IVector3 = positions[0]
    for (pos in positions) {
        min = min.min(pos)
        max = max.max(pos)
    }
    return AABB(min, max)
}

fun IElementLeaf.rayTrace(matrix: IMatrix4, ray: Ray): RayTraceResult? {
    val hits = mutableListOf<RayTraceResult>()
    for ((a, b, c, d) in getQuads().map { it.transform(matrix) }) {
        RayTraceUtil.rayTraceQuad(ray, FakeRayObstacle, a.pos, b.pos, c.pos, d.pos)?.let { hits += it }
    }
    if (hits.isEmpty()) return null
    if (hits.size == 1) return hits.first()
    return hits.apply { sortBy { it.hit.distance(ray.start) } }.first()
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


fun Model.zipGroups(): Map<String, List<IElement>> {
    return zipGroup(elements, "root")
}

private fun zipGroup(group: List<IElement>, prefix: String): Map<String, List<IElement>> {
    val map = mutableMapOf<String, List<IElement>>()

    val noGroups = mutableListOf<IElement>()
    for (i in group) {
        if (i is IElementGroup) {
            map += zipGroup(i.elements, "$prefix/${i.name}")
        } else {
            noGroups += i
        }
    }
    map += prefix to noGroups

    return map
}

