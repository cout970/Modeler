package com.cout970.modeler.render.tool.camera

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.core.config.Config
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.lengthSq
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times
import com.cout970.vector.extensions.vec2Of

/**
 * Created by cout970 on 2017/03/26.
 */
class CameraHandler {

    var camera = Camera.Companion.DEFAULT
    var desiredZoom = camera.zoom
    var desiredRotation: IVector2 = vec2Of(camera.angleX, camera.angleY)
    var rotationInertia: IVector2? = null

    fun updateAnimation(timer: Timer) {

        if (Math.abs(desiredZoom - camera.zoom) > 0.01) {
            camera = camera.copy(zoom = camera.zoom + (desiredZoom - camera.zoom) * Math.min(1.0, timer.delta * 5))
        }

        rotationInertia?.let {
            if (it.lengthSq() < 0.001) {
                rotationInertia = null; return@let
            }
            desiredRotation += it * (1 - Math.min(1.0, timer.delta * Config.cameraInertiaFactor))
            rotationInertia = it * 0.75
        }

        if (Math.abs(desiredRotation.xd - camera.angleX) > 0.01 ||
            Math.abs(desiredRotation.yd - camera.angleY) > 0.01) {

            camera = camera.copy(
                    angleX = camera.angleX + (desiredRotation.xd - camera.angleX) * Math.min(1.0, timer.delta * 10),
                    angleY = camera.angleY + (desiredRotation.yd - camera.angleY) * Math.min(1.0, timer.delta * 10)
            )
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

        if (Config.cameraRotationInertia) {
            rotationInertia = vec2Of(angleX, angleY)
        }
        desiredRotation = vec2Of(camera.angleX + angleX, camera.angleY + angleY)
    }

    fun setZoom(zoom: Double) {
        desiredZoom = zoom
    }

    fun setOrtho(option: Boolean) {
        camera = camera.copy(perspective = !option)
    }
}