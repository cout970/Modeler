package com.cout970.modeler.newView.selector

import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.newView.TransformationMode
import com.cout970.modeler.newView.gui.Scene
import com.cout970.modeler.util.toIVector
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3

/**
 * Created by cout970 on 2017/04/09.
 */
class Cursor(val scene: Scene, val modelEditor: ModelEditor) {

    var enable = false
    var center: IVector3 = Vector3.ORIGIN

    val transformationMode: TransformationMode
        get() = scene.contentPanel.controllerState.transformationMode
    val parameters: CursorParameters
        get() = CursorParameters.create(scene.cameraHandler.camera.zoom, scene.size.toIVector())

    private val axis = mapOf(
            TransformationMode.TRANSLATION to listOf(
                    CursorPartTranslate(this, Vector3.X_AXIS, Vector3.X_AXIS),
                    CursorPartTranslate(this, Vector3.Y_AXIS, Vector3.Y_AXIS),
                    CursorPartTranslate(this, Vector3.Z_AXIS, Vector3.Z_AXIS)
            ),
            TransformationMode.ROTATION to listOf(
                    CursorPartRotation(this, Vector3.X_AXIS, Vector3.Z_AXIS, Vector3.X_AXIS),
                    CursorPartRotation(this, Vector3.Y_AXIS, Vector3.X_AXIS, Vector3.Y_AXIS),
                    CursorPartRotation(this, Vector3.Z_AXIS, Vector3.Y_AXIS, Vector3.Z_AXIS)
            ),
            TransformationMode.SCALE to listOf(
                    CursorPartScale(this, Vector3.X_AXIS, Vector3.X_AXIS),
                    CursorPartScale(this, Vector3.Y_AXIS, Vector3.Y_AXIS),
                    CursorPartScale(this, Vector3.Z_AXIS, Vector3.Z_AXIS)
            )
    )

    fun getSubParts(): List<ISelectable> {
        if (!enable) return emptyList()
        return axis[transformationMode]!!
    }
}
