package com.cout970.modeler.gui.rcomponents.popup

import com.cout970.modeler.core.export.ExportTextureProperties
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.input.dialogs.FileDialogs
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RState
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign

data class ExportTextureDialogState(val text: String, val size: Int, var forceUpdate: Boolean) : RState

class ExportTextureDialog : RComponent<PopupReturnProps, ExportTextureDialogState>() {

    override fun getInitialState() = ExportTextureDialogState("", 64, false)

    override fun RBuilder.render() = div("ExportDialog") {
        style {
            width = 460f
            height = 240f
            classes("popup_back")
        }

        postMount {
            center()
        }

        // first line
        +FixedLabel("Export Texture Template", 0f, 8f, 460f, 24f).apply {
            textState.fontSize = 22f
        }

        //second line
        +FixedLabel("Scale", 25f, 50f, 400f, 24f).apply {
            textState.fontSize = 20f
            textState.horizontalAlign = HorizontalAlign.LEFT
        }

        comp(TextInput(state.size.toString(), 90f, 50f, 250f, 24f)) {
            style {
                horizontalAlign = HorizontalAlign.RIGHT
                fontSize(20f)
            }

            on<TextInputContentChangeEvent<TextInput>> {
                setState { copy(size = it.newValue.toIntOrNull() ?: size) }
            }
        }

        //third line
        +FixedLabel("Path", 25f, 100f, 400f, 24f).apply {
            textState.fontSize = 20f
            textState.horizontalAlign = HorizontalAlign.LEFT
        }

        comp(TextInput(state.text, 90f, 100f, 250f, 24f)) {
            on<TextInputContentChangeEvent<TextInput>> {
                setState { copy(text = it.newValue, forceUpdate = false) }
            }
        }

        comp(TextButton("", "Select", 360f, 100f, 80f, 24f)) {
            onRelease {
                val file = FileDialogs.saveFile(
                        title = "Export Texture Template",
                        description = "PNG texture (*.png)",
                        defaultPath = "template.png",
                        filters = listOf("*.png")
                )

                if (file != null) {
                    setState { copy(text = file, forceUpdate = true) }
                }
            }
        }

        //fourth line

        //fifth line
        +TextButton("", "Export", 270f, 200f, 80f, 24f).apply {
            onClick {
                props.returnFunc(ExportTextureProperties(
                        path = state.text,
                        size = state.size
                ))
            }
        }

        +TextButton("", "Cancel", 360f, 200f, 80f, 24f).apply {
            onClick {
                props.returnFunc(null)
            }
        }
    }

    override fun shouldComponentUpdate(nextProps: PopupReturnProps, nextState: ExportTextureDialogState): Boolean {
        return state.size != nextState.size || nextState.forceUpdate
    }
}