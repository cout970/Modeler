package com.cout970.modeler.view.newView.selector

import com.cout970.modeler.to_redo.modeleditor.ModelEditor
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.newView.TransformationMode
import com.cout970.modeler.view.newView.gui.ContentPanel
import com.cout970.modeler.view.newView.gui.Scene
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3

/**
 * Created by cout970 on 2017/04/09.
 */
class Cursor(val contentPanel: ContentPanel, val modelEditor: ModelEditor) {

    var enable = false
    var center: IVector3 = Vector3.ORIGIN

    val transformationMode: TransformationMode
        get() = contentPanel.controllerState.transformationMode

    fun getCursorParameters(scene: Scene): CursorParameters {
        return CursorParameters.create(scene.cameraHandler.camera.zoom, scene.size.toIVector())
    }

    private fun getParts(mode: TransformationMode, scene: Scene): List<CursorPart> {
        return when (mode) {
            TransformationMode.TRANSLATION -> listOf(
                    CursorPartTranslate(this, scene, Vector3.X_AXIS, Vector3.X_AXIS),
                    CursorPartTranslate(this, scene, Vector3.Y_AXIS, Vector3.Y_AXIS),
                    CursorPartTranslate(this, scene, Vector3.Z_AXIS, Vector3.Z_AXIS)
            )
            TransformationMode.ROTATION -> listOf(
                    CursorPartRotation(this, scene, Vector3.X_AXIS, Vector3.Z_AXIS, Vector3.X_AXIS),
                    CursorPartRotation(this, scene, Vector3.Y_AXIS, Vector3.X_AXIS, Vector3.Y_AXIS),
                    CursorPartRotation(this, scene, Vector3.Z_AXIS, Vector3.Y_AXIS, Vector3.Z_AXIS)
            )
            TransformationMode.SCALE -> listOf(
                    CursorPartScale(this, scene, Vector3.X_AXIS, Vector3.X_AXIS),
                    CursorPartScale(this, scene, Vector3.Y_AXIS, Vector3.Y_AXIS),
                    CursorPartScale(this, scene, Vector3.Z_AXIS, Vector3.Z_AXIS)
            )
        }
    }

    fun getSubParts(scene: Scene): List<ISelectable> {
        if (!enable) return emptyList()
        return getParts(transformationMode, scene)
    }

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
