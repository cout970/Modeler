package com.cout970.modeler.view.gui.comp.canvas

import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.view.gui.comp.CBorderRenderer
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.render.tool.camera.CameraHandler
import org.liquidengine.legui.util.ColorConstants

/**
 * Created by cout970 on 2017/05/02.
 */

class Canvas : CPanel() {

    val cameraHandler = CameraHandler()
    var viewMode: SelectionTarget = SelectionTarget.MODEL

    init {
        backgroundColor = ColorConstants.transparent()
        border.renderer = CBorderRenderer
    }
}