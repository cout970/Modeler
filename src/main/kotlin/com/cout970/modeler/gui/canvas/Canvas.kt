package com.cout970.modeler.gui.canvas

import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.gui.leguicomp.Panel
import com.cout970.modeler.render.tool.camera.CameraHandler
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import com.cout970.reactive.dsl.borderless
import com.cout970.reactive.dsl.transparent

/**
 * Created by cout970 on 2017/05/02.
 */

class Canvas : Panel() {

    var viewMode: SelectionTarget = SelectionTarget.MODEL

    val modelCamera = CameraHandler()
    val textureCamera = CameraHandler().apply { setOrtho(true) }

    val cameraHandler
        get() = when (viewMode) {
            SelectionTarget.MODEL -> modelCamera
            SelectionTarget.TEXTURE -> textureCamera
        }

    init {
        transparent()
        borderless()
    }
}