package com.cout970.modeler.view.gui.comp.canvas

import com.cout970.modeler.view.gui.comp.CBorderRenderer
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.render.tool.Light
import com.cout970.modeler.view.render.tool.camera.CameraHandler
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of
import org.liquidengine.legui.util.ColorConstants

/**
 * Created by cout970 on 2017/05/02.
 */

class Canvas : CPanel() {

    val cameraHandler = CameraHandler()

    val lights: List<Light> = listOf(
            Light(vec3Of(500, 1000, 750), Vector3.ONE),
            Light(vec3Of(-500, -1000, -750), Vector3.ONE)
    )

    init {
        backgroundColor = ColorConstants.transparent()
        border.renderer = CBorderRenderer
    }
}