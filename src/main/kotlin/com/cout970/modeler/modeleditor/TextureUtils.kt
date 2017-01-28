package com.cout970.modeler.modeleditor

import com.cout970.modeler.model.Mesh
import com.cout970.modeler.model.Quad
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