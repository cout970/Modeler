package com.cout970.modeler.gui

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.gui.canvas.tool.Cursor3D
import com.cout970.modeler.util.BooleanPropertyWrapper
import com.cout970.modeler.util.GuiProperty

/**
 * Created by cout970 on 2017/06/12.
 */
class GuiState(val projectManager: IProgramState) : IProgramState by projectManager {

    var selectionType: SelectionType by GuiProperty(SelectionType.OBJECT, "SelectionType")

    val cursor = Cursor3D()

    var useTexture: Boolean = true

    var useColor: Boolean = false
    var useLight: Boolean = true
    var showHiddenFaces: Boolean = false
    var drawTextureProjection: Boolean = true
    var drawTextureGridLines: Boolean = true
    var drawOutline: Boolean = false

    var renderLights: Boolean = false
    var renderSkybox: Boolean = true
    var renderBaseBlock: Boolean = true

    var popup: Popup? = null

    var tmpModel: IModel? = null

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