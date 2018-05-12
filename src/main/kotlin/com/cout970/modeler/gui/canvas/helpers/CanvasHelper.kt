package com.cout970.modeler.gui.canvas.helpers

import com.cout970.modeler.core.helpers.PickupHelper
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.gui.canvas.SceneSpaceContext
import com.cout970.modeler.util.absolutePositionV
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJOML
import com.cout970.modeler.util.toJoml3d
import com.cout970.raytrace.Ray
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.vec3Of
import org.joml.Vector3d

/**
 * Created by cout970 on 2017/06/13.
 */
object CanvasHelper {

    fun getContext(canvas: Canvas, pos: Pair<IVector2, IVector2>): Pair<SceneSpaceContext, SceneSpaceContext> {
        val oldContext = getMouseSpaceContext(canvas, pos.first)
        val newContext = getMouseSpaceContext(canvas, pos.second)
        return newContext to oldContext
    }

    fun getMouseSpaceContext(canvas: Canvas, absMousePos: IVector2): SceneSpaceContext {
        val matrix = canvas.cameraHandler.camera.getMatrix(canvas.size.toIVector()).toJOML()
        val mousePos = absMousePos - canvas.absolutePositionV
        val mouseRay = PickupHelper.getMouseRay(canvas, mousePos)
        return SceneSpaceContext(mousePos, mouseRay, matrix)
    }

    fun getContextForOrientationCube(canvas: Canvas, start: IVector2, viewportSize: IVector2, absMousePos: IVector2)
            : SceneSpaceContext {

        val matrix = canvas.cameraHandler.camera.getMatrixForOrientationCube().toJOML()
        val mousePos = absMousePos - start
        val viewport = intArrayOf(0, 0, viewportSize.xi, viewportSize.yi)

        val a = matrix.unproject(vec3Of(mousePos.x, viewportSize.yd - mousePos.yd, 0.0).toJoml3d(),
                viewport, Vector3d()).toIVector()
        val b = matrix.unproject(vec3Of(mousePos.x, viewportSize.yd - mousePos.yd, 1.0).toJoml3d(),
                viewport, Vector3d()).toIVector()

        val mouseRay = Ray(a, b)

        return SceneSpaceContext(mousePos, mouseRay, matrix)
    }
}