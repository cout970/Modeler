package com.cout970.modeler.view.canvas.cursor


import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.canvas.Canvas
import com.cout970.modeler.view.canvas.ISelectable
import com.cout970.modeler.view.canvas.TransformationMode
import com.cout970.modeler.view.render.tool.camera.Camera
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3

/**
 * Created by cout970 on 2017/04/09.
 */
class Cursor(val center: IVector3 = Vector3.ORIGIN) {


    fun getCursorParameters(camera: Camera, viewport: IVector2): CursorParameters {
        return CursorParameters.create(camera.zoom, viewport)
    }

    fun getSelectableParts(gui: Gui, canvas: Canvas): List<ISelectable> {
        val camera = canvas.cameraHandler.camera
        val viewport = canvas.size.toIVector()

        return getSelectableParts(gui, camera, viewport)
    }

    fun getSelectableParts(gui: Gui, camera: Camera, viewport: IVector2): List<ISelectable> {
        if (gui.selectionHandler.ref.isEmpty()) {
            return emptyList()
        }
        val parameters = CursorParameters.create(camera.zoom,
                viewport)
        return when (gui.state.transformationMode) {
            TransformationMode.TRANSLATION -> listOf(
                    CursorPartTranslate(this, parameters, Vector3.X_AXIS),
                    CursorPartTranslate(this, parameters, Vector3.Y_AXIS),
                    CursorPartTranslate(this, parameters, Vector3.Z_AXIS)
            )
            TransformationMode.ROTATION -> listOf()
            TransformationMode.SCALE -> listOf()
        }
    }
}
