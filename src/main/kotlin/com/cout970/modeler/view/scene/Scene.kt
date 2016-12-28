package com.cout970.modeler.view.scene

import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.config.Config
import com.cout970.modeler.modelcontrol.selection.SelectionNone
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.view.controller.ModelSelector
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.controller.TransformationMode
import com.cout970.modeler.view.render.RenderManager
import com.cout970.vector.extensions.vec2Of
import org.joml.Math
import org.joml.Matrix4d
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2016/12/27.
 */
class Scene(val sceneController: SceneController) : Panel() {

    var camera = Camera.DEFAULT
    val modelSelector = ModelSelector(this, sceneController)

    fun render(renderManager: RenderManager) {
        if (size.x < 1 || size.y < 1) return

        renderManager.modelRenderer.run {
            matrixP = Matrix4d().setPerspective(Math.toRadians(60.0), (size.x / size.y).toDouble(), 0.1, 1000.0).toIMatrix()
            matrixV = camera.matrix

            val selector = modelSelector
            val model = sceneController.modelController.model
            val selection = sceneController.modelController.selectionManager.selection

            if (sceneController.modelController.modelChange) {
                sceneController.modelController.modelChange = false
                modelCache.clear()
                selectionCache.clear()
            }

            setViewport(vec2Of(position.x, 0), vec2Of(size.x, size.y))

            startModel()
            renderModel(selector.getModel(model))

            startSelection()
            renderModelSelection(selector.getModel(model), selection)
            renderExtras()
            if (selection != SelectionNone && selector.transformationMode != TransformationMode.NONE) {
                renderTranslation(selector.center, selector, selection, camera)
            }
            if (Config.keyBindings.moveCamera.check(sceneController.mouse) || Config.keyBindings.rotateCamera.check(sceneController.mouse)) {
                startPlane(vec2Of(size.x, size.y))
                renderCursor()
            }
            stop()
        }
    }

    fun getMatrixMVP(): IMatrix4 {
        return Matrix4d().setPerspective(Math.toRadians(60.0), (size.x / size.y).toDouble(), 0.1, 1000.0).toIMatrix() * camera.matrix
    }
}