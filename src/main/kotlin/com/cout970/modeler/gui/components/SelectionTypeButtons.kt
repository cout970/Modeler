package com.cout970.modeler.gui.components

import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.util.setBorderless
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.event.MouseClickEvent

/**
 * Created by cout970 on 2017/10/21.
 */
class SelectionTypeButtons : RComponent<SelectionTypeButtons.Props, Unit>() {

    override fun build(ctx: RBuilder): Component = panel {
        background { blackColor }
        posX = 432f + 10f
        posY = 5f
        width = 143f + 8f
        height = 40f
        cornerRadius = 5f
        setBorderless()

        val firstButton = props.guiState.selectionType == SelectionType.OBJECT
        val secondButton = props.guiState.selectionType == SelectionType.FACE
        val thirdButton = props.guiState.selectionType == SelectionType.EDGE
        val fourthButton = props.guiState.selectionType == SelectionType.VERTEX

        +ToggleButton("", "selection_mode_object", firstButton, 4f, 4f, 32f, 32f).apply {
            setBorderless()
            tooltip = InstantTooltip("Selection mode: OBJECT")

            listenerMap.addListener(MouseClickEvent::class.java) {
                props.guiState.selectionType = SelectionType.OBJECT
                replaceState(Unit)
            }
        }
        +ToggleButton("", "selection_mode_face", secondButton, 32f + 5f + 4f, 4f, 32f, 32f).apply {
            setBorderless()
            tooltip = InstantTooltip("Selection mode: FACE")

            listenerMap.addListener(MouseClickEvent::class.java) {
                props.guiState.selectionType = SelectionType.FACE
                replaceState(Unit)
            }
        }
        +ToggleButton("", "selection_mode_edge", thirdButton, 64f + 10f + 4f, 4f, 32f, 32f).apply {
            setBorderless()
            tooltip = InstantTooltip("Selection mode: EDGE")

            listenerMap.addListener(MouseClickEvent::class.java) {
                props.guiState.selectionType = SelectionType.EDGE
                replaceState(Unit)
            }
        }
        +ToggleButton("", "selection_mode_vertex", fourthButton, 96f + 15f + 4f, 4f, 32f, 32f).apply {
            setBorderless()
            tooltip = InstantTooltip("Selection mode: VERTEX")

            listenerMap.addListener(MouseClickEvent::class.java) {
                props.guiState.selectionType = SelectionType.VERTEX
                replaceState(Unit)
            }
        }
    }

    class Props(val guiState: GuiState)
    companion object : RComponentSpec<SelectionTypeButtons, SelectionTypeButtons.Props, Unit>

}