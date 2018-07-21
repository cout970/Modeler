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
        return getOffsetGlobal(obj.center, obj.tangent, canvas, pos, input)
    }

    fun getOffsetGlobal(center: IVector3, normal: IVector3, canvas: Canvas, pos: Pair<IVector2, IVector2>, input: IInput): Float {
        return if (canvas.viewMode == SelectionTarget.TEXTURE) {
            val start = getAngle(canvas, pos.first, center)
            val end = getAngle(canvas, pos.second, center)
            val change = end - start

            getOffsetFromChange(-change, input)
        } else {
            val context = CanvasHelper.getContext(canvas, pos)
            val change = getAngle(context.first, context.second, center, normal)

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

    fun getAngle(canvas: Canvas, mouse: IVector2, center: IVector3): Double {
        val clickPos = PickupHelper.getMousePosAbsolute(canvas, mouse)

        val direction = (center.toVector2() - clickPos).normalize()

        return Math.atan2(direction.xd, direction.yd)
    }

    fun getAngle(oldContext: SceneSpaceContext, newContext: SceneSpaceContext, center: IVector3, normal: IVector3): Double {
        val new = getDirectionToMouse(oldContext.mouseRay, center)
        val old = getDirectionToMouse(newContext.mouseRay, center)

        var angle = (normal cross new) angle (normal cross old)

        if (normal dot (new cross old) < 0) { // Or > 0
            angle = -angle
        }

        return angle
    }

    fun getDirectionToMouse(mouseRay: Ray, center: IVector3): IVector3 {
        val closest = getClosestPointOnLineSegment(mouseRay.start, mouseRay.end, center)
        return (closest - center).normalize()
    }
}