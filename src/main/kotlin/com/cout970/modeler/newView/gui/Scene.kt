package com.cout970.modeler.newView.gui

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.config.Config
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.newView.CameraHandler
import com.cout970.modeler.newView.gui.comp.CBorderRenderer
import com.cout970.modeler.newView.gui.comp.CPanel
import com.cout970.modeler.newView.selector.Cursor
import com.cout970.modeler.newView.viewtarget.ViewTarget
import com.cout970.modeler.util.MatrixUtils
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toRads
import org.joml.Matrix4d
import org.liquidengine.legui.util.ColorConstants

/**
 * Created by cout970 on 2017/04/08.
 */
class Scene(
        val contentPanel: ContentPanel,
        var viewTarget: ViewTarget,
        modelEditor: ModelEditor) : CPanel() {

    val cameraHandler = CameraHandler()
    val cursor: Cursor

    var perspective: Boolean = true
        private set

    init {
        cursor = Cursor(this, modelEditor)
        backgroundColor = ColorConstants.transparent()
        border.renderer = CBorderRenderer
    }

    fun getMatrixMVP(): IMatrix4 {
        if (!viewTarget.is3d) {
            return MatrixUtils.createOrthoMatrix(size.toIVector()) * cameraHandler.camera.matrixForUV
        } else {
            val projection: IMatrix4
            val view: IMatrix4

            if (perspective) {
                projection = Matrix4d().setPerspective(Config.perspectiveFov.toRads(),
                        (size.x / size.y).toDouble(), 0.1, 1000.0).toIMatrix()

                view = cameraHandler.camera.matrixForPerspective
            } else {
                projection = MatrixUtils.createOrthoMatrix(size.toIVector())
                view = cameraHandler.camera.matrixForOrtho
            }
            return projection * view
        }
    }
}