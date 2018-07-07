package com.cout970.modeler.gui.rcomponents.popup

import com.cout970.modeler.gui.leguicomp.FixedLabel
import com.cout970.modeler.gui.leguicomp.TextButton
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.leguicomp.onClick
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RState
import com.cout970.reactive.dsl.center
import com.cout970.reactive.dsl.height
import com.cout970.reactive.dsl.postMount
import com.cout970.reactive.dsl.width
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign


data class SelectNameDialogState(val text: String) : RState

class SelectNameDialog : RComponent<PopupReturnProps, SelectNameDialogState>() {

    override fun getInitialState() = SelectNameDialogState("Unnamed")

    override fun RBuilder.render() = div("SelectName") {
        style {
            width = 460f
            height = 140f
            classes("popup_back")
        }

        postMount {
            center()
        }

        // first line
        +FixedLabel("New Project Name", 0f, 8f, 460f, 24f).apply {
            textState.fontSize = 22f
        }

        //second line
        +FixedLabel("Name", 25f, 50f, 400f, 24f).apply {
            textState.fontSize = 20f
            textState.horizontalAlign = HorizontalAlign.LEFT
        }

        comp(TextInput(state.text, 90f, 50f, 250f, 24f)) {
            on<TextInputContentChangeEvent<TextInput>> {
                setState { copy(text = it.newValue) }
            }
        }

        //fifth line
        +TextButton("", "Create", 270f, 100f, 80f, 24f).apply {
            onClick {
                props.returnFunc(state.text)
            }
        }

        +TextButton("", "Cancel", 360f, 100f, 80f, 24f).apply {
            onClick {
                props.returnFunc(null)
            }
        }
    }

    override fun shouldComponentUpdate(nextProps: PopupReturnProps, nextState: SelectNameDialogState): Boolean {
        return false
    }
}