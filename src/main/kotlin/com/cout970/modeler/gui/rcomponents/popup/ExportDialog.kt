package com.cout970.modeler.gui.rcomponents.popup

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.export.*
import com.cout970.modeler.gui.leguicomp.FixedLabel
import com.cout970.modeler.gui.leguicomp.TextButton
import com.cout970.modeler.gui.leguicomp.classes
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
import org.joml.Vector2f
import org.liquidengine.legui.component.CheckBox
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.event.checkbox.CheckBoxChangeValueEvent
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.CharIcon
import java.io.File

data class ExportDialogState(
    val text: String,
    val prefix: String,
    val selection: Int,
    val flipUV: Boolean = false,
    val useNormals: Boolean = true,
    var forceUpdate: Boolean = false
) : RState

class ExportDialog : RComponent<PopupReturnProps, ExportDialogState>() {

    companion object {
        private val options = listOf("Obj (*.obj)", "MCX (*.mcx)", "GLTF (*.gltf)", "VS (*.json)")

        private fun getExportFileExtensions(format: ExportFormat): List<String> = when (format) {
            ExportFormat.OBJ -> listOf("*.obj")
            ExportFormat.MCX -> listOf("*.mcx")
            ExportFormat.GLTF -> listOf("*.gltf")
            ExportFormat.VS -> listOf("*.json")
        }

        private var lastPath: String = ""
        private var lastType: Int = 1
    }

    override fun getInitialState() = ExportDialogState(lastPath, "magneticraft:blocks", lastType)

    override fun RBuilder.render() = div("ExportDialog") {
        style {
            width = 460f
            height = 290f
            classes("popup_back")
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


        +TextButton("", "Obj (*.obj)", 90f, 50f, 82.5f, 24f).apply {
            classes("popup_export_type")
            if (state.selection == 0) classes("popup_export_type_selected")

            onClick {
                lastType = 0
                setState { copy(selection = 0) }
            }
        }

        +TextButton("", "MCX (*.mcx)", 180f, 50f, 82.5f, 24f).apply {
            classes("popup_export_type")
            if (state.selection == 1) classes("popup_export_type_selected")

            onClick {
                lastType = 1
                setState { copy(selection = 1) }
            }
        }

        +TextButton("", "GLTF (*.gltf)", 270f, 50f, 82.5f, 24f).apply {
            classes("popup_export_type")
            if (state.selection == 2) classes("popup_export_type_selected")

            onClick {
                lastType = 2
                setState { copy(selection = 2) }
            }
        }

        +TextButton("", "VS (*.json)", 360f, 50f, 82.5f, 24f).apply {
            classes("popup_export_type")
            if (state.selection == 3) classes("popup_export_type_selected")

            onClick {
                lastType = 3
                setState { copy(selection = 3) }
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
                    lastPath = file
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
            style {
                if (state.selection != 1) {
                    isEnabled = false
                    isEditable = false
                    classes("string_input_disabled")
                }
            }

            on<TextInputContentChangeEvent<TextInput>> {
                setState { copy(prefix = it.newValue, forceUpdate = false) }
            }
        }

        //fifth line
        +CheckBox("Flip UV", 25f, 200f, 120f, 24f).apply {

            textState.fontSize = 18f
            paddingLeft(5f)
            isChecked = state.flipUV
            style.setBorderRadius(0f)

            if (state.selection != 0) { // disable
                isEnabled = false
                textState.textColor = Config.colorPalette.dark3.toColor()
                (iconChecked as CharIcon).color = Config.colorPalette.dark3.toColor()
                (iconUnchecked as CharIcon).color = Config.colorPalette.dark3.toColor()
            } else { // enable
                textState.textColor = Config.colorPalette.textColor.toColor()
                (iconChecked as CharIcon).color = Config.colorPalette.bright4.toColor()
                (iconUnchecked as CharIcon).color = Config.colorPalette.bright4.toColor()
            }

            (iconChecked as CharIcon).size = Vector2f(24f)
            (iconUnchecked as CharIcon).size = Vector2f(24f)

            on<CheckBoxChangeValueEvent<CheckBox>> {
                setState { copy(flipUV = it.isNewValue) }
            }
        }

        +CheckBox("Include normals", 25f + 120f + 10f, 200f, 180f, 24f).apply {

            textState.fontSize = 18f
            paddingLeft(5f)
            isChecked = state.useNormals
            style.setBorderRadius(0f)

            if (state.selection != 0) { // disable
                isEnabled = false
                textState.textColor = Config.colorPalette.dark3.toColor()
                (iconChecked as CharIcon).color = Config.colorPalette.dark3.toColor()
                (iconUnchecked as CharIcon).color = Config.colorPalette.dark3.toColor()
            } else { // enable
                textState.textColor = Config.colorPalette.textColor.toColor()
                (iconChecked as CharIcon).color = Config.colorPalette.bright4.toColor()
                (iconUnchecked as CharIcon).color = Config.colorPalette.bright4.toColor()
            }

            (iconChecked as CharIcon).size = Vector2f(24f)
            (iconUnchecked as CharIcon).size = Vector2f(24f)

            on<CheckBoxChangeValueEvent<CheckBox>> {
                setState { copy(useNormals = it.isNewValue) }
            }
        }

        //last line
        div {
            style { classes("div") }

            postMount {
                posY = 250f
                height = 24f
                marginX(25f)
                floatRight(10f, 50f)
            }

            +TextButton("", "Cancel", 0f, 0f, 80f, 24f).apply {
                onClick {
                    props.returnFunc(null)
                }
            }

            +TextButton("", "Export", 0f, 0f, 80f, 24f).apply {
                onClick {
                    val exportProps = when (ExportFormat.values()[state.selection]) {
                        ExportFormat.OBJ -> ObjExportProperties(state.text, File(state.text).nameWithoutExtension, state.useNormals, state.flipUV)
                        ExportFormat.MCX -> McxExportProperties(state.text, state.prefix)
                        ExportFormat.GLTF -> GltfExportProperties(state.text)
                        ExportFormat.VS -> VsExportProperties(state.text)
                    }

                    props.returnFunc(exportProps)
                }
            }
        }
    }

    override fun shouldComponentUpdate(nextProps: PopupReturnProps, nextState: ExportDialogState): Boolean {
        return state.selection != nextState.selection || nextState.forceUpdate
    }
}