package com.cout970.modeler.view.scene

import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.event.IEventController
import com.cout970.modeler.modeleditor.IModelProvider
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.window.WindowHandler
import org.joml.Matrix4d
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.util.ColorConstants

/**
 * Created by cout970 on 2016/12/27.
 */
abstract class Scene(val modelProvider: IModelProvider, val windowHandler: WindowHandler,
                     val sceneController: SceneController) : Panel() {

    var camera = Camera.DEFAULT
    var desiredZoom = camera.zoom

    open fun update() {
        if (Math.abs(desiredZoom - camera.zoom) > 0.01) {
            camera = camera.copy(
                    zoom = camera.zoom + (desiredZoom - camera.zoom) * Math.min(1.0, sceneController.timer.delta * 20))
        }
    }

    fun createOrthoMatrix(): IMatrix4 {
        val aspectRatio = (size.y / size.x)
        return Matrix4d().setOrtho(-1.0 / aspectRatio, 1.0 / aspectRatio, -1.0, 1.0, 0.1, 1000.0).toIMatrix()
    }

    abstract fun registerListeners(eventHandler: IEventController)

    init {
        backgroundColor = ColorConstants.transparent()
    }
}

