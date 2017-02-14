package com.cout970.modeler.model

import com.cout970.matrix.api.IMatrix4
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

operator fun IElementGroup.get(index: Int): IElement = elements[index]

fun Model.getElement(path: ElementPath): IElement {
    var element: IElement = elements[path.indices[0]]

    for (level in 1 until path.indices.size) {

        if (element !is IElementGroup)
            throw IllegalArgumentException("Invalid path: $path, level $level is not a group")

        val subIndex = path.indices[level]
        element = element[subIndex]
    }
    return element
}

fun Model.getQuads(path: ElementPath): List<Quad> {
    return getElement(path).getQuads()
}

@Suppress("FoldInitializerAndIfToElvis")
fun Model.getVertex(path: VertexPath): Vertex {
    val element = getElement(path)

    if (element !is IElementObject)
        throw IllegalArgumentException("Invalid path: $path, $element is not a ElementObject")

    val vertexIndex = element.vertex[path.vertexIndex]

    return vertexIndex.toVertex(element)
}

fun Model.getObjectElements(): List<IElementObject> {
    return getObjectPaths().map { getElement(it) as IElementObject }
}

fun Model.getObjectPaths(): List<ElementPath> {
    val paths = mutableListOf<ElementPath>()
    for ((index, elem) in elements.withIndex()) {
        if (elem is IElementGroup) {
            paths += elem.getObjectPaths()
        } else {
            paths += ElementPath(intArrayOf(index))
        }
    }
    return paths
}

fun IElementGroup.getObjectPaths(subPath: ElementPath = ElementPath(intArrayOf())): List<ElementPath> {
    val paths = mutableListOf<ElementPath>()

    for ((index, elem) in elements.withIndex()) {
        val newSubPath = ElementPath(subPath.indices + index)

        if (elem is IElementGroup) {
            paths += elem.getObjectPaths(subPath)
        } else {
            paths += newSubPath
        }
    }
    return paths
}


fun Model.applyObject(selection: Selection, func: (ElementPath, IElementObject) -> IElement): Model {
    return copy(
            elements = elements.mapIndexed { i, element ->
                element.applyObject(ElementPath(intArrayOf(i)), selection, func)
            }
    )
}

fun IElement.applyObject(path: ElementPath, selection: Selection,
                         func: (ElementPath, IElementObject) -> IElement): IElement {
    if (this is IElementGroup) {
        return deepCopy(elements.mapIndexed { i, element ->
            element.applyObject(ElementPath(path.indices + i), selection, func)
        })
    } else if (this is IElementObject) {
        return func(path, this)
    } else {
        throw IllegalStateException("Class ${this.javaClass} is not IElementGroup nor IElementObject")
    }
}

fun Model.applyVertex(selection: Selection, func: (VertexPath, Vertex) -> Vertex): Model {
    return copy(
            elements = elements.mapIndexed { i, element ->
                element.applyVertex(ElementPath(intArrayOf(i)), selection, func)
            }
    )
}

fun IElement.applyVertex(path: ElementPath, selection: Selection, func: (VertexPath, Vertex) -> Vertex): IElement {
    if (this is IElementGroup) {
        return deepCopy(elements.mapIndexed { i, element ->
            element.applyVertex(ElementPath(path.indices + i), selection, func)
        })
    } else if (this is IElementObject) {
        return applyVertexInObject(path, selection, func)
    } else {
        throw IllegalStateException("Class ${this.javaClass} is not IElementGroup nor IElementObject")
    }
}

fun IElementObject.applyVertexInObject(path: ElementPath, selection: Selection,
                                       func: (VertexPath, Vertex) -> Vertex): IElementObject {

    val affectedPaths = selection.filterPaths(path).mapNotNull { it as VertexPath }
    if (affectedPaths.isEmpty()) return this

    val newVertex = vertex.mapIndexed { i, vertexIndex ->
        val subPath = affectedPaths.find { it.vertexIndex == i } as? VertexPath
        val vertex = Vertex(positions[vertexIndex.pos], textures[vertexIndex.tex])
        if (subPath != null) {
            func(subPath, vertex)
        } else {
            vertex
        }
    }
    return updateVertex(newVertex)
}

fun IElementObject.isCuboid(): Boolean {
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

fun IElementObject.getCuboidSize(): IVector3 {
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

fun IElementObject.toAABB(): AABB {
    if (faces.isEmpty()) return AABB(Vector3.ORIGIN, Vector3.ORIGIN)
    var min: IVector3 = positions[0]
    var max: IVector3 = positions[0]
    for (pos in positions) {
        min = min.min(pos)
        max = max.max(pos)
    }
    return AABB(min, max)
}

fun IElementObject.rayTrace(matrix: IMatrix4, ray: Ray): RayTraceResult? {
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

