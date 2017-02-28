package com.cout970.modeler.model.structure

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.api.IElementLeaf
import com.cout970.modeler.model.util.getElement
import com.cout970.modeler.selection.VertexPath
import com.cout970.modeler.selection.VertexPosSelection

/**
 * Created by cout970 on 2017/02/28.
 */

fun Model.zipVertexPaths(sel: VertexPosSelection): VertexStructurePath {
    val quadList = mutableListOf<VertexStructurePath.QuadPath>()
    val edgeList = mutableListOf<VertexStructurePath.EdgePath>()

    val vertexInQuads = mutableListOf<VertexPath>()
    val paths = sel.paths

    // Get all quads
    for ((elemPath, vertex) in paths) {
        val elem = getElement(elemPath) as IElementLeaf
        val indices = vertex.map { it.vertexIndex }

        val facesWithAllVertexInIndices = elem.faces.filter { it.pos.all { it in indices } }
        facesWithAllVertexInIndices.forEach { face ->

            vertexInQuads += face.pos.map { VertexPath(elemPath, it) }
            quadList += VertexStructurePath.QuadPath(listOf(
                    VertexPath(elemPath, face.a.first) to VertexPath(elemPath, face.a.second),
                    VertexPath(elemPath, face.b.first) to VertexPath(elemPath, face.b.second),
                    VertexPath(elemPath, face.c.first) to VertexPath(elemPath, face.c.second),
                    VertexPath(elemPath, face.d.first) to VertexPath(elemPath, face.d.second)
            ))
        }
    }

    val vertexInEdges = mutableListOf<VertexPath>()

    //Get all edges
    for ((elemPath, vertex) in paths) {
        val elem = getElement(elemPath) as IElementLeaf
        val indices = vertex.filter { it !in vertexInQuads }.map { it.vertexIndex }

        val edges = elem.faces.flatMap { it.edges }

        val edgesWithAllVertexInIndices = edges.filter { it.a.first in indices && it.b.first in indices }
        edgesWithAllVertexInIndices.forEach { (a, b) ->
            vertexInEdges += VertexPath(elemPath, a.first)
            vertexInEdges += VertexPath(elemPath, b.first)

            val first = VertexPath(elemPath, a.first) to VertexPath(elemPath, a.second)
            val second = VertexPath(elemPath, b.first) to VertexPath(elemPath, b.second)
            edgeList += VertexStructurePath.EdgePath(first, second)
        }
    }

    // Get remaining vertex
    val vertexInQuadsOrEdges = vertexInQuads + vertexInEdges
    val vertexList = sel.pathList.filter { it !in vertexInQuadsOrEdges }.map { it to VertexPath(it.elementPath, 0) }

    return VertexStructurePath(
            quadList,
            edgeList,
            vertexList
    )
}