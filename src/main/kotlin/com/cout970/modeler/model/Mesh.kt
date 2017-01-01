package com.cout970.modeler.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2016/11/29.
 */

//this class and subclasses must be immutable
data class Mesh(
        val positions: List<IVector3>,
        val textures: List<IVector2>,
        val indices: List<QuadIndices>,
        val transform: Transformation = Transformation.IDENTITY
               ) : IRayObstacle {

    fun getQuads(): List<Quad> = indices.map { it.toQuad(positions, textures) }

    fun getVertices(): List<Vertex> = getQuads().flatMap(Quad::vertex).distinct()

    fun rayTrace(matrix: IMatrix4, ray: Ray): RayTraceResult? {
        val hits = mutableListOf<RayTraceResult>()
        for ((a, b, c, d) in getQuads().map { it.transform(matrix) }) {
            RayTraceUtil.rayTraceQuad(ray, this, a.pos, b.pos, c.pos, d.pos)?.let { hits += it }
        }
        if (hits.isEmpty()) return null
        if (hits.size == 1) return hits.first()
        return hits.apply { sortBy { it.hit.distance(ray.start) } }.first()
    }

    override fun rayTrace(ray: Ray): RayTraceResult? {
        val hits = mutableListOf<RayTraceResult>()
        for ((a, b, c, d) in getQuads()) {
            RayTraceUtil.rayTraceQuad(ray, this, a.pos, b.pos, c.pos, d.pos)?.let { hits += it }
        }
        if (hits.isEmpty()) return null
        if (hits.size == 1) return hits.first()
        return hits.apply { sortBy { it.hit.distance(ray.start) } }.first()
    }

    override fun toString(): String {
        return "Mesh(transform=$transform)"
    }

    companion object {
        fun createPlane(size: IVector2, transform: Transformation = Transformation.IDENTITY) = Mesh(
                listOf(vec3Of(0, 0, 0), vec3Of(0, 0, 1), vec3Of(1, 0, 1), vec3Of(1, 0, 0)),
                listOf(vec2Of(0, 0), vec2Of(1, 0), vec2Of(1, 1), vec2Of(0, 1)),
                listOf(QuadIndices(0, 0, 1, 1, 2, 2, 3, 3)),
                transform
                                                                                                   )

        fun quadsToMesh(quads: List<Quad>, transform: Transformation = Transformation.IDENTITY): Mesh {
            val positions = quads.flatMap(Quad::vertex).map(Vertex::pos).distinct()
            val textures = quads.flatMap(Quad::vertex).map(Vertex::tex).distinct()
            val indices = quads.map {
                QuadIndices(
                        positions.indexOf(it.a.pos), textures.indexOf(it.a.tex),
                        positions.indexOf(it.b.pos), textures.indexOf(it.b.tex),
                        positions.indexOf(it.c.pos), textures.indexOf(it.c.tex),
                        positions.indexOf(it.d.pos), textures.indexOf(it.d.tex))
            }
            return Mesh(positions, textures, indices, transform)
        }

        fun createCube(size: IVector3, offset: IVector3 = vec3Of(0), centered: Boolean = false,
                       transform: Transformation = Transformation.IDENTITY): Mesh {
            val n: IVector3
            val p: IVector3
            if (centered) {
                n = -(size / 2) + offset
                p = size / 2 + offset
            } else {
                n = vec3Of(0) + offset
                p = size + offset
            }

            return quadsToMesh(listOf(
                    //negX
                    Quad.create(vec3Of(n.x, n.y, p.z), vec3Of(n.x, p.y, p.z), vec3Of(n.x, p.y, n.z),
                                vec3Of(n.x, n.y, n.z)),
                    //posX
                    Quad.create(vec3Of(p.x, p.y, n.z), vec3Of(p.x, p.y, p.z), vec3Of(p.x, n.y, p.z),
                                vec3Of(p.x, n.y, n.z)),
                    //negY
                    Quad.create(vec3Of(p.x, n.y, n.z), vec3Of(p.x, n.y, p.z), vec3Of(n.x, n.y, p.z),
                                vec3Of(n.x, n.y, n.z)),
                    //posY
                    Quad.create(vec3Of(n.x, p.y, p.z), vec3Of(p.x, p.y, p.z), vec3Of(p.x, p.y, n.z),
                                vec3Of(n.x, p.y, n.z)),
                    //negZ
                    Quad.create(vec3Of(n.x, p.y, n.z), vec3Of(p.x, p.y, n.z), vec3Of(p.x, n.y, n.z),
                                vec3Of(n.x, n.y, n.z)),
                    //posZ
                    Quad.create(vec3Of(p.x, n.y, p.z), vec3Of(p.x, p.y, p.z), vec3Of(n.x, p.y, p.z),
                                vec3Of(n.x, n.y, p.z))
                                     ), transform)
        }
    }
}

data class QuadIndices(val aP: Int, val aT: Int, val bP: Int, val bT: Int, val cP: Int, val cT: Int, val dP: Int,
                       val dT: Int) {
    fun toQuad(pos: List<IVector3>, tex: List<IVector2>): Quad = Quad(
            Vertex(pos[aP], tex[aT]),
            Vertex(pos[bP], tex[bT]),
            Vertex(pos[cP], tex[cT]),
            Vertex(pos[dP], tex[dT]))

    val positions: List<Int> get() = listOf(aP, bP, cP, dP)
    val textureCoords: List<Int> get() = listOf(aT, bT, cT, dT)
}