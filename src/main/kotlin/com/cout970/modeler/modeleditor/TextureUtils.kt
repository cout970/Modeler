package com.cout970.modeler.modeleditor

import com.cout970.modeler.model.ElementLeaf
import com.cout970.modeler.model.Meshes
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.selection.VertexTexSelection
import com.cout970.modeler.util.rotateAround
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/01/28.
 */

fun Quad.setTexture0(uv0: IVector2, uv1: IVector2): Quad {
    return Quad(
            a.copy(tex = vec2Of(uv1.x, uv0.y)),
            b.copy(tex = vec2Of(uv0.x, uv0.y)),
            c.copy(tex = vec2Of(uv0.x, uv1.y)),
            d.copy(tex = vec2Of(uv1.x, uv1.y))
    )
}

fun Quad.setTexture1(uv0: IVector2, uv1: IVector2): Quad {
    return Quad(
            a.copy(tex = vec2Of(uv1.x, uv1.y)),
            b.copy(tex = vec2Of(uv1.x, uv0.y)),
            c.copy(tex = vec2Of(uv0.x, uv0.y)),
            d.copy(tex = vec2Of(uv0.x, uv1.y))
    )
}

fun ElementLeaf.setUVFromCuboid(size: IVector3, offset: IVector2, textureSize: IVector2): ElementLeaf {
    val uvs = generateUVs(size, offset, textureSize)
    return Meshes.quadsToMesh(getQuads().mapIndexed { index, quad ->
        val flag = when (index) {
            0 -> false; 1 -> true
            2 -> true; 3 -> false
            4 -> false; 5 -> true
            else -> false
        }
        if (flag) {
            quad.setTexture0(uvs[index * 2], uvs[index * 2 + 1])
        } else {
            quad.setTexture1(uvs[index * 2], uvs[index * 2 + 1])
        }
    })
}

private fun generateUVs(size: IVector3, offset: IVector2, textureSize: IVector2): List<IVector2> {
    val width = size.xd
    val height = size.yd
    val length = size.zd

    val offsetX = offset.xd
    val offsetY = offset.yd

    val texelSize = vec2Of(1) / textureSize

    return listOf(
            //-y
            vec2Of(offsetX + length + width, offsetY) * texelSize,
            vec2Of(offsetX + length + width + width, offsetY + length) * texelSize,
            //+y
            vec2Of(offsetX + length + width, offsetY + length) * texelSize,
            vec2Of(offsetX + length, offsetY) * texelSize,
            //-z
            vec2Of(offsetX + length + width + length, offsetY + length) * texelSize,
            vec2Of(offsetX + length + width + length + width, offsetY + length + height) * texelSize,
            //+z
            vec2Of(offsetX + length, offsetY + length) * texelSize,
            vec2Of(offsetX + length + width, offsetY + length + height) * texelSize,
            //-x
            vec2Of(offsetX, offsetY + length) * texelSize,
            vec2Of(offsetX + length, offsetY + length + height) * texelSize,
            //+x
            vec2Of(offsetX + length + width, offsetY + length) * texelSize,
            vec2Of(offsetX + length + width + length, offsetY + length + height) * texelSize
    )
}

fun Model.moveTexture(selection: VertexTexSelection, translation: IVector3): Model {
    return applyToSelectedTextureVertices(selection) { pos -> pos + translation }
}

fun Model.rotateTexture(selection: VertexTexSelection, center: IVector3, rotation: Double): Model {
    return applyToSelectedTextureVertices(selection) { pos -> pos.rotateAround(center, rotation) }
}

fun Model.scaleTexture(selection: VertexTexSelection, scale: IVector3): Model {
    return applyToSelectedTextureVertices(selection) { pos -> pos * scale }
}

fun Model.applyToSelectedTextureVertices(selection: VertexTexSelection, func: (IVector2) -> IVector2): Model {
    //TODO reimplement this, again...
    return this
//    return this.applyVertexPos(selection) { path, vertex ->
//        vertex.transformTex(func)
//    }
}

//TODO test if the changes make it work
fun Model.splitUV(selection: VertexTexSelection): Model {
//    return applyElementLeaves(selection) { path, element ->
//        val pathToThisComponent = selection.filterPaths(path)
//
//        val selectedVertex = if (selection is VertexSelection) {
//            pathToThisComponent
//                    .map { it as VertexPath }
//                    .map { it.vertexIndex }
//        } else {
//            pathToThisComponent
//                    .flatMap { it.getSubPaths(this) }
//                    .map { it as VertexPath }
//                    .map { it.vertexIndex }
//        }
//
//        val indexMap = mutableMapOf<Pair<QuadIndex, Int>, Int>()
//        val newVertexList = mutableListOf<VertexIndex>()
//
//        for (quad in element.faces) {
//            for (i in quad.indices) {
//                if (i in selectedVertex) {
//                    indexMap += (quad to i) to newVertexList.size
//                    newVertexList += element.vertex[i]
//                } else {
//                    val aux = element.vertex[i]
//                    if (aux in newVertexList) {
//                        indexMap += (quad to i) to newVertexList.indexOf(aux)
//                    } else {
//                        indexMap += (quad to i) to newVertexList.size
//                        newVertexList += element.vertex[i]
//                    }
//                }
//            }
//        }
//
//        ElementLeaf(element.positions, element.textures, newVertexList,
//                element.faces.map { indices ->
//                    QuadIndex(
//                            indexMap[indices to indices.a]!!,
//                            indexMap[indices to indices.b]!!,
//                            indexMap[indices to indices.c]!!,
//                            indexMap[indices to indices.d]!!
//                    )
//                })
//    }
    return this
}