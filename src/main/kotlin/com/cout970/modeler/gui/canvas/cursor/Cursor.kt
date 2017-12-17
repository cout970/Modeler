package com.cout970.modeler.gui.canvas.cursor


import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.gui.canvas.ISelectable
import com.cout970.modeler.gui.canvas.TransformationMode
import com.cout970.modeler.render.tool.camera.Camera
import com.cout970.modeler.util.toIVector
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3

/**
 * Created by cout970 on 2017/04/09.
 */
class Cursor(val center: IVector3 = Vector3.ORIGIN) {


    fun getCursorParameters(camera: Camera, viewport: IVector2): CursorParameters =
            CursorParameters.create(camera.zoom, viewport)

    fun getSelectablePartsModel(gui: Gui, canvas: Canvas): List<ISelectable> {
        val camera = canvas.cameraHandler.camera
        val viewport = canvas.size.toIVector()

        return getSelectablePartsModel(gui, camera, viewport)
    }

    fun getSelectablePartsTexture(gui: Gui, canvas: Canvas): List<ISelectable> {
        val camera = canvas.cameraHandler.camera
        val viewport = canvas.size.toIVector()

        return getSelectablePartsTexture(gui, camera, viewport)
    }

    fun getSelectablePartsModel(gui: Gui, camera: Camera, viewport: IVector2): List<ISelectable> {
        val parameters = CursorParameters.create(camera.zoom, viewport)

        return when (gui.state.transformationMode) {
            TransformationMode.TRANSLATION -> listOf(
                    CursorPartTranslateModel(this, parameters, Vector3.X_AXIS),
                    CursorPartTranslateModel(this, parameters, Vector3.Y_AXIS),
                    CursorPartTranslateModel(this, parameters, Vector3.Z_AXIS)
            )
            TransformationMode.ROTATION -> listOf(
                    CursorPartRotateModel(this, parameters, Vector3.X_AXIS, Vector3.Y_AXIS),
                    CursorPartRotateModel(this, parameters, Vector3.Y_AXIS, Vector3.Z_AXIS),
                    CursorPartRotateModel(this, parameters, Vector3.Z_AXIS, Vector3.X_AXIS)
            )
            TransformationMode.SCALE -> listOf(
                    CursorPartScaleModel(this, parameters, Vector3.X_AXIS),
                    CursorPartScaleModel(this, parameters, Vector3.Y_AXIS),
                    CursorPartScaleModel(this, parameters, Vector3.Z_AXIS)
            )
        }
    }

    fun getSelectablePartsTexture(gui: Gui, camera: Camera, viewport: IVector2): List<ISelectable> {
        val parameters = CursorParameters.create(camera.zoom, viewport)

        return when (gui.state.transformationMode) {
            TransformationMode.TRANSLATION -> listOf(
                    CursorPartTranslateTexture(this, parameters, Vector3.X_AXIS),
                    CursorPartTranslateTexture(this, parameters, Vector3.Y_AXIS)
            )
            TransformationMode.ROTATION -> listOf(
                    CursorPartRotateTexture(this, parameters)
            )
            TransformationMode.SCALE -> listOf(
                    CursorPartScaleTexture(this, parameters, Vector3.X_AXIS),
                    CursorPartScaleTexture(this, parameters, Vector3.Y_AXIS)
            )
        }
    }
}
