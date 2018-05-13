package com.cout970.modeler.gui.rcomponents.popup

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.export.ExportFormat
import com.cout970.modeler.core.export.ExportProperties
import com.cout970.modeler.gui.leguicomp.FixedLabel
import com.cout970.modeler.gui.leguicomp.TextButton
import com.cout970.modeler.gui.leguicomp.background
import com.cout970.modeler.gui.leguicomp.onClick
import com.cout970.modeler.input.dialogs.FileDialogs
import com.cout970.modeler.util.toColor
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
import org.liquidengine.legui.style.border.SimpleLineBorder

data class ExportDialogState(val text: String, val prefix: String, val selection: Int, var forceUpdate: Boolean) : RState

class ExportDialog : RComponent<PopupReturnProps, ExportDialogState>() {

    companion object {
        private val options = listOf("Obj (*.obj)", "MCX (*.mcx)", "GLTF (*.gltf)")

        private fun getExportFileExtensions(format: ExportFormat): List<String> = when (format) {
            ExportFormat.OBJ -> listOf("*.obj")
            ExportFormat.MCX -> listOf("*.mcx")
            ExportFormat.GLTF -> listOf("*.gltf")
        }
    }

    override fun getInitialState() = ExportDialogState("", "magneticraft:blocks/", 1, false)

    override fun RBuilder.render() = div("ExportDialog") {
        style {
            background { darkestColor }
            style.border = SimpleLineBorder(Config.colorPalette.greyColor.toColor(), 2f)
            width = 460f
            height = 240f
        }

        postMount {
            center()
        }

        // first line
        +FixedLabel("Export Model", 0f, 8f, 460f, 24f).apply {
            textState.fontSize = 22f
        }

        //second line
        +FixedLabel("Format", 25f, 50f, 400f, 24f).apply {
            textState.fontSize = 20f
            textState.horizontalAlign = HorizontalAlign.LEFT
        }


        +TextButton("", "Obj (*.obj)", 90f, 50f, 110f, 24f).apply {
            if (state.selection != 0) background { darkColor }

            onClick {
                setState { copy(selection = 0) }
            }
        }

        +TextButton("", "MCX (*.mcx)", 210f, 50f, 110f, 24f).apply {
            if (state.selection != 1) background { darkColor }

            onClick {
                setState { copy(selection = 1) }
            }
        }

        +TextButton("", "GLTF (*.gltf)", 330f, 50f, 110f, 24f).apply {
            if (state.selection != 2) background { darkColor }

            onClick {
                setState { copy(selection = 2) }
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
                        title = "Export",
                        description = options[state.selection],
                        defaultPath = "model." + ExportFormat.values()[state.selection].name.toLowerCase(),
                        filters = getExportFileExtensions(ExportFormat.values()[state.selection])
                )
                if (file != null) {
                    setState { copy(text = file, forceUpdate = true) }
                }
            }
        }

        //fourth line

        +FixedLabel("Prefix", 25f, 150f, 400f, 24f).apply {
            textState.fontSize = 20f
            textState.horizontalAlign = HorizontalAlign.LEFT
        }

        comp(TextInput(state.prefix, 90f, 150f, 350f, 24f)) {
            on<TextInputContentChangeEvent<TextInput>> {
                setState { copy(prefix = it.newValue, forceUpdate = false) }
            }
        }

        //fifth line
        +TextButton("", "Export", 270f, 200f, 80f, 24f).apply {
            onClick {
                props.returnFunc(ExportProperties(
                        path = state.text,
                        format = ExportFormat.values()[state.selection],
                        domain = state.prefix,
                        materialLib = "materials"
                ))
            }
        }

        +TextButton("", "Cancel", 360f, 200f, 80f, 24f).apply {
            onClick {
                props.returnFunc(null)
            }
        }
    }

    override fun shouldComponentUpdate(nextProps: PopupReturnProps, nextState: ExportDialogState): Boolean {
        return state.selection != nextState.selection || nextState.forceUpdate
    }
}