package com.cout970.modeler.view.gui.canvas

import com.cout970.modeler.to_redo.newView.render.shader.Light
import com.cout970.modeler.view.gui.camera.CameraHandler
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/05/02.
 */
class CanvasState {

    val cameraHandler = CameraHandler()

    val lights: List<Light> = listOf(
            Light(vec3Of(500, 1000, 750), Vector3.ONE),
            Light(vec3Of(-500, -1000, -750), Vector3.ONE)
    )
}