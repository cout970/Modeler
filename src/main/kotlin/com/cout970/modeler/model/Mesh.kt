package com.cout970.modeler.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.raytrace.IRayObstacle
import com.cout970.raytrace.Ray
import com.cout970.raytrace.RayTraceResult
import com.cout970.raytrace.RayTraceUtil
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import com.google.gson.annotations.Expose

/**
 * Created by cout970 on 2016/11/29.
 */

//this class must be immutable
data class Mesh(
        @Expose val positions: List<IVector3>,
        @Expose val textures: List<IVector2>,
        @Expose val indices: List<QuadIndices>) : IRayObstacle {

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
        return "Mesh(${indices.size} quads, ${positions.size} vertex, ${textures.size} uvCoords)"
    }

    companion object {
        fun createPlane(size: IVector2) = Mesh(
                listOf(vec3Of(0, 0, 0), vec3Of(0, 0, 1), vec3Of(1, 0, 1), vec3Of(1, 0, 0)),
                listOf(vec2Of(0, 0), vec2Of(1, 0), vec2Of(1, 1), vec2Of(0, 1)),
                listOf(QuadIndices(0, 0, 1, 1, 2, 2, 3, 3)))

        fun quadsToMesh(quads: List<Quad>): Mesh {
            val positions = quads.flatMap(Quad::vertex).map(Vertex::pos).distinct()
            val textures = quads.flatMap(Quad::vertex).map(Vertex::tex).distinct()
            val indices = quads.map {
                QuadIndices(
                        positions.indexOf(it.a.pos), textures.indexOf(it.a.tex),
                        positions.indexOf(it.b.pos), textures.indexOf(it.b.tex),
                        positions.indexOf(it.c.pos), textures.indexOf(it.c.tex),
                        positions.indexOf(it.d.pos), textures.indexOf(it.d.tex))
            }
            return Mesh(positions, textures, indices)
        }

        fun createCube(size: IVector3, offset: IVector3 = Vector3.ORIGIN, textureOffset: IVector2 = Vector2.ORIGIN,
                       textureSize: IVector2 = vec2Of(64, 64)): Mesh {
            val n: IVector3 = vec3Of(0) + offset
            val p: IVector3 = size + offset

            val width = size.xd
            val height = size.yd
            val length = size.zd

            val offsetX = textureOffset.xd
            val offsetY = textureOffset.yd

            val texelSize = vec2Of(1) / textureSize

            val quads = listOf(
                    //negX West
                    Quad.create(
                            vec3Of(n.x, n.y, p.z),
                            vec3Of(n.x, p.y, p.z),
                            vec3Of(n.x, p.y, n.z),
                            vec3Of(n.x, n.y, n.z)
                    ).setTexture1(
                            vec2Of(offsetX + length + width + length, offsetY + length + height) * texelSize,
                            vec2Of(offsetX + length + width, offsetY + length) * texelSize
                    ),
                    //posX East
                    Quad.create(
                            vec3Of(p.x, p.y, n.z),
                            vec3Of(p.x, p.y, p.z),
                            vec3Of(p.x, n.y, p.z),
                            vec3Of(p.x, n.y, n.z)
                    ).setTexture(
                            vec2Of(offsetX + length, offsetY + length + height) * texelSize,
                            vec2Of(offsetX, offsetY + length) * texelSize
                    ),
                    //negY Down
                    Quad.create(
                            vec3Of(p.x, n.y, n.z),
                            vec3Of(p.x, n.y, p.z),
                            vec3Of(n.x, n.y, p.z),
                            vec3Of(n.x, n.y, n.z)
                    ).setTexture1(
                            vec2Of(offsetX + length + width + width, offsetY) * texelSize,
                            vec2Of(offsetX + length + width, offsetY + length) * texelSize
                    ),
                    //posY Up
                    Quad.create(
                            vec3Of(n.x, p.y, p.z),
                            vec3Of(p.x, p.y, p.z),
                            vec3Of(p.x, p.y, n.z),
                            vec3Of(n.x, p.y, n.z)
                    ).setTexture(
                            vec2Of(offsetX + length + width, offsetY + length) * texelSize,
                            vec2Of(offsetX + length, offsetY) * texelSize
                    ),
                    //negZ North
                    Quad.create(
                            vec3Of(n.x, p.y, n.z),
                            vec3Of(p.x, p.y, n.z),
                            vec3Of(p.x, n.y, n.z),
                            vec3Of(n.x, n.y, n.z)
                    ).setTexture(
                            vec2Of(offsetX + length + width, offsetY + length + height) * texelSize,
                            vec2Of(offsetX + length, offsetY + length) * texelSize
                    ),
                    //posZ South
                    Quad.create(
                            vec3Of(p.x, n.y, p.z),
                            vec3Of(p.x, p.y, p.z),
                            vec3Of(n.x, p.y, p.z),
                            vec3Of(n.x, n.y, p.z)
                    ).setTexture1(
                            vec2Of(offsetX + length + width + length, offsetY + length) * texelSize,
                            vec2Of(offsetX + length + width + length + width, offsetY + length + height) * texelSize
                    )
            )
            return Mesh.quadsToMesh(quads)
        }

        private fun Quad.setTexture(uv0: IVector2, uv1: IVector2): Quad {
            return Quad(
                    a.copy(tex = vec2Of(uv1.x, uv0.y)),
                    b.copy(tex = vec2Of(uv0.x, uv0.y)),
                    c.copy(tex = vec2Of(uv0.x, uv1.y)),
                    d.copy(tex = vec2Of(uv1.x, uv1.y))
            )
        }

        private fun Quad.setTexture1(uv0: IVector2, uv1: IVector2): Quad {
            return Quad(
                    a.copy(tex = vec2Of(uv1.x, uv1.y)),
                    b.copy(tex = vec2Of(uv1.x, uv0.y)),
                    c.copy(tex = vec2Of(uv0.x, uv0.y)),
                    d.copy(tex = vec2Of(uv0.x, uv1.y))
            )
        }
    }

    fun translate(offset: IVector3): Mesh {
        return copy(positions.map { it + offset })
    }
}

data class QuadIndices(@Expose val aP: Int,
                       @Expose val aT: Int,
                       @Expose val bP: Int,
                       @Expose val bT: Int,
                       @Expose val cP: Int,
                       @Expose val cT: Int,
                       @Expose val dP: Int,
                       @Expose val dT: Int) {

    fun toQuad(pos: List<IVector3>, tex: List<IVector2>): Quad = Quad(
            Vertex(pos[aP], tex[aT]),
            Vertex(pos[bP], tex[bT]),
            Vertex(pos[cP], tex[cT]),
            Vertex(pos[dP], tex[dT]))

    val positions: List<Int> get() = listOf(aP, bP, cP, dP)
    val textureCoords: List<Int> get() = listOf(aT, bT, cT, dT)
}