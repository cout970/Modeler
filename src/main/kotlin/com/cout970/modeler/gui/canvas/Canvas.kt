package com.cout970.modeler.gui.canvas

import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.gui.comp.CPanel
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.render.tool.camera.CameraHandler

/**
 * Created by cout970 on 2017/05/02.
 */

class Canvas : CPanel() {

    val cameraHandler = CameraHandler()
    var viewMode: SelectionTarget = SelectionTarget.MODEL

    init {
        setTransparent()
        setBorderless()
    }
}