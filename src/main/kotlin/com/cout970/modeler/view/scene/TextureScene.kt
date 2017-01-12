package com.cout970.modeler.view.scene

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.event.EventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.render.RenderManager
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.xd
import com.cout970.vector.extensions.yd
import org.joml.Matrix4d

/**
 * Created by cout970 on 2017/01/02.
 */
class TextureScene(controller: SceneController) : Scene(controller) {

    init {
        camera = Camera.DEFAULT.copy(angleX = 0.0, angleY = 0.0)
    }

    override fun render(renderManager: RenderManager) {
        if (size.x < 1 || size.y < 1) return

        renderManager.modelRenderer.run {
            val model = sceneController.modelController.model
            val selection = sceneController.modelController.selectionManager.selection
            val viewport = size.toIVector()
            val aspectRatio = (viewport.yd / viewport.xd)
            val orthoMatrix = Matrix4d().setOrtho(-1.0, 1.0, -1.0 * aspectRatio, 1.0 * aspectRatio, 0.1,
                    1000.0).toIMatrix()
            matrixP = orthoMatrix
            matrixV = camera.matrixForUV

            val y = parent.size.y - (position.y + size.y)
            setViewport(vec2Of(absolutePosition.x, y),
                    vec2Of(size.x, size.y))

            startUV()
            renderTextureGrid()
            renderUVs(model, selection)
            stop()
            setViewport(vec2Of(0, 0), sceneController.viewManager.getSize())
        }
    }

    override fun update() {
        super.update()
    }

    override fun registerListeners(eventController: EventController) {
        eventController.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean {
                if (e.keyState == EnumKeyState.PRESS) {
                    if (e.keycode == Keyboard.KEY_T) {

                    }
                }
                return false
            }
        })
    }
}