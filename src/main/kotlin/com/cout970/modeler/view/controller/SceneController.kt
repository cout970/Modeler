package com.cout970.modeler.view.controller

import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IEventController
import com.cout970.modeler.event.IInput
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.IModelProvider
import com.cout970.modeler.util.*
import com.cout970.modeler.view.gui.Root
import com.cout970.modeler.view.scene.Scene
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Vector2f

/**
 * Created by cout970 on 2016/12/27.
 */

class SceneController(val modelProvider: IModelProvider, val input: IInput, val rootFrame: Root,
                      val timer: Timer) {

    lateinit var selectedScene: Scene
    val scenes = mutableListOf<Scene>()

    val cursorCenter: IVector3 get() {
        return modelProvider.selectionManager.getSelectionCenter(
                selectedScene.selectorCache.model ?: modelProvider.model)
    }

    val modelCache = Cache<Int, VAO>(20).apply { onRemove = { _, v -> v.close() } }
    val selectionCache = Cache<Int, VAO>(40).apply { onRemove = { _, v -> v.close() } }

    var transformationMode = TransformationMode.TRANSLATION

    val showAllMeshUVs = BooleanProperty(true)
    val showBoundingBoxes = BooleanProperty(false)

    val cursorTemplate: Cursor get() = Cursor(cursorCenter, transformationMode, CursorParameters.create(0.0, vec2Of(1)))

    val listeners = ViewListeners(modelProvider, input, this)

    fun registerListeners(eventHandler: IEventController) {
        listeners.registerListeners(eventHandler)
    }

    fun tick() {
        scaleScenes()
        updateCamera()
        scenes.forEach(Scene::update)
    }

    fun updateCamera() {
        val move = Config.keyBindings.moveCamera.check(input)
        val rotate = Config.keyBindings.rotateCamera.check(input)
        if (move || rotate) {

            val speed = 1 / 60.0 * if (Config.keyBindings.slowCameraMovements.check(input)) 1 / 10f else 1f

            if (move) {
                val camera = selectedScene.camera
                val rotations = vec2Of(camera.angleY, camera.angleX).toDegrees()
                val axisX = vec2Of(Math.cos(rotations.x.toRads()), Math.sin(rotations.x.toRads()))
                var axisY = vec2Of(Math.cos((rotations.xd - 90).toRads()), Math.sin((rotations.xd - 90).toRads()))
                axisY *= Math.sin(rotations.y.toRads())
                var a = vec3Of(axisX.x, 0.0, axisX.y)
                var b = vec3Of(axisY.x, Math.cos(rotations.y.toRads()), axisY.y)
                val diff = input.mouse.getMousePosDiff()

                a = a.normalize() * (diff.xd * Config.mouseTranslateSpeedX * speed * Math.sqrt(camera.zoom))
                b = b.normalize() * (-diff.yd * Config.mouseTranslateSpeedY * speed * Math.sqrt(camera.zoom))

                selectedScene.cameraHandler.translate(a + b)
            } else if (rotate) {
                selectedScene.apply {
                    val diff = input.mouse.getMousePosDiff()
                    cameraHandler.rotate(
                            diff.yd * Config.mouseRotationSpeedY * speed,
                            diff.xd * Config.mouseRotationSpeedX * speed
                    )
                }
            }
        }
    }

    fun getModel(model: Model): Model {
        if (selectedScene.selectorCache.model != null) {
            return selectedScene.selectorCache.model!!
        }
        return model
    }

    fun refreshScenes() {
        rootFrame.bottomCenterPanel.apply {
            clearComponents()
            for (scene in scenes) {
                addComponent(scene)
            }
        }
        selectedScene = scenes.first()
    }

    fun scaleScenes() {
        val contentPanel = rootFrame.bottomCenterPanel
        when (scenes.size) {
            1 -> scenes[0].apply {
                size = contentPanel.size
                position = Vector2f(0f, 0f)
            }
            2 -> {
                scenes[0].apply {
                    size = contentPanel.size.run { Vector2f(x, y / 2) }
                    position = Vector2f(0f, 0f)
                }
                scenes[1].apply {
                    size = contentPanel.size.run { Vector2f(x, y / 2) }
                    position = Vector2f(0f, contentPanel.size.y / 2f)
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