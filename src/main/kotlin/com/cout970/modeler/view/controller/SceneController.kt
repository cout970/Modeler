package com.cout970.modeler.view.controller

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.device.Mouse
import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.EventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.ModelController
import com.cout970.modeler.util.*
import com.cout970.modeler.view.ViewManager
import com.cout970.modeler.view.scene.ModelScene
import com.cout970.modeler.view.scene.Scene
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Vector2f

/**
 * Created by cout970 on 2016/12/27.
 */

class SceneController(val viewManager: ViewManager, val modelController: ModelController) {

    val scenes = mutableListOf<Scene>()
    lateinit var selectedScene: Scene
    val timer get() = viewManager.renderManager.timer

    var cursorCenter: IVector3 = vec3Of(0)
    var tmpModel: Model? = null

    val modelCache = Cache<Int, VAO>(1).apply { onRemove = { _, v -> v.close() } }
    val selectionCache = Cache<Int, VAO>(2).apply { onRemove = { _, v -> v.close() } }

    var transformationMode = TransformationMode.TRANSLATION
    var selectedAxis = SelectionAxis.NONE
    var hoveredAxis = SelectionAxis.NONE

    lateinit var mouse: Mouse
    lateinit var keyboard: Keyboard

    fun update() {
        scaleScenes()
        mouse.update()
        updateCamera()
        scenes.forEach(Scene::update)
    }

    fun updateCamera() {
        val move = Config.keyBindings.moveCamera.check(mouse)
        val rotate = Config.keyBindings.rotateCamera.check(mouse)
        if (move || rotate) {
            val speed = 1 / 60.0 * if (Config.keyBindings.slowCameraMovements.check(keyboard)) 1 / 10f else 1f

            if (move) {
                val camera = selectedScene.camera
                val rotations = vec2Of(camera.angleY, camera.angleX).toDegrees()
                val axisX = vec2Of(Math.cos(rotations.x.toRads()), Math.sin(rotations.x.toRads()))
                var axisY = vec2Of(Math.cos((rotations.xd - 90).toRads()), Math.sin((rotations.xd - 90).toRads()))
                axisY *= Math.sin(rotations.y.toRads())
                var a = vec3Of(axisX.x, 0.0, axisX.y)
                var b = vec3Of(axisY.x, Math.cos(rotations.y.toRads()), axisY.y)
                val diff = mouse.getMousePosDiff()

                a = a.normalize() * (diff.xd * Config.mouseTranslateSpeedX * speed * Math.sqrt(camera.zoom))
                b = b.normalize() * (-diff.yd * Config.mouseTranslateSpeedY * speed * Math.sqrt(camera.zoom))

                selectedScene.camera = selectedScene.camera.run { copy(position = position + a + b) }
            } else if (rotate) {
                selectedScene.apply {
                    val diff = mouse.getMousePosDiff()
                    camera = camera.run {
                        copy(angleY = angleY + diff.xd * Config.mouseRotationSpeedX * speed)
                    }
                    camera = camera.run {
                        copy(angleX = angleX + diff.yd * Config.mouseRotationSpeedY * speed)
                    }
                }
            }
        }
    }

    fun registerListeners(eventController: EventController) {
        mouse = eventController.mouse
        keyboard = eventController.keyboard

        eventController.addListener(EventMouseScroll::class.java, object : IEventListener<EventMouseScroll> {
            override fun onEvent(e: EventMouseScroll): Boolean {
                scenes.forEach { scene ->
                    if (inside(mouse.getMousePos(), scene.absolutePosition, scene.size.toIVector())) {
                        scene.run {
                            val scroll = -e.offsetY * Config.cameraScrollSpeed
                            if (camera.zoom <= 10) {
                                if (camera.zoom <= 3) {
                                    if (camera.zoom + scroll / 20 > 0.5) {
                                        desiredZoom = camera.zoom + scroll / 20
                                    }
                                } else {
                                    desiredZoom = camera.zoom + scroll / 10
                                }
                            } else {
                                desiredZoom = camera.zoom + scroll
                            }
                        }
                    }
                }
                return true
            }
        })
        eventController.addListener(EventMouseClick::class.java, object : IEventListener<EventMouseClick> {
            override fun onEvent(e: EventMouseClick): Boolean {
                scenes.forEach {
                    if (inside(mouse.getMousePos(), it.absolutePosition, it.size.toIVector())) {
                        selectedScene = it
                    }
                }
                return false
            }
        })
        scenes.forEach {
            it.registerListeners(eventController)
        }
        var lastOption = 0
        eventController.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean {
                if (e.keyState == EnumKeyState.PRESS) {
                    if (Config.keyBindings.switchCameraAxis.keycode == e.keycode) {
                        when (lastOption) {
                            0 -> selectedScene.camera = selectedScene.camera.copy(angleX = 0.0, angleY = 0.0)
                            1 -> selectedScene.camera = selectedScene.camera.copy(angleX = 0.0, angleY = -90.toRads())
                            2 -> selectedScene.camera = selectedScene.camera.copy(angleX = 90.toRads(), angleY = 0.0)
                            3 -> selectedScene.camera = selectedScene.camera.copy(angleX = 45.toRads(),
                                    angleY = -45.toRads())
                        }
                        lastOption++
                        if (lastOption > 3) {
                            lastOption = 0
                        }
                    } else if (Config.keyBindings.switchOrthoProjection.keycode == e.keycode) {
                        (selectedScene as? ModelScene)?.apply {
                            perspective = !perspective
                        }
                    } else if (Config.keyBindings.moveCameraToCursor.keycode == e.keycode) {
                        selectedScene.apply {
                            camera = camera.copy(position = -cursorCenter)
                        }
                    }
                }
                return false
            }
        })
    }

    fun getModel(model: Model): Model {
        if (tmpModel != null) {
            return tmpModel!!
        }
        return model
    }

    fun refreshScenes() {
        selectedScene = scenes.first()
        viewManager.root.contentPanel.apply {
            clearComponents()
            for (scene in scenes) {
                addComponent(scene)
            }
        }
    }

    fun scaleScenes() {
        val contentPanel = viewManager.root.contentPanel
        when (scenes.size) {
            1 -> scenes[0].apply {
                size = contentPanel.size
                position = Vector2f(0f, 0f)
            }
            2 -> {
                scenes[0].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y) }
                    position = Vector2f(0f, 0f)
                }
                scenes[1].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y) }
                    position = Vector2f(contentPanel.size.x / 2f, 0f)
                }
            }
            3 -> {
                scenes[0].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y) }
                    position = Vector2f(0f, 0f)
                }
                scenes[1].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(contentPanel.size.x / 2f, 0f)
                }
                scenes[2].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(contentPanel.size.x / 2f, contentPanel.size.y / 2f)
                }
            }
            4 -> {
                scenes[0].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(0f, 0f)
                }
                scenes[1].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(contentPanel.size.x / 2f, 0f)
                }
                scenes[2].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(0f, contentPanel.size.y / 2f)
                }
                scenes[3].apply {
                    size = contentPanel.size.run { Vector2f(x / 2, y / 2) }
                    position = Vector2f(contentPanel.size.x / 2f, contentPanel.size.y / 2f)
                }
            }
        }
    }
}