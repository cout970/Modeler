package com.cout970.modeler.gui.canvas.input

import com.cout970.collision.collide
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.gui.canvas.ISelectable
import com.cout970.modeler.gui.canvas.SceneSpaceContext
import com.cout970.modeler.util.getClosest
import com.cout970.modeler.util.getVertexTexturePolygon
import com.cout970.raytrace.RayTraceResult
import com.cout970.vector.api.IVector2

/**
 * Created by cout970 on 2017/08/16.
 */

object Hover {

    fun getHoveredObject(ctx: SceneSpaceContext, objs: List<ISelectable>): ISelectable? {

        val ray = ctx.mouseRay
        val list = mutableListOf<Pair<RayTraceResult, ISelectable>>()

        objs.forEach { obj ->
            val res = obj.hitbox!!.rayTrace(ray)
            res?.let { list += it to obj }
        }

        return list.getClosest(ray)?.second
    }

    fun getHoveredObject(clickPos: IVector2, material: IMaterial, objs: List<ISelectable>): ISelectable? {

        val polygonsPairs = objs.map { it.polygons!! to it }

        val mouseCollisionBox = getVertexTexturePolygon(clickPos)

        val selected = polygonsPairs
                .map { (polygons, select) -> polygons.filter { it.collide(mouseCollisionBox) } to select }
                .filter { it.first.isNotEmpty() }


        val results = selected.map { it.second }.distinct()

        return results.firstOrNull()
    }
}