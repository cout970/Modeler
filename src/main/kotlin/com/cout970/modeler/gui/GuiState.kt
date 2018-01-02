package com.cout970.modeler.gui

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.gui.canvas.ISelectable
import com.cout970.modeler.gui.canvas.TransformationMode
import com.cout970.modeler.util.BooleanPropertyWrapper

/**
 * Created by cout970 on 2017/06/12.
 */
class GuiState {

    var transformationMode = TransformationMode.TRANSLATION
    var selectionType: SelectionType = SelectionType.OBJECT

    var useTexture: Boolean = true
    var useColor: Boolean = false
    var useLight: Boolean = true
    var showHiddenFaces: Boolean = false
    var drawTextureProjection: Boolean = true
    var drawTextureGridLines: Boolean = true

    var renderLights: Boolean = false
    var renderSkybox: Boolean = true
    var renderBaseBlock: Boolean = true

    var popup: Popup? = null

    var showLeftPanel = true
    var showRightPanel = true
    var showBottomPanel = false

    var hoveredObject: ISelectable? = null
    var tmpModel: IModel? = null

    var selectedMaterial: IMaterialRef = MaterialRef(-1)

    var modelHash: Int = -1
    var modelSelectionHash: Int = -1
    var textureSelectionHash: Int = -1
    var materialsHash: Int = -1
    var visibilityHash: Int = -1
    var gridLinesHash: Int = -1

    var playAnimation = false

    fun getBooleanProperties() = mapOf(
            "drawTextureGridLines" to BooleanPropertyWrapper(this::drawTextureGridLines),
            "drawTextureProjection" to BooleanPropertyWrapper(this::drawTextureProjection),
            "renderLights" to BooleanPropertyWrapper(this::renderLights),
            "renderBase" to BooleanPropertyWrapper(this::renderBaseBlock),
            "useTexture" to BooleanPropertyWrapper(this::useTexture),
            "useColor" to BooleanPropertyWrapper(this::useColor),
            "useLight" to BooleanPropertyWrapper(this::useLight),
            "showInvisible" to BooleanPropertyWrapper(this::showHiddenFaces),
            "showLeftPanel" to BooleanPropertyWrapper(this::showLeftPanel),
            "showRightPanel" to BooleanPropertyWrapper(this::showRightPanel),
            "showBottomPanel" to BooleanPropertyWrapper(this::showBottomPanel)
    )
}