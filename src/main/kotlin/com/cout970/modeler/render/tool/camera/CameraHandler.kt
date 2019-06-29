package com.cout970.modeler.render.tool.camera

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.util.toRads
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec2Of
import kotlin.math.*

/**
 * Created by cout970 on 2017/03/26.
 */
class CameraHandler {

    var camera = Camera.DEFAULT
    var desiredZoom = camera.zoom
    var newRotation: IVector2 = vec2Of(camera.angleX, camera.angleY)
    var desiredRotation: IVector2 = vec2Of(camera.angleX, camera.angleY)

    var lockPos = false
    var lockRot = false
    var lockScale = false

    fun updateAnimation(timer: Timer) {

        if (!lockScale) {
            if (abs(desiredZoom - camera.zoom) > 0.001) {
                camera = camera.copy(
                    zoom = camera.zoom + (desiredZoom - camera.zoom) * min(1.0, timer.delta * 5)
                )
            }
        }

        if (lockRot) return
        if (abs(desiredRotation.xd - camera.angleX) > 0.0001 ||
            abs(desiredRotation.yd - camera.angleY) > 0.0001) {

            camera = camera.copy(
                angleX = camera.angleX + (desiredRotation.xd - camera.angleX) * min(1.0, timer.delta * 10),
                angleY = camera.angleY + (desiredRotation.yd - camera.angleY) * min(1.0, timer.delta * 10)
            )
            newRotation = vec2Of(camera.angleX, camera.angleY)
        } else {
            val distX = distance(camera.angleX, newRotation.xd)
            val distY = distance(camera.angleY, newRotation.yd)

            if (distX.absoluteValue > 0.00001 || distY.absoluteValue > 0.00001) {

                val delta = min(1.0, timer.delta * 5)

                val moveX = -distX * delta
                val moveY = -distY * delta

                camera = camera.copy(
                    angleX = normRad(camera.angleX + moveX),
                    angleY = normRad(camera.angleY + moveY)
                )
                desiredRotation = vec2Of(camera.angleX, camera.angleY)
            }
        }
    }

    private fun normRad(angle: Double): Double {
        val res = angle % 360.toRads()
        return if (res < (-180).toRads()) res + 360.toRads() else if (res > 180.toRads()) res - 360.toRads() else res
    }

    fun distance(src: Double, dst: Double): Double = atan2(sin(src - dst), cos(src - dst))

    fun setPosition(pos: IVector3) {
        if (lockPos) return
        camera = camera.copy(position = pos)
    }

    fun translate(move: IVector3) {
        if (lockPos) return
        camera = camera.copy(position = camera.position + move)
    }

    fun setRotation(angleX: Double, angleY: Double) {
        if (lockRot) return
        newRotation = vec2Of(normRad(angleX), normRad(angleY))
    }

    fun rotate(angleX: Double, angleY: Double) {
        if (lockRot) return
        desiredRotation = vec2Of(camera.angleX + angleX, camera.angleY + angleY)
    }

    fun setZoom(zoom: Double) {
        if (lockScale) return
        desiredZoom = zoom
    }

    fun setOrtho(option: Boolean) {
        camera = camera.copy(perspective = !option)
    }
}