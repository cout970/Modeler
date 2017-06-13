package com.cout970.modeler.controller.selector


import com.cout970.modeler.controller.ProjectController
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.gui.comp.canvas.Canvas
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3

/**
 * Created by cout970 on 2017/04/09.
 */
class Cursor(val projectController: ProjectController) {

    var enable = false
    var center: IVector3 = Vector3.ORIGIN

    val transformationMode: TransformationMode
        get() = projectController.guiState.transformationMode

    fun getCursorParameters(canvas: Canvas): CursorParameters {
        return CursorParameters.create(canvas.cameraHandler.camera.zoom, canvas.size.toIVector())
    }

//    private fun getParts(mode: TransformationMode, canvas: Canvas): List<CursorPart> {
//        return when (mode) {
//            TransformationMode.TRANSLATION -> listOf(
//                    CursorPartTranslate(this, canvas, Vector3.X_AXIS, Vector3.X_AXIS),
//                    CursorPartTranslate(this, canvas, Vector3.Y_AXIS, Vector3.Y_AXIS),
//                    CursorPartTranslate(this, canvas, Vector3.Z_AXIS, Vector3.Z_AXIS)
//            )
//            TransformationMode.ROTATION -> listOf(
//                    CursorPartRotation(this, canvas, Vector3.X_AXIS, Vector3.Z_AXIS, Vector3.X_AXIS),
//                    CursorPartRotation(this, canvas, Vector3.Y_AXIS, Vector3.X_AXIS, Vector3.Y_AXIS),
//                    CursorPartRotation(this, canvas, Vector3.Z_AXIS, Vector3.Y_AXIS, Vector3.Z_AXIS)
//            )
//            TransformationMode.SCALE -> listOf(
//                    CursorPartScale(this, canvas, Vector3.X_AXIS, Vector3.X_AXIS),
//                    CursorPartScale(this, canvas, Vector3.Y_AXIS, Vector3.Y_AXIS),
//                    CursorPartScale(this, canvas, Vector3.Z_AXIS, Vector3.Z_AXIS)
//            )
//        }
//    }

//    fun getSubParts(canvas: Canvas): List<ISelectable> {
//        if (!enable) return emptyList()
//        return getParts(transformationMode, canvas)
//    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cursor) return false

        if (enable != other.enable) return false
        if (center != other.center) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enable.hashCode()
        result = 31 * result + center.hashCode()
        return result
    }
}
