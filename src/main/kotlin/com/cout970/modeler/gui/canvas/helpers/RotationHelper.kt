package com.cout970.modeler.gui.canvas.helpers

import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.helpers.PickupHelper
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.gui.canvas.IRotable
import com.cout970.modeler.gui.canvas.SceneSpaceContext
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.getClosestPointOnLineSegment
import com.cout970.modeler.util.toVector2
import com.cout970.raytrace.Ray
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/04/08.
 */
object RotationHelper {

    fun getOffsetGlobal(obj: IRotable, canvas: Canvas, pos: Pair<IVector2, IVector2>, input: IInput): Float {
        return if (canvas.viewMode == SelectionTarget.TEXTURE) {
            val start = getAngle(canvas, pos.first, obj)
            val end = getAngle(canvas, pos.second, obj)
            val change = end - start

            getOffsetFromChange(-change, input)
        } else {
            val context = CanvasHelper.getContext(canvas, pos)
            val change = getAngle(context.first, context.second, obj)

            getOffsetFromChange(-change, input)
        }
    }

    fun getOffsetFromChange(change: Double, input: IInput): Float {
        val move = Math.toDegrees(change) / 360.0 * 32 * Config.cursorRotationSpeed
        val offset: Float

        offset = when {
            Config.keyBindings.disableGridMotion.check(input) -> Math.round(move * 16) / 16f
            Config.keyBindings.disablePixelGridMotion.check(input) -> Math.round(move * 4) / 4f
            else -> Math.round(move).toFloat()
        }
        return Math.toRadians(offset.toDouble() * 360.0 / 32).toFloat()
    }

    fun getAngle(canvas: Canvas, mouse: IVector2, obj: IRotable): Double {
        val clickPos = PickupHelper.getMousePosAbsolute(canvas, mouse)

        val center = obj.center.toVector2()
        val direction = (center - clickPos).normalize()

        return Math.atan2(direction.xd, direction.yd)
    }

    fun getAngle(oldContext: SceneSpaceContext, newContext: SceneSpaceContext, obj: IRotable): Double {
        val normal = obj.tangent
        val new = getDirectionToMouse(oldContext.mouseRay, obj)
        val old = getDirectionToMouse(newContext.mouseRay, obj)

        var angle = (normal cross new) angle (normal cross old)

        if (normal dot (new cross old) < 0) { // Or > 0
            angle = -angle
        }

        return angle
    }

    fun getDirectionToMouse(mouseRay: Ray, obj: IRotable): IVector3 {
        val closest = getClosestPointOnLineSegment(mouseRay.start, mouseRay.end, obj.center)
        return (closest - obj.center).normalize()
    }
}