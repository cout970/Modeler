package com.cout970.modeler.model

import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.distance
import com.cout970.vector.extensions.div
import com.cout970.vector.extensions.unaryMinus
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/11/29.
 */

//this class and subclasses must be immutable
sealed class ModelComponent() : IRayObstacle {

    val transformation: Transformation = Transformation.IDENTITY

    abstract fun getQuads(): List<Quad>
    abstract fun getVertices(): List<Vertex>

    override fun rayTrace(ray: Ray): RayTraceResult? {
        val hits = mutableListOf<RayTraceResult>()
        for ((a, b, c, d) in getQuads()) {
            RayTraceUtil.rayTraceQuad(ray, this, a.pos, b.pos, c.pos, d.pos)?.let { hits += it }
        }
        if (hits.isEmpty()) return null
        if (hits.size == 1) return hits.first()
        return hits.apply { sortBy { it.hit.distance(ray.start) } }.first()
    }
}

data class Mesh(
        val vertex: List<Vertex>,
        val indices: List<QuadIndices>
) : ModelComponent() {

    override fun getQuads(): List<Quad> = indices.map { it.toQuad(vertex) }

    override fun getVertices(): List<Vertex> = vertex

    data class QuadIndices(val a: Int, val b: Int, val c: Int, val d: Int) {
        fun toQuad(vertex: List<Vertex>): Quad = Quad(vertex[a], vertex[b], vertex[c], vertex[d])
    }
}

data class Plane(
        val vertex0: Vertex,
        val vertex1: Vertex,
        val vertex2: Vertex,
        val vertex3: Vertex
) : ModelComponent() {

    override fun getQuads(): List<Quad> = listOf(Quad(vertex0, vertex1, vertex2, vertex3))

    override fun getVertices(): List<Vertex> = listOf(vertex0, vertex1, vertex2, vertex3)
}

data class Cube(
        val negX: Quad,
        val posX: Quad,
        val negY: Quad,
        val posY: Quad,
        val negZ: Quad,
        val posZ: Quad
) : ModelComponent() {

    override fun getQuads(): List<Quad> = listOf(negX, posX, negY, posY, negZ, posZ)

    override fun getVertices(): List<Vertex> = getQuads().flatMap(Quad::vertex)

    companion object {
        fun create(size: IVector3): Cube {
            val n = -(size / 2)
            val p = size / 2
            return Cube(
                    //negX
                    Quad.create(vec3Of(n.x, n.y, n.z), vec3Of(n.x, p.y, n.z), vec3Of(n.x, p.y, p.z), vec3Of(n.x, n.y, p.z)),
                    //posX
                    Quad.create(vec3Of(p.x, n.y, n.z), vec3Of(p.x, n.y, p.z), vec3Of(p.x, p.y, p.z), vec3Of(p.x, p.y, n.z)),
                    //negY
                    Quad.create(vec3Of(n.x, n.y, n.z), vec3Of(n.x, n.y, p.z), vec3Of(p.x, n.y, p.z), vec3Of(p.x, n.y, n.z)),
                    //posY
                    Quad.create(vec3Of(n.x, p.y, n.z), vec3Of(p.x, p.y, n.z), vec3Of(p.x, p.y, p.z), vec3Of(n.x, p.y, p.z)),
                    //negZ
                    Quad.create(vec3Of(n.x, n.y, n.z), vec3Of(p.x, n.y, n.z), vec3Of(p.x, p.y, n.z), vec3Of(n.x, p.y, n.z)),
                    //posZ
                    Quad.create(vec3Of(n.x, n.y, p.z), vec3Of(n.x, p.y, p.z), vec3Of(p.x, p.y, p.z), vec3Of(p.x, n.y, p.z))
            )
        }
    }
}