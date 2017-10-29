package com.cout970.modeler.gui.react.components

import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.leguicomp.ToggleButton
import com.cout970.modeler.gui.react.marginX
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.event.MouseClickEvent

/**
 * Created by cout970 on 2017/10/21.
 */
class SelectionTypeButtons : RComponent<SelectionTypeButtons.Props, Unit>() {

    override fun build(ctx: RBuildContext): Component = panel {
        backgroundColor = Config.colorPalette.darkestColor.toColor()
        posY = 28f
        marginX(ctx, 5f)
        height = 48f
        setBorderless()

        val firstButton = props.guiState.selectionType == SelectionType.OBJECT
        val secondButton = props.guiState.selectionType == SelectionType.FACE
        val thirdButton = props.guiState.selectionType == SelectionType.EDGE
        val fourthButton = props.guiState.selectionType == SelectionType.VERTEX

        +ToggleButton("", "selection_mode_object", firstButton, 0f, 0f, 32f, 32f).apply {
            setBorderless()

            listenerMap.addListener(MouseClickEvent::class.java) {
                props.guiState.selectionType = SelectionType.OBJECT
                replaceState(Unit)
            }
        }
        +ToggleButton("", "selection_mode_face", secondButton, 32f + 5f, 0f, 32f, 32f).apply {
            setBorderless()

            listenerMap.addListener(MouseClickEvent::class.java) {
                props.guiState.selectionType = SelectionType.FACE
                replaceState(Unit)
            }
        }
        +ToggleButton("", "selection_mode_edge", thirdButton, 64f + 10f, 0f, 32f, 32f).apply {
            setBorderless()

            listenerMap.addListener(MouseClickEvent::class.java) {
                props.guiState.selectionType = SelectionType.EDGE
                replaceState(Unit)
            }
        }
        +ToggleButton("", "selection_mode_vertex", fourthButton, 96f + 15f, 0f, 32f, 32f).apply {
            setBorderless()

            listenerMap.addListener(MouseClickEvent::class.java) {
                props.guiState.selectionType = SelectionType.VERTEX
                replaceState(Unit)
            }
        }
    }

    class Props(val guiState: GuiState)
    companion object : RComponentSpec<SelectionTypeButtons, SelectionTypeButtons.Props, Unit>

}