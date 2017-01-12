package com.cout970.modeler.view.scene

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.EventController
import com.cout970.modeler.modeleditor.selection.SelectionNone
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toRads
import com.cout970.modeler.view.controller.ModelSelector
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.controller.TransformationMode
import com.cout970.modeler.view.render.RenderManager
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.xd
import com.cout970.vector.extensions.yd
import org.joml.Matrix4d

class ModelScene(sceneController: SceneController) : Scene(sceneController) {

    val modelSelector = ModelSelector(this, sceneController)
    var perspective = true

    override fun update() {
        modelSelector.update()
    }

    override fun render(renderManager: RenderManager) {
        if (size.x < 1 || size.y < 1) return

        renderManager.modelRenderer.run {
            matrixP = getProjectionMatrix()
            matrixV = getViewMatrix()

            val selector = modelSelector
            val model = sceneController.modelController.model
            val selection = sceneController.modelController.selectionManager.selection

            if (sceneController.modelController.modelChange) {
                sceneController.modelController.modelChange = false
                sceneController.modelCache.clear()
                sceneController.selectionCache.clear()
            }

            val y = parent.size.y - (position.y + size.y)
            setViewport(vec2Of(absolutePosition.x, y),
                    vec2Of(size.x, size.y))

            startModel()
            renderModel(sceneController.getModel(model), sceneController.modelCache)

            startSelection()
            renderModelSelection(sceneController.getModel(model), selection, sceneController.selectionCache)
            renderExtras()
            if (selection != SelectionNone && selector.transformationMode != TransformationMode.NONE) {
                renderTranslation(sceneController.cursorCenter, selector, selection, camera)
//                renderRotation(sceneController.cursorCenter, selector, selection, camera)
            }
            if (Config.keyBindings.moveCamera.check(sceneController.mouse) || Config.keyBindings.rotateCamera.check(
                    sceneController.mouse)) {
                startPlane(vec2Of(size.x, size.y))
                renderCursor()
            }
            stop()
            setViewport(vec2Of(0, 0), sceneController.viewManager.getSize())
        }
    }

    override fun registerListeners(eventController: EventController) {
        modelSelector.registerListeners(eventController)
    }

    fun getProjectionMatrix(): IMatrix4 {
        if (perspective) {
            return Matrix4d().setPerspective(Config.perspectiveFov.toRads(), (size.x / size.y).toDouble(), 0.1,
                    1000.0).toIMatrix()
        } else {
            val size = size.toIVector()
            val aspectRatio = (size.yd / size.xd)
            return Matrix4d().setOrtho(-1.0, 1.0, -1.0 * aspectRatio, 1.0 * aspectRatio, 0.1, 1000.0).toIMatrix()
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