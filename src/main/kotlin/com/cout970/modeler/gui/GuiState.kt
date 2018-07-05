package com.cout970.modeler.gui

import com.cout970.modeler.api.animation.IAnimationRef
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.animation.AnimationRefNone
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.gui.canvas.ISelectable
import com.cout970.modeler.gui.canvas.TransformationMode
import com.cout970.modeler.util.BooleanPropertyWrapper
import com.cout970.modeler.util.GuiProperty

/**
 * Created by cout970 on 2017/06/12.
 */
class GuiState {

    var transformationMode = TransformationMode.TRANSLATION
    var selectionType: SelectionType by GuiProperty(SelectionType.OBJECT, "SelectionType")

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

    var hoveredObject: ISelectable? = null
    var tmpModel: IModel? = null

    var selectedMaterial: IMaterialRef = MaterialRefNone
    var selectedAnimation: IAnimationRef = AnimationRefNone

    var modelHash: Int = -1
    var modelSelectionHash: Int = -1
    var textureSelectionHash: Int = -1
    var materialsHash: Int = -1
    var gridLinesHash: Int = -1

    fun getBooleanProperties() = mapOf(
            "drawTextureGridLines" to BooleanPropertyWrapper(this::drawTextureGridLines),
            "drawTextureProjection" to BooleanPropertyWrapper(this::drawTextureProjection),
            "renderLights" to BooleanPropertyWrapper(this::renderLights),
            "renderBase" to BooleanPropertyWrapper(this::renderBaseBlock),
            "renderSkybox" to BooleanPropertyWrapper(this::renderSkybox),
            "useTexture" to BooleanPropertyWrapper(this::useTexture),
            "useColor" to BooleanPropertyWrapper(this::useColor),
            "useLight" to BooleanPropertyWrapper(this::useLight),
            "showInvisible" to BooleanPropertyWrapper(this::showHiddenFaces)
    )
}