package com.cout970.modeler.view.render.tool.camera

import com.cout970.glutilities.structure.Timer
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.plus

/**
 * Created by cout970 on 2017/03/26.
 */
class CameraHandler {

    var camera = Camera.Companion.DEFAULT
    var desiredZoom = camera.zoom

    fun updateAnimation(timer: Timer) {
        if (Math.abs(desiredZoom - camera.zoom) > 0.01) {
            camera = camera.copy(zoom = camera.zoom + (desiredZoom - camera.zoom) * Math.min(1.0, timer.delta * 20))
        }
    }

    fun setPosition(pos: IVector3) {
        camera = camera.copy(position = pos)
    }

    fun translate(move: IVector3) {
        camera = camera.copy(position = camera.position + move)
    }

    fun setRotation(angleX: Double, angleY: Double) {
        camera = camera.copy(
                angleX = angleX,
                angleY = angleY
        )
    }

    fun rotate(angleX: Double, angleY: Double) {
        camera = camera.copy(
                angleX = camera.angleX + angleX,
                angleY = camera.angleY + angleY
        )
    }

    fun setZoom(zoom: Double) {
        desiredZoom = zoom
    }

    fun setOrtho(option: Boolean) {
        camera = camera.copy(perspective = !option)
    }
}