package com.cout970.modeler.util

import com.cout970.glutilities.tessellator.Tessellator
import com.cout970.modeler.model.Quad
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2016/12/09.
 */
object RenderUtil {

    fun renderBar(tessellator: Tessellator, a: IVector3, b: IVector3, d: Double = 0.015675, col: IVector3 = vec3Of(1, 1, 0)) {
        tessellator.apply {
            //-x
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            //+x
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            //-y
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            //+y
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            //-z
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            //+z
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
        }
    }
}

fun Quad.center(): IVector3 {
    val ab = (b.pos + a.pos) / 2
    val cd = (d.pos + c.pos) / 2
    return (ab + cd) / 2
}

fun Iterable<IVector3>.middle(): IVector3 {
    var sum: IVector3? = null
    var count = 0
    for (i in this) {
        if (sum == null) sum = i else sum += i
        count++
    }
    if (sum == null) return vec3Of(0)
    return sum / count
}