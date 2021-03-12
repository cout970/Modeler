package com.cout970.modeler.gui.rcomponents.popup

import com.cout970.modeler.core.export.ImportFormat
import com.cout970.modeler.core.export.ImportProperties
import com.cout970.modeler.gui.leguicomp.FixedLabel
import com.cout970.modeler.gui.leguicomp.TextButton
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.leguicomp.onClick
import com.cout970.modeler.input.dialogs.FileDialogs
import com.cout970.modeler.util.disableInput
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RState
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.selectBox
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.CheckBox
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.event.checkbox.CheckBoxChangeValueEvent
import org.liquidengine.legui.component.event.selectbox.SelectBoxChangeSelectionEvent
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign


data class ImportDialogState(val text: String, val option: Int, val flipUV: Boolean, val forceUpdate: Boolean) : RState

class ImportDialog : RComponent<PopupReturnProps, ImportDialogState>() {

    companion object {
        private val options = listOf("Obj (*.obj)", "Techne (*.tcn, *.zip)", "Minecraft (*.json)",
                "Tabula (*.tbl)", "MCX (*.mcx)", "Project (*.pff)", "GL Transport Format (*.gltf)")
    }

    override fun getInitialState() = ImportDialogState("", 0, false, false)

    override fun RBuilder.render() = div("ImportPopup") {
        style {
            borderSize = 2f
            width = 460f
            height = 240f
            classes("popup_back")
        }

        postMount {
            center()
        }

        // first line
        +FixedLabel("Import Model", 0f, 8f, 460f, 24f).apply {
            style.fontSize = 22f
        }

        //second line
        +FixedLabel("Path", 25f, 50f, 64f, 24f).apply {
            style.fontSize = 20f
            style.horizontalAlign = HorizontalAlign.LEFT
        }

        +TextInput(state.text, 90f, 50f, 250f, 24f).apply {
            on<TextInputContentChangeEvent<TextInput>> {
                setState { copy(text = it.newValue) }
            }
        }

        +TextButton("", "Select", 360f, 50f, 80f, 24f).apply {

            onRelease {
                val file = FileDialogs.openFile(
                        title = "Import",
                        description = "Model Files (*.tcn, *.obj, *.json, *.tbl, *.mcx, *.pff, *.gltf)",
                        filters = listOf("*.obj", "*.tcn", "*.json", "*.tbl", "*.mcx", "*.pff", "*.gltf")
                )

                if (file != null) {
                    val newOption = when {
                        file.endsWith(".obj") -> 0
                        file.endsWith(".zip") || file.endsWith(".tcn") -> 1
                        file.endsWith(".json") -> 2
                        file.endsWith(".tbl") -> 3
                        file.endsWith(".mcx") -> 4
                        file.endsWith(".pff") -> 5
                        file.endsWith(".gltf") -> 6
                        else -> state.option
                    }
                    setState { copy(text = file, option = newOption, forceUpdate = !forceUpdate) }
                }
            }
        }

        //third line
        +FixedLabel("Format", 25f, 100f, 64f, 24f).apply {
            style.fontSize = 20f
            style.horizontalAlign = HorizontalAlign.LEFT
        }

        selectBox {
            style {
                posX = 90f
                posY = 100f
                sizeX = 350f
                sizeY = 24f
                elementHeight = 22f
                buttonWidth = 22f
                visibleCount = 4
                options.forEach { addElement(it) }
                setSelected(state.option, true)
            }

            childrenAsNodes()

            on<SelectBoxChangeSelectionEvent<String>> {
                setState { copy(option = options.indexOf(it.newValue)) }
            }
        }

        //fourth line
        +CheckBox("Flip UV", 360f, 150f, 80f, 24f).apply {

            classes("import_checkbox")
            paddingLeft(5f)
            isChecked = state.flipUV

            if (state.option == 0) { // enable
                classes("checkbox_icon_on")
            } else { // disable
                disableInput()
                classes("checkbox_disable", "checkbox_icon_off")
            }

            on<CheckBoxChangeValueEvent<CheckBox>> {
                setState { copy(flipUV = it.newValue) }
            }
        }

        //fifth line
        +TextButton("", "Replace", 180f, 200f, 80f, 24f).apply {
            onClick {
                props.returnFunc(ImportProperties(
                        path = state.text,
                        format = ImportFormat.values()[state.option],
                        flipUV = state.flipUV,
                        append = false
                ))
            }
        }

        +TextButton("", "Append", 270f, 200f, 80f, 24f).apply {
            onClick {
                props.returnFunc(ImportProperties(
                        path = state.text,
                        format = ImportFormat.values()[state.option],
                        flipUV = state.flipUV,
                        append = true
                ))
            }
        }

        +TextButton("", "Cancel", 360f, 200f, 80f, 24f).apply {
            onClick {
                props.returnFunc(null)
            }
        }
    }

    override fun shouldComponentUpdate(nextProps: PopupReturnProps, nextState: ImportDialogState): Boolean {
        return state.flipUV != nextState.flipUV || state.option != nextState.option || state.forceUpdate != nextState.forceUpdate
    }
}