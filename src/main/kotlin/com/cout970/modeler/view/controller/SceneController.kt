package com.cout970.modeler.view.controller

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.device.Mouse
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.EventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.ModelController
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.inside
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toRads
import com.cout970.modeler.view.ViewManager
import com.cout970.modeler.view.scene.Scene
import com.cout970.vector.extensions.*
import org.joml.Vector2f
import org.joml.Vector4f
import java.util.*

/**
 * Created by cout970 on 2016/12/27.
 */

class SceneController(val viewManager: ViewManager, val modelController: ModelController) {

    val scenes = mutableListOf<Scene>()
    val selectedScene: Scene get() = scenes.first()
    var tmpModel: Model? = null

    lateinit var mouse: Mouse
    lateinit var keyboard: Keyboard

    init {
        viewManager.root.contentPanel.apply {
            for (scene in scenes) {
                addComponent(scene)
            }
        }
    }

    fun update() {
        mouse.update()

        if (Config.keyBindings.moveCamera.check(mouse)) {
            val rotations = vec2Of(selectedScene.camera.angleY, selectedScene.camera.angleX).toDegrees()
            val axisX = vec2Of(Math.cos(rotations.x.toRads()), Math.sin(rotations.x.toRads()))
            var axisY = vec2Of(Math.cos((rotations.xd - 90).toRads()), Math.sin((rotations.xd - 90).toRads()))
            axisY *= Math.sin(rotations.y.toRads())
            var a = vec3Of(axisX.x, 0.0, axisX.y)
            var b = vec3Of(axisY.x, Math.cos(rotations.y.toRads()), axisY.y)

            a = a.normalize() * (mouse.getMousePosDiff().xd * viewManager.renderManager.timer.delta * Config.mouseTranslateSpeedX)
            b = b.normalize() * (-mouse.getMousePosDiff().yd * viewManager.renderManager.timer.delta * Config.mouseTranslateSpeedY)

            selectedScene.camera = selectedScene.camera.run { copy(position = position + a + b) }
        } else if (Config.keyBindings.rotateCamera.check(mouse)) {
            selectedScene.camera = selectedScene.camera.run {
                copy(angleY = angleY + mouse.getMousePosDiff().xd * viewManager.renderManager.timer.delta * Config.mouseRotationSpeedX)
            }
            selectedScene.camera = selectedScene.camera.run {
                copy(angleX = angleX + mouse.getMousePosDiff().yd * viewManager.renderManager.timer.delta * Config.mouseRotationSpeedY)
            }
        }
        scenes.map(Scene::modelSelector).forEach(ModelSelector::update)
        if (scenes.size == 1) {
            scenes[0].apply {
                size = viewManager.root.contentPanel.size
                position = viewManager.root.contentPanel.position
            }
        } else {
            scenes.forEachIndexed { i, scene ->
                scene.size = Vector2f(viewManager.root.contentPanel.size.x / 2f, viewManager.root.contentPanel.size.y / 2f)
                scene.position = Vector2f((i % 2) * scene.size.x, (i / 2) * -scene.size.y).add(viewManager.root.contentPanel.position)
                val r = Random()
                scene.backgroundColor = Vector4f(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1f)
            }
        }
    }

    fun registerListeners(eventController: EventController) {
        mouse = eventController.mouse
        keyboard = eventController.keyboard

        eventController.addListener(EventMouseScroll::class.java, object : IEventListener<EventMouseScroll> {
            override fun onEvent(e: EventMouseScroll): Boolean {
                if (inside(mouse.getMousePos(), selectedScene.absolutePosition, selectedScene.size.toIVector())) {
                    selectedScene.run {
                        val scroll = -e.offsetY * Config.cameraScrollSpeed
                        if (camera.zoom <= 10) {
                            if (camera.zoom <= 3) {
                                if (camera.zoom + scroll / 8 > 0.5) {
                                    camera = camera.copy(zoom = camera.zoom + scroll / 8)
                                }
                            } else {
                                camera = camera.copy(zoom = camera.zoom + scroll / 4)
                            }
                        } else {
                            camera = camera.copy(zoom = camera.zoom + scroll)
                        }
                    }
                }
                return true
            }
        })
        scenes.map(Scene::modelSelector).forEach {
            it.registerListeners(eventController)
        }
    }
}