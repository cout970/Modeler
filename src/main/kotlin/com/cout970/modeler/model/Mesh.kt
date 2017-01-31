package com.cout970.modeler.model

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.modeleditor.setUVFromCuboid
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
        @Expose val indices: List<QuadIndices>
) : IRayObstacle {

    fun getQuads(): List<Quad> = indices.map { it.toQuad(positions, textures) }

    fun getVertices(): List<Vertex> = getQuads().flatMap(Quad::vertex).distinct()

    fun translate(offset: IVector3): Mesh {
        return copy(positions.map { it + offset })
    }

    fun isCuboid(): Boolean {
        if (positions.size != 8) return false
        if (indices.size != 6) return false
        (0..1).forEach {
            if (indices[it].toQuad(positions, textures).vertex.map { it.pos.yd }.distinct().size != 1) {
                return false
            }
        }
        (2..3).forEach {
            if (indices[it].toQuad(positions, textures).vertex.map { it.pos.zd }.distinct().size != 1) {
                return false
            }
        }
        (4..5).forEach {
            if (indices[it].toQuad(positions, textures).vertex.map { it.pos.xd }.distinct().size != 1) {
                return false
            }
        }
        return true
    }

    fun getCuboidSize(): IVector3 {
        if (isCuboid()) {
            var a = indices[0].toQuad(positions, textures).vertex
            var b = indices[1].toQuad(positions, textures).vertex
            val y = b[0].pos.yd - a[0].pos.yd

            a = indices[2].toQuad(positions, textures).vertex
            b = indices[3].toQuad(positions, textures).vertex
            val z = b[0].pos.zd - a[0].pos.zd

            a = indices[4].toQuad(positions, textures).vertex
            b = indices[5].toQuad(positions, textures).vertex
            val x = b[0].pos.xd - a[0].pos.xd
            return vec3Of(x, y, z)
        }
        return Vector3.ORIGIN
    }

    fun toAABB(): AABB {
        if (indices.isEmpty()) return AABB(Vector3.ORIGIN, Vector3.ORIGIN)
        var min: IVector3 = positions[0]
        var max: IVector3 = positions[0]
        for (pos in positions) {
            min = min.min(pos)
            max = max.max(pos)
        }
        return AABB(min, max)
    }

    override fun toString(): String {
        return "Mesh(${indices.size} quads, ${positions.size} vertex, ${textures.size} uvCoords)"
    }

    companion object {

        fun createPlane(size: IVector2): Mesh {
            return Mesh(
                    listOf(vec3Of(0, 0, 0), vec3Of(0, 0, 1), vec3Of(1, 0, 1), vec3Of(1, 0, 0)).map {
                        it * vec3Of(size.x, 1, size.y)
                    },
                    listOf(vec2Of(0, 0), vec2Of(1, 0), vec2Of(1, 1), vec2Of(0, 1)),
                    listOf(QuadIndices(0, 0, 1, 1, 2, 2, 3, 3)))
        }

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

            val quads = createQuads(size, offset)
            return Mesh.quadsToMesh(quads).setUVFromCuboid(size, textureOffset, textureSize)
        }

        fun createQuads(size: IVector3, offset: IVector3): List<Quad> {
            val n: IVector3 = offset
            val p: IVector3 = size + offset

            return listOf(
                    //negY Down
                    Quad.create(
                            vec3Of(p.x, n.y, n.z),
                            vec3Of(p.x, n.y, p.z),
                            vec3Of(n.x, n.y, p.z),
                            vec3Of(n.x, n.y, n.z)
                    ),
                    //posY Up
                    Quad.create(
                            vec3Of(n.x, p.y, p.z),
                            vec3Of(p.x, p.y, p.z),
                            vec3Of(p.x, p.y, n.z),
                            vec3Of(n.x, p.y, n.z)
                    ),
                    //negZ North
                    Quad.create(
                            vec3Of(n.x, p.y, n.z),
                            vec3Of(p.x, p.y, n.z),
                            vec3Of(p.x, n.y, n.z),
                            vec3Of(n.x, n.y, n.z)
                    ),
                    //posZ South
                    Quad.create(
                            vec3Of(p.x, n.y, p.z),
                            vec3Of(p.x, p.y, p.z),
                            vec3Of(n.x, p.y, p.z),
                            vec3Of(n.x, n.y, p.z)
                    ),
                    //negX West
                    Quad.create(
                            vec3Of(n.x, n.y, p.z),
                            vec3Of(n.x, p.y, p.z),
                            vec3Of(n.x, p.y, n.z),
                            vec3Of(n.x, n.y, n.z)
                    ),
                    //posX East
                    Quad.create(
                            vec3Of(p.x, p.y, n.z),
                            vec3Of(p.x, p.y, p.z),
                            vec3Of(p.x, n.y, p.z),
                            vec3Of(p.x, n.y, n.z)
                    )
            )
        }
    }

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
}

data class QuadIndices(
        @Expose val aP: Int,
        @Expose val aT: Int,
        @Expose val bP: Int,
        @Expose val bT: Int,
        @Expose val cP: Int,
        @Expose val cT: Int,
        @Expose val dP: Int,
        @Expose val dT: Int
) {

    fun toQuad(pos: List<IVector3>, tex: List<IVector2>): Quad = Quad(
            Vertex(pos[aP], tex[aT]),
            Vertex(pos[bP], tex[bT]),
            Vertex(pos[cP], tex[cT]),
            Vertex(pos[dP], tex[dT]))

    val positions: List<Int> get() = listOf(aP, bP, cP, dP)
    val textureCoords: List<Int> get() = listOf(aT, bT, cT, dT)
}