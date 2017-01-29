package com.cout970.modeler.modeleditor

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.QuadIndices
import com.cout970.modeler.modeleditor.selection.ITextureSelection
import com.cout970.modeler.modeleditor.selection.TextureSelectionMode
import com.cout970.modeler.util.applyMesh
import com.cout970.modeler.view.controller.SelectionAxis
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

fun Mesh.setUVFromCuboid(size: IVector3, offset: IVector2, textureSize: IVector2): Mesh {
    val uvs = generateUVs(size, offset, textureSize)
    return Mesh.quadsToMesh(getQuads().mapIndexed { index, quad ->
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

fun Model.moveTexture(selection: ITextureSelection, axis: SelectionAxis, offset: Float): Model {
    val newModel = when (selection.textureMode) {
        TextureSelectionMode.QUAD -> {
            applyMesh(selection) { mesh ->
                val pathToThisComponent = selection.paths.filter { it.getMesh(this) == mesh }
                val selectedIndices = pathToThisComponent.map { mesh.indices[it.quad] }.flatMap { it.textureCoords }

                mesh.copy(textures = mesh.textures.mapIndexed { i, pos ->
                    if (i in selectedIndices) {
                        pos + axis.axis * offset
                    } else {
                        pos
                    }
                })
            }
        }
        TextureSelectionMode.VERTEX -> {
            applyMesh(selection) { mesh ->
                val pathToThisComponent = selection.paths.filter { it.getMesh(this) == mesh }
                val selectedIndices = pathToThisComponent.map { mesh.indices[it.quad].positions[it.vertex] }

                mesh.copy(textures = mesh.textures.mapIndexed { i, pos ->
                    if (i in selectedIndices) {
                        pos + axis.axis * offset
                    } else {
                        pos
                    }
                })
            }
        }
        else -> this
    }
    return newModel
}

fun Model.splitUV(selection: ITextureSelection): Model {
    return applyMesh(selection) { mesh ->
        val pathToThisComponent = selection.paths.filter { it.getMesh(this) == mesh }
        val selectedIndices = if (selection.textureMode == TextureSelectionMode.VERTEX) {
            pathToThisComponent.map { mesh.indices[it.quad].positions[it.vertex] }
        } else {
            pathToThisComponent.map { mesh.indices[it.quad] }.flatMap { it.textureCoords }
        }
        val indexMap = mutableMapOf<Pair<QuadIndices, Int>, Int>()
        val newTextureList = mutableListOf<IVector2>()
        for (quad in mesh.indices) {
            for (i in quad.textureCoords) {
                if (i in selectedIndices) {
                    indexMap += (quad to i) to newTextureList.size
                    newTextureList += mesh.textures[i]
                } else {
                    val aux = mesh.textures[i]
                    if (aux in newTextureList) {
                        indexMap += (quad to i) to newTextureList.indexOf(aux)
                    } else {
                        indexMap += (quad to i) to newTextureList.size
                        newTextureList += mesh.textures[i]
                    }
                }
            }
        }
        Mesh(mesh.positions, newTextureList, mesh.indices.map { indices ->
            QuadIndices(
                    indices.aP, indexMap[indices to indices.aT]!!,
                    indices.bP, indexMap[indices to indices.bT]!!,
                    indices.cP, indexMap[indices to indices.cT]!!,
                    indices.dP, indexMap[indices to indices.dT]!!
            )
        })
    }
}