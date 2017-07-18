package com.cout970.modeler.view.render.tool.camera

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.isInside
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toRads
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.gui.comp.canvas.Canvas
import com.cout970.modeler.view.gui.comp.canvas.CanvasContainer
import com.cout970.vector.extensions.*

/**
 * Created by cout970 on 2017/05/02.
 */

class CameraUpdater(
        val canvasContainer: CanvasContainer,
        val input: IInput,
        val timer: Timer
) {

    private var selectedCanvas: Canvas? = null

    fun updateCameras() {
        canvasContainer.canvas.forEach { canvas ->
            canvas.cameraHandler.updateAnimation(timer)
        }

        updateSelectedCanvas()
        selectedCanvas?.let { moveCamera(it) }
    }

    private fun updateSelectedCanvas() {
        if (Config.keyBindings.moveCamera.check(input) ||
            Config.keyBindings.rotateCamera.check(input)) {

            if (selectedCanvas == null) {
                val mousePos = input.mouse.getMousePos()
                val hover = canvasContainer.canvas.indexOfFirst { canvas ->
                    mousePos.isInside(canvas.absolutePosition, canvas.size.toIVector())
                }
                selectedCanvas = if (hover == -1) null else canvasContainer.canvas[hover]
            }
        } else {
            selectedCanvas = null
        }
    }

    private fun moveCamera(selectedScene: Canvas) {

        val move = Config.keyBindings.moveCamera.check(input)
        val rotate = Config.keyBindings.rotateCamera.check(input)

        if (!move && !rotate) return

        val speed = 1 / 60.0 * if (Config.keyBindings.slowCameraMovements.check(input)) 1 / 10f else 1f

        if (selectedScene.viewMode == SelectionTarget.MODEL) {
            if (move) {
                moveModelCamera(selectedScene, speed)
            } else if (rotate) {
                rotateModelCamera(selectedScene, speed)
            }
        } else {
            moveTextureCamera(selectedScene, speed)
        }
    }

    private fun rotateModelCamera(selectedScene: Canvas, speed: Double) {
        val diff = input.mouse.getMousePosDiff()
        selectedScene.cameraHandler.rotate(
                diff.yd * Config.mouseRotationSpeedY * speed,
                diff.xd * Config.mouseRotationSpeedX * speed
        )
    }

    private fun moveModelCamera(selectedScene: Canvas, speed: Double) {
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
    }

    fun moveTextureCamera(selectedScene: Canvas, speed: Double) {
        val camera = selectedScene.cameraHandler.camera
        val diff = input.mouse.getMousePosDiff()

        val a = (diff.xd * Config.mouseTranslateSpeedX * speed * Math.sqrt(camera.zoom))
        val b = (-diff.yd * Config.mouseTranslateSpeedY * speed * Math.sqrt(camera.zoom))

        selectedScene.cameraHandler.translate(vec3Of(a, b, 0))
    }
}