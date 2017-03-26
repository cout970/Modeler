package com.cout970.modeler.view.scene

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.config.Config
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.util.MatrixUtils
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toRads
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.window.WindowHandler
import org.joml.Matrix4d

class Scene3d(modelEditor: ModelEditor, windowHandler: WindowHandler, sceneController: SceneController) : Scene(
        modelEditor, windowHandler, sceneController) {

    override var perspective = true

    fun getProjectionMatrix(): IMatrix4 {
        if (perspective) {
            return Matrix4d().setPerspective(Config.perspectiveFov.toRads(), (size.x / size.y).toDouble(), 0.1,
                    1000.0).toIMatrix()
        } else {
            return MatrixUtils.createOrthoMatrix(size.toIVector())
        }
    }

    fun getViewMatrix(): IMatrix4 {
        if (perspective) {
            return camera.matrixForPerspective
        } else {
            return camera.matrixForOrtho
        }
    }

    override fun getMatrixMVP(): IMatrix4 {
        return getProjectionMatrix() * getViewMatrix()
    }
}