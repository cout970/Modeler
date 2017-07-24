package com.cout970.modeler.view

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.view.canvas.ISelectable
import com.cout970.modeler.view.canvas.TransformationMode

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

    var modelHash: Int = -1
    var modelSelectionHash: Int = -1
    var textureSelectionHash: Int = -1
    var materialsHash: Int = -1
    var visibilityHash: Int = -1
}