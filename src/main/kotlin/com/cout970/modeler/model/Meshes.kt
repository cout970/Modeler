package com.cout970.modeler.model

import com.cout970.modeler.model.api.QuadIndex
import com.cout970.modeler.modeleditor.setUVFromCuboid
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/02/11.
 */
object Meshes {

    fun createPlane(size: IVector2): ElementLeaf {
        return ElementLeaf(
                listOf(vec3Of(0, 0, 0), vec3Of(0, 0, 1), vec3Of(1, 0, 1), vec3Of(1, 0, 0)).map {
                    it * vec3Of(size.x, 1, size.y)
                },
                listOf(vec2Of(0, 0), vec2Of(1, 0), vec2Of(1, 1), vec2Of(0, 1)),
                listOf(QuadIndex(0 to 0, 1 to 1, 2 to 2, 3 to 3)))
    }

    fun quadsToMesh(quads: List<Quad>): ElementLeaf {
        val positions = quads.flatMap(Quad::vertex).map(Vertex::pos).distinct()
        val textures = quads.flatMap(Quad::vertex).map(Vertex::tex).distinct()

        val quadIndex = quads.map { (a, b, c, d) ->
            QuadIndex(
                    positions.indexOf(a.pos) to textures.indexOf(a.tex),
                    positions.indexOf(b.pos) to textures.indexOf(b.tex),
                    positions.indexOf(c.pos) to textures.indexOf(c.tex),
                    positions.indexOf(d.pos) to textures.indexOf(d.tex)
            )
        }

        return ElementLeaf(positions, textures, quadIndex)
    }

    fun createCube(size: IVector3, offset: IVector3 = Vector3.ORIGIN, textureOffset: IVector2 = Vector2.ORIGIN,
                   textureSize: IVector2 = vec2Of(64, 64)): ElementLeaf {

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