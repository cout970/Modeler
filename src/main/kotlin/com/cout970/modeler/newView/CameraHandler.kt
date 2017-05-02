package com.cout970.modeler.newView

import com.cout970.glutilities.structure.Timer
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.plus

/**
 * Created by cout970 on 2017/03/26.
 */
class CameraHandler {

    var camera = Camera.DEFAULT
    var desiredZoom = camera.zoom

    fun update(timer: Timer) {
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

    fun rotate(angleX: Double, angleY: Double) {
        camera = camera.copy(
                angleX = camera.angleX + angleX,
                angleY = camera.angleY + angleY
        )
    }

    fun setRotation(angleX: Double, angleY: Double) {
        camera = camera.copy(
                angleX = angleX,
                angleY = angleY
        )
    }

    fun makeZoom(zoom: Double) {
        desiredZoom = zoom
    }
}