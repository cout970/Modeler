package com.cout970.modeler.view.scene

import com.cout970.glutilities.event.EventMouseClick
import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.config.Config
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toRads
import com.cout970.modeler.view.controller.ModelSelector
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.window.WindowHandler
import org.joml.Matrix4d

class Scene3d(modelEditor: ModelEditor, windowHandler: WindowHandler, sceneController: SceneController) : Scene(
        modelEditor, windowHandler, sceneController) {

    val modelSelector = ModelSelector(this, sceneController, modelEditor)
    var perspective = true

    override fun update() {
        super.update()
        modelSelector.update()
        if (sceneController.selectedScene === this) {
            modelSelector.updateUserInput()
        }
    }

    override fun onEvent(e: EventMouseClick): Boolean {
        return modelSelector.onEvent(e)
    }

    fun getProjectionMatrix(): IMatrix4 {
        if (perspective) {
            return Matrix4d().setPerspective(Config.perspectiveFov.toRads(), (size.x / size.y).toDouble(), 0.1,
                    1000.0).toIMatrix()
        } else {
            return createOrthoMatrix()
        }
    }

    fun getViewMatrix(): IMatrix4 {
        if (perspective) {
            return camera.matrixForPerspective
        } else {
            return camera.matrixForOrtho
        }
    }

    fun getMatrixMVP(): IMatrix4 {
        return getProjectionMatrix() * getViewMatrix()
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }

    override fun equals(other: Any?): Boolean {
        return other === this
    }
}