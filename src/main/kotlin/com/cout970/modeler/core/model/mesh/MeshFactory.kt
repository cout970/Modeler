package com.cout970.modeler.core.model.mesh

import com.cout970.modeler.api.model.mesh.IMesh
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/05/28.
 */
object MeshFactory {

    fun createPlaneY(size: IVector2): IMesh {
        return Mesh(
                listOf(vec3Of(0, 0, 0), vec3Of(0, 0, 1), vec3Of(1, 0, 1), vec3Of(1, 0, 0))
                        .map { it * vec3Of(size.x, 1, size.y) },
                listOf(vec2Of(0, 0), vec2Of(1, 0), vec2Of(1, 1), vec2Of(0, 1)),

                listOf(FaceIndex.from(listOf(0, 1, 2, 3), listOf(0, 1, 2, 3)))
        )
    }

    fun createPlaneZ(size: IVector2): IMesh {
        return Mesh(
                listOf(vec3Of(0, 0, 0), vec3Of(0, 1, 0), vec3Of(1, 1, 0), vec3Of(1, 0, 0))
                        .map { it * vec3Of(size.x, size.y, 1) },
                listOf(vec2Of(0, 0), vec2Of(1, 0), vec2Of(1, 1), vec2Of(0, 1)),

                listOf(FaceIndex.from(listOf(0, 1, 2, 3), listOf(0, 1, 2, 3)))
        )
    }

    fun createCube(size: IVector3, offset: IVector3): IMesh {
        val n: IVector3 = offset
        val p: IVector3 = size + offset
        return createAABB(n, p)
    }

    fun createAABB(n: IVector3, p: IVector3): IMesh {

        val pos = listOf(
                vec3Of(p.x, n.y, n.z), //0
                vec3Of(p.x, n.y, p.z), //1
                vec3Of(n.x, n.y, p.z), //2
                vec3Of(n.x, n.y, n.z), //3
                vec3Of(n.x, p.y, p.z), //4
                vec3Of(p.x, p.y, p.z), //5
                vec3Of(p.x, p.y, n.z), //6
                vec3Of(n.x, p.y, n.z)  //7
        )
        val tex = listOf(vec2Of(0, 0), vec2Of(1, 0), vec2Of(1, 1), vec2Of(0, 1))
        val faces = listOf(
            FaceIndex.from(listOf(0, 1, 2, 3), listOf(0, 1, 2, 3)), // down
            FaceIndex.from(listOf(4, 5, 6, 7), listOf(0, 1, 2, 3)), // up
            FaceIndex.from(listOf(7, 6, 0, 3), listOf(0, 1, 2, 3)), // north
            FaceIndex.from(listOf(1, 5, 4, 2), listOf(0, 1, 2, 3)), // south
            FaceIndex.from(listOf(2, 4, 7, 3), listOf(0, 1, 2, 3)), // west
            FaceIndex.from(listOf(6, 5, 1, 0), listOf(0, 1, 2, 3))  // east
        )
        return Mesh(pos, tex, faces)
    }
}