package com.cout970.modeler.model.util

import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.Vertex
import com.cout970.modeler.model.api.IElement
import com.cout970.modeler.model.api.IElementGroup
import com.cout970.modeler.model.api.IElementLeaf
import com.cout970.modeler.selection.ElementPath
import com.cout970.modeler.selection.VertexPath
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3

/**
 * Created by cout970 on 2017/02/27.
 */

operator fun IElementGroup.get(index: Int): IElement = elements[index]

fun Model.getElement(path: ElementPath): IElement {
    var element: IElement = elements[path.indices[0]]

    for (level in 1 until path.indices.size) {

        if (element !is IElementGroup)
            throw IllegalArgumentException("Invalid path: $path, level $level is not a group")

        val subIndex = path.indices[level]
        element = element[subIndex]
    }
    return element
}

fun Model.getQuads(path: ElementPath): List<Quad> {
    return getElement(path).getQuads()
}


fun Model.getVertex(pos: VertexPath, tex: VertexPath): Vertex {
    val vertexPos = getVertexPos(pos)
    val vertexTex = getVertexTex(tex)
    return Vertex(vertexPos, vertexTex)
}

fun Model.getVertexPos(path: VertexPath): IVector3 {
    val element = getElement(path.elementPath)

    if (element !is IElementLeaf)
        throw IllegalArgumentException("Invalid path: $path, $element is not a ElementObject")

    return element.positions[path.vertexIndex]
}

fun Model.getVertexTex(path: VertexPath): IVector2 {
    val element = getElement(path.elementPath)

    if (element !is IElementLeaf)
        throw IllegalArgumentException("Invalid path: $path, $element is not a ElementObject")

    return element.textures[path.vertexIndex]
}

fun Model.getLeafElements(): List<IElementLeaf> {
    return getLeafPaths().map { getElement(it) as IElementLeaf }
}

fun Model.getLeafPaths(): List<ElementPath> {
    val paths = mutableListOf<ElementPath>()
    for ((index, elem) in elements.withIndex()) {
        if (elem is IElementGroup) {
            paths += elem.getLeafPaths(ElementPath(intArrayOf(index)))
        } else {
            paths += ElementPath(intArrayOf(index))
        }
    }
    return paths
}

fun IElementGroup.getLeafPaths(subPath: ElementPath = ElementPath(intArrayOf())): List<ElementPath> {
    val paths = mutableListOf<ElementPath>()

    for ((index, elem) in elements.withIndex()) {
        val newSubPath = ElementPath(subPath.indices + index)

        if (elem is IElementGroup) {
            paths += elem.getLeafPaths(subPath)
        } else {
            paths += newSubPath
        }
    }
    return paths
}
