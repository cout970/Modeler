package com.cout970.modeler.gui.canvas.input

import com.cout970.modeler.gui.canvas.ISelectable
import com.cout970.modeler.gui.canvas.SceneSpaceContext
import com.cout970.modeler.util.getClosest
import com.cout970.raytrace.RayTraceResult

/**
 * Created by cout970 on 2017/08/16.
 */

object Hover {

    fun getHoveredObject(ctx: SceneSpaceContext, objs: List<ISelectable>): ISelectable? {

        val ray = ctx.mouseRay
        val list = mutableListOf<Pair<RayTraceResult, ISelectable>>()

        objs.forEach { obj ->
            val res = obj.hitbox.rayTrace(ray)
            res?.let { list += it to obj }
        }

        return list.getClosest(ray)?.second
    }
}