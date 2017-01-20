package com.cout970.modeler.view.scene

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IEventController
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.modeleditor.selection.SelectionNone
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toRads
import com.cout970.modeler.view.controller.ModelSelector
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.controller.TransformationMode
import com.cout970.modeler.view.render.RenderManager
import com.cout970.modeler.window.WindowHandler
import com.cout970.vector.extensions.vec2Of
import org.joml.Matrix4d

class ModelScene(modelEditor: ModelEditor, windowHandler: WindowHandler, sceneController: SceneController) : Scene(
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

    override fun render(renderManager: RenderManager) {
        if (size.x < 1 || size.y < 1) return

        renderManager.modelRenderer.run {
            matrixP = getProjectionMatrix()
            matrixV = getViewMatrix()

            val selector = modelSelector
            val model = modelProvider.model
            val selection = modelProvider.selectionManager.selection

            if (modelProvider.modelNeedRedraw) {
                modelProvider.modelNeedRedraw = false
                sceneController.modelCache.clear()
                sceneController.selectionCache.clear()
            }

            val y = parent.size.y - (position.y + size.y)
            windowHandler.saveViewport(vec2Of(absolutePosition.x, y), vec2Of(size.x, size.y)) {

                renderModel(sceneController.getModel(model), sceneController.modelCache,
                        selection, sceneController.selectionCache)

                drawGrids()
                if (selection != SelectionNone && selector.transformationMode != TransformationMode.NONE) {
                    when (selector.transformationMode) {
                        TransformationMode.TRANSLATION -> {
                            renderTranslation(sceneController.cursorCenter, selector, selection, camera)
                        }
                        TransformationMode.ROTATION -> {
                            renderRotation(selection.getCenter(model), selector, selection, camera)
                        }
                        else -> {
                        }
                    }
                }
                if (Config.keyBindings.moveCamera.check(sceneController.input) ||
                    Config.keyBindings.rotateCamera.check(sceneController.input)) {
                    renderCursor(vec2Of(size.x, size.y))
                }
            }
        }
    }

    override fun registerListeners(eventHandler: IEventController) {
        modelSelector.registerListeners(eventHandler)
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