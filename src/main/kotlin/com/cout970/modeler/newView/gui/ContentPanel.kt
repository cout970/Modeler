package com.cout970.modeler.newView.gui

import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IInput
import com.cout970.modeler.newView.ControllerState
import com.cout970.modeler.newView.gui.comp.CPanel
import com.cout970.modeler.util.toRads
import com.cout970.vector.extensions.*
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/04/08.
 */
class ContentPanel : CPanel() {

    val controllerState = ControllerState()
    val scenes = mutableListOf<Scene>()
    var selectedScene: Scene? = null

    fun showScenes(layout: Int) {
        scenes.clear()
        when (layout) {
            1 -> {
                scenes += Scene()
                scenes += Scene()
            }
            2 -> {
                scenes += Scene()
                scenes += Scene()
                scenes += Scene()
                scenes += Scene()
            }
            3 -> {
                scenes += Scene()
                scenes += Scene()
            }
            4 -> {
                scenes += Scene()
                scenes += Scene()
                scenes += Scene()
                scenes += Scene()
            }
            else -> {
                scenes += Scene()
            }
        }
        refreshScenes()
    }

    fun refreshScenes() {
        clearComponents()
        for (scene in scenes) {
            addComponent(scene)
        }
        selectedScene = scenes.first()
    }

    fun updateCamera(input: IInput) {
        selectedScene?.let { selectedScene ->
            val move = Config.keyBindings.moveCamera.check(input)
            val rotate = Config.keyBindings.rotateCamera.check(input)
            if (move || rotate) {

                val speed = 1 / 60.0 * if (Config.keyBindings.slowCameraMovements.check(input)) 1 / 10f else 1f

                if (move) {
                    val camera = selectedScene.cameraHandler.camera
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
    }

    fun scaleScenes() {
        val contentPanel = this
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