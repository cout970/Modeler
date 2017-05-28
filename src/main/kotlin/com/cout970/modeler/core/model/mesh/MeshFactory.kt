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

    fun createPlane(size: IVector2): IMesh {
        return Mesh(
                listOf(vec3Of(0, 0, 0), vec3Of(0, 0, 1), vec3Of(1, 0, 1), vec3Of(1, 0, 0))
                        .map { it * vec3Of(size.x, 1, size.y) },
                listOf(vec2Of(0, 0), vec2Of(1, 0), vec2Of(1, 1), vec2Of(0, 1)),

                listOf(FaceIndex(listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2), Pair(3, 3))))
        )
    }

    fun createCube(size: IVector3, offset: IVector3): IMesh {
        val n: IVector3 = offset
        val p: IVector3 = size + offset

        val pos = listOf(
                vec3Of(p.x, n.y, n.z), //0
                vec3Of(p.x, n.y, p.z), //1
                vec3Of(n.x, n.y, p.z), //2
                vec3Of(n.x, n.y, n.z), //3
                vec3Of(n.x, p.y, p.z), //4
                vec3Of(p.x, p.y, p.z), //5
                vec3Of(p.x, p.y, n.z), //6
                vec3Of(n.x, p.y, n.z) //7
        )
        val tex = listOf(vec2Of(0, 0), vec2Of(1, 0), vec2Of(1, 1), vec2Of(0, 1))
        val faces = listOf(
                FaceIndex(listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2), Pair(3, 3))),
                FaceIndex(listOf(Pair(4, 0), Pair(5, 1), Pair(6, 2), Pair(7, 3))),
                FaceIndex(listOf(Pair(7, 0), Pair(6, 1), Pair(0, 2), Pair(3, 3))),
                FaceIndex(listOf(Pair(1, 0), Pair(5, 1), Pair(4, 2), Pair(2, 3))),
                FaceIndex(listOf(Pair(2, 0), Pair(4, 1), Pair(7, 2), Pair(3, 3))),
                FaceIndex(listOf(Pair(6, 0), Pair(5, 1), Pair(1, 2), Pair(0, 3)))
        )
        return Mesh(pos, tex, faces)
    }
}