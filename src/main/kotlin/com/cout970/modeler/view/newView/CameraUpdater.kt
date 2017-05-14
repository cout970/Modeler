package com.cout970.modeler.view.newView

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toRads
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.newView.gui.Scene
import com.cout970.modeler.view.window.WindowHandler
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/05/02.
 */

class CameraUpdater(
        val sceneHandler: SceneHandler,
        val input: IInput,
        val windowHandler: WindowHandler
) {

    fun updateCameras() {
        sceneHandler.scenes.forEach {
            it.cameraHandler.update(windowHandler.timer)
        }
        sceneHandler.selectedScene?.let(this::updateScene)
    }

    private fun updateScene(selectedScene: Scene) {

        val move = Config.keyBindings.moveCamera.check(input)
        val rotate = Config.keyBindings.rotateCamera.check(input)

        if (!move && !rotate) return

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