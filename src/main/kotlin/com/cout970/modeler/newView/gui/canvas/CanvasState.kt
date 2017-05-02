package com.cout970.modeler.newView.gui.canvas

import com.cout970.modeler.newView.CameraHandler
import com.cout970.modeler.newView.render.shader.Light
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/05/02.
 */
class CanvasState {

    var perspective: Boolean = true
    val cameraHandler = CameraHandler()

    val lights: List<Light> = listOf(
            Light(vec3Of(500, 1000, 750), Vector3.ONE),
            Light(vec3Of(-500, -1000, -750), Vector3.ONE)
    )
}