package com.cout970.modeler.controller

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.selector.ISelectable
import com.cout970.modeler.controller.selector.TransformationMode

/**
 * Created by cout970 on 2017/06/12.
 */
class GuiState {

    var transformationMode = TransformationMode.TRANSLATION

    var useTexture = false
    var useColor = false
    var useLight = true

    var drawTextureGridLines: Boolean = true
    var drawModelGridLines: Boolean = true

    var renderLights: Boolean = false

    var holdingSelection: ISelectable? = null
    var hoveredObject: ISelectable? = null
    var tmpModel: IModel? = null
}