package com.cout970.modeler.model.freemodel

import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.Vertex

/**
 * Created by cout970 on 2017/02/11.
 */

operator fun IElementGroup.get(index: Int): Element = elements[index]

fun FreeModel.getElement(path: ElementPath): Element {
    var element: Element = elements[path.indices[0]]

    for (level in 1 until path.indices.size) {

        if (element !is IElementGroup)
            throw IllegalArgumentException("Invalid path: $path, level $level is not a group")

        val subIndex = path.indices[level]
        element = element[subIndex]
    }
    return element
}

fun FreeModel.getQuads(path: ElementPath): List<Quad> {
    return getElement(path).getQuads()
}

@Suppress("FoldInitializerAndIfToElvis")
fun FreeModel.getVertex(path: VertexPath): Vertex {
    val element = getElement(path)

    if (element !is IElementObject)
        throw IllegalArgumentException("Invalid path: $path, $element is not a ElementObject")

    val vertexIndex = element.vertex[path.vertexIndex]

    return vertexIndex.toVertex(element)
}

fun IElementGroup.getObjectPaths(subPath: ElementPath = ElementPath(intArrayOf())): List<ElementPath> {
    val paths = mutableListOf<ElementPath>()

    for ((index, elem) in elements.withIndex()) {
        val newSubPath = ElementPath(subPath.indices + index)

        if (elem is IElementGroup) {
            paths += elem.getObjectPaths(subPath)
        } else {
            paths += newSubPath
        }
    }
    return paths
}


fun FreeModel.apply(selection: Selection, func: (VertexPath, Vertex) -> Vertex): FreeModel {
    return copy(
            elements = elements.mapIndexed { i, element ->
                element.apply(ElementPath(intArrayOf(i)), selection, func)
            }
    )
}

fun Element.apply(path: ElementPath, selection: Selection, func: (VertexPath, Vertex) -> Vertex): Element {
    if (this is IElementGroup) {
        return deepCopy(elements.mapIndexed { i, element ->
            element.apply(ElementPath(path.indices + i), selection, func)
        })
    } else if (this is IElementObject) {
        return applyObject(path, selection, func)
    } else {
        throw IllegalStateException("Class ${this.javaClass} is not IElementGroup nor IElementObject")
    }
}

fun IElementObject.applyObject(path: ElementPath, selection: Selection,
                               func: (VertexPath, Vertex) -> Vertex): IElementObject {

    val affectedPaths = selection.filterPaths(path).mapNotNull { it as VertexPath }
    if (affectedPaths.isEmpty()) return this

    val newVertex = vertex.mapIndexed { i, vertexIndex ->
        val subPath = affectedPaths.find { it.vertexIndex == i } as? VertexPath
        val vertex = Vertex(positions[vertexIndex.pos], textures[vertexIndex.tex])
        if (subPath != null) {
            func(subPath, vertex)
        } else {
            vertex
        }
    }
    return updateVertex(newVertex)
}