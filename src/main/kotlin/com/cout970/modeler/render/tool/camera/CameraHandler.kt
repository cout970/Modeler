package com.cout970.modeler.render.tool.camera

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.util.toRads
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec2Of

/**
 * Created by cout970 on 2017/03/26.
 */
class CameraHandler {

    var camera = Camera.Companion.DEFAULT
    var desiredZoom = camera.zoom
    var desiredRotation: IVector2 = vec2Of(camera.angleX, camera.angleY)

    fun updateAnimation(timer: Timer) {

        if (Math.abs(desiredZoom - camera.zoom) > 0.001) {
            camera = camera.copy(zoom = camera.zoom + (desiredZoom - camera.zoom) * Math.min(1.0, timer.delta * 5))
        }

        if (Math.abs(desiredRotation.xd - camera.angleX) > 0.001 ||
            Math.abs(desiredRotation.yd - camera.angleY) > 0.001) {

            val currentRota = vec2Of(camera.angleX, camera.angleY)

            val diff = desiredRotation - currentRota

            val diffX = diff.xd
            val diffY = diff.yd

            val dirX = diffX * Math.min(1.0, timer.delta * 5)
            val dirY = diffY * Math.min(1.0, timer.delta * 5)

            camera = camera.copy(
                    angleX = camera.angleX + dirX,
                    angleY = camera.angleY + dirY
            )
        }
    }

    private fun normalizeAngle(vec: IVector2): IVector2 {
        var x = vec.xd
        var y = vec.yd
        if (vec.xd > 180.toRads()) {
            x = vec.xd - 360.toRads()
        }
        if (vec.yd > 180.toRads()) {
            y = vec.yd - 360.toRads()
        }
        if (vec.xd < (-180).toRads()) {
            x = vec.xd + 360.toRads()
        }
        if (vec.yd < (-180).toRads()) {
            y = vec.yd + 360.toRads()
        }
        return vec2Of(x, y)
    }

    fun setPosition(pos: IVector3) {
        camera = camera.copy(position = pos)
    }

    fun translate(move: IVector3) {
        camera = camera.copy(position = camera.position + move)
    }

    fun setRotation(angleX: Double, angleY: Double) {
        desiredRotation = vec2Of(angleX, angleY)
    }

    fun rotate(angleX: Double, angleY: Double) {
        desiredRotation = vec2Of(camera.angleX + angleX, camera.angleY + angleY)
    }

    fun setZoom(zoom: Double) {
        desiredZoom = zoom
    }

    fun setOrtho(option: Boolean) {
        camera = camera.copy(perspective = !option)
    }
}