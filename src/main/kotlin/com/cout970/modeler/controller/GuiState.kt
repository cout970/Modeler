package com.cout970.modeler.controller

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.selector.ISelectable
import com.cout970.modeler.controller.selector.TransformationMode
import com.cout970.modeler.core.model.selection.PosSelection

/**
 * Created by cout970 on 2017/06/12.
 */
class GuiState {
    var editMode = EditMode.OBJECT
    var transformationMode = TransformationMode.TRANSLATION

    var useTexture = false
    var useColor = false
    var useLight = true

    var persistentSelection: ISelectable? = null
    var holdingSelection: ISelectable? = null
    var hoveredObject: ISelectable? = null
    var tmpModel: IModel? = null

    var posSelection: List<PosSelection>? = null

    fun getPosSelection(model: IModel): List<PosSelection> {
        return posSelection ?: emptyList()
    }
}

enum class EditMode {
    OBJECT,
    FACE,
    EDGE,
    VERTEX
}