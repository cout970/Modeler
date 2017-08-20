package com.cout970.modeler.view.canvas.input

import com.cout970.modeler.util.getClosest
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.canvas.Canvas
import com.cout970.modeler.view.canvas.ISelectable
import com.cout970.modeler.view.canvas.SceneSpaceContext
import com.cout970.modeler.view.canvas.cursor.Cursor
import com.cout970.modeler.view.canvas.helpers.CanvasHelper
import com.cout970.raytrace.RayTraceResult

/**
 * Created by cout970 on 2017/08/16.
 */

object Hover {

    fun getHoveredObject(gui: Gui, canvas: Canvas, cursor: Cursor): ISelectable? {

        val context = CanvasHelper.getMouseSpaceContext(canvas, gui.input.mouse.getMousePos())
        val objects = cursor.getSelectableParts(gui, canvas)

        return getHoveredObject(context, objects)
    }

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