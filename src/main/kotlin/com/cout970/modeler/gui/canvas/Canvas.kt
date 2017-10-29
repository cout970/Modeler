package com.cout970.modeler.gui.canvas

import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.gui.react.leguicomp.Panel
import com.cout970.modeler.render.tool.camera.CameraHandler
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent

/**
 * Created by cout970 on 2017/05/02.
 */

class Canvas : Panel() {

    var viewMode: SelectionTarget = SelectionTarget.MODEL

    val modelCamera = CameraHandler()
    val textureCamera = CameraHandler()

    val cameraHandler
        get() = when (viewMode) {
            SelectionTarget.MODEL -> modelCamera
            SelectionTarget.TEXTURE -> textureCamera
            SelectionTarget.ANIMATION -> modelCamera
        }

    init {
        setTransparent()
        setBorderless()
    }
}