package com.cout970.modeler.model

import com.cout970.modeler.modeleditor.setUVFromCuboid
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/02/11.
 */
object Meshes {

    fun createPlane(size: IVector2): ElementObject {
        return ElementObject(
                listOf(vec3Of(0, 0, 0), vec3Of(0, 0, 1), vec3Of(1, 0, 1), vec3Of(1, 0, 0)).map {
                    it * vec3Of(size.x, 1, size.y)
                },
                listOf(vec2Of(0, 0), vec2Of(1, 0), vec2Of(1, 1), vec2Of(0, 1)),
                listOf(
                        VertexIndex(0, 0),
                        VertexIndex(1, 1),
                        VertexIndex(2, 2),
                        VertexIndex(3, 3)
                ),
                listOf(QuadIndex(0, 1, 2, 3)))
    }

    fun quadsToMesh(quads: List<Quad>): ElementObject {

        val vertexList = mutableListOf<Vertex>()
        val quadIndex = quads.map { quad ->
            quad.vertex
                    .filter { it !in vertexList }
                    .forEach { vertexList += it }

            QuadIndex(
                    vertexList.indexOf(quad.a),
                    vertexList.indexOf(quad.b),
                    vertexList.indexOf(quad.c),
                    vertexList.indexOf(quad.d)
            )
        }

        val positions = vertexList.map(Vertex::pos).distinct()
        val textures = vertexList.map(Vertex::tex).distinct()

        val vertexIndex = vertexList.map { (pos, tex) ->
            VertexIndex(positions.indexOf(pos), textures.indexOf(tex))
        }

        return ElementObject(positions, textures, vertexIndex, quadIndex)
    }

    fun createCube(size: IVector3, offset: IVector3 = Vector3.ORIGIN, textureOffset: IVector2 = Vector2.ORIGIN,
                   textureSize: IVector2 = vec2Of(64, 64)): ElementObject {

        val quads = createQuads(size, offset)
        return quadsToMesh(quads).setUVFromCuboid(size, textureOffset, textureSize)
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