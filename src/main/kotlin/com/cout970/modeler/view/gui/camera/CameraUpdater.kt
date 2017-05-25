package com.cout970.modeler.view.gui.camera

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toRads
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.gui.canvas.Canvas
import com.cout970.modeler.view.gui.canvas.CanvasContainer
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/05/02.
 */

class CameraUpdater(
        val canvasContainer: CanvasContainer,
        val input: IInput,
        val timer: Timer
) {

    fun updateCameras() {
        canvasContainer.canvas.forEach {
            it.state.cameraHandler.update(timer)
            updateScene(it)
        }
    }

    private fun updateScene(selectedScene: Canvas) {

        val move = Config.keyBindings.moveCamera.check(input)
        val rotate = Config.keyBindings.rotateCamera.check(input)

        if (!move && !rotate) return

        val speed = 1 / 60.0 * if (Config.keyBindings.slowCameraMovements.check(input)) 1 / 10f else 1f

        if (move) {
            val camera = selectedScene.state.cameraHandler.camera
            val rotations = vec2Of(camera.angleY, camera.angleX).toDegrees()
            val axisX = vec2Of(Math.cos(rotations.x.toRads()), Math.sin(rotations.x.toRads()))
            var axisY = vec2Of(Math.cos((rotations.xd - 90).toRads()), Math.sin((rotations.xd - 90).toRads()))
            axisY *= Math.sin(rotations.y.toRads())
            var a = vec3Of(axisX.x, 0.0, axisX.y)
            var b = vec3Of(axisY.x, Math.cos(rotations.y.toRads()), axisY.y)
            val diff = input.mouse.getMousePosDiff()

            a = a.normalize() * (diff.xd * Config.mouseTranslateSpeedX * speed * Math.sqrt(camera.zoom))
            b = b.normalize() * (-diff.yd * Config.mouseTranslateSpeedY * speed * Math.sqrt(camera.zoom))

            selectedScene.state.cameraHandler.translate(a + b)
        } else if (rotate) {
            val diff = input.mouse.getMousePosDiff()
            selectedScene.state.cameraHandler.rotate(
                    diff.yd * Config.mouseRotationSpeedY * speed,
                    diff.xd * Config.mouseRotationSpeedX * speed
            )
        }
    }
}