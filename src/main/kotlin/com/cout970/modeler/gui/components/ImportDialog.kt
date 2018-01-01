package com.cout970.modeler.gui.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.export.ImportFormat
import com.cout970.modeler.core.export.ImportProperties
import com.cout970.modeler.gui.Popup
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.util.toColor
import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.util.toPointerBuffer
import org.joml.Vector4f
import org.liquidengine.legui.border.SimpleLineBorder
import org.liquidengine.legui.component.CheckBox
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.event.checkbox.CheckBoxChangeValueEvent
import org.liquidengine.legui.component.event.selectbox.SelectBoxChangeSelectionEvent
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.icon.CharIcon
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.util.*

/**
 * Created by cout970 on 2017/09/28.
 */
class ImportDialog : RComponent<ImportDialog.Props, ImportDialog.State>() {

    init {
        state = State("", 0, false, false)
    }

    override fun build(ctx: RBuilder): Component = panel {
        size = ctx.parentSize.toJoml2f()
        backgroundColor = Vector4f(1f, 1f, 1f, 0.05f)

        // Centered panel
        +panel {
            backgroundColor = Config.colorPalette.darkestColor.toColor()
            border = SimpleLineBorder(Config.colorPalette.greyColor.toColor(), 2f)
            width = 460f
            height = 240f
            posX = (ctx.parentSize.xf - width) / 2f
            posY = (ctx.parentSize.yf - height) / 2f


            // first line
            +FixedLabel("Import Model", 0f, 8f, 460f, 24f).apply {
                textState.fontSize = 22f
            }

            //second line
            +FixedLabel("Path", 25f, 50f, 400f, 24f).apply {
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.LEFT
            }

            +TextInput(state.text, 90f, 50f, 250f, 24f).apply {
                listenerMap.addListener(TextInputContentChangeEvent::class.java) {
                    replaceState(state.copy(text = it.newValue))
                }
            }

            +TextButton("", "Select", 360f, 50f, 80f, 24f).apply {
                listenerMap.addListener(MouseClickEvent::class.java) {
                    if (it.action == MouseClickEvent.MouseClickAction.RELEASE) {
                        val file = TinyFileDialogs.tinyfd_openFileDialog(
                                "Import",
                                "",
                                extensions,
                                "Model Files (*.tcn, *.obj, *.json, *.tbl, *.mcx)",
                                false
                        )
                        if (file != null) {
                            val newOption = when {
                                file.endsWith(".obj") -> 0
                                file.endsWith(".zip") || file.endsWith(".tcn") -> 1
                                file.endsWith(".json") -> 2
                                file.endsWith(".tbl") -> 3
                                file.endsWith(".mcx") -> 4
                                else -> state.option
                            }
                            replaceState(state.copy(text = file, option = newOption, forceUpdate = !state.forceUpdate))
                        }
                    }
                }
            }

            //third line
            +FixedLabel("Format", 25f, 100f, 400f, 24f).apply {
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.LEFT
            }

            +DropDown("", 90f, 100f, 350f, 24f).apply {
                elementHeight = 22f
                buttonWidth = 22f
                visibleCount = 5
                options.forEach { addElement(it) }
                setSelected(state.option, true)

                listenerMap.addListener(SelectBoxChangeSelectionEvent::class.java) {
                    replaceState(state.copy(option = options.indexOf(it.newValue)))
                }
            }

            //fourth line
            +CheckBox("Flip UV", 360f, 150f, 80f, 24f).apply {

                backgroundColor = Config.colorPalette.buttonColor.toColor()
                textState.fontSize = 18f
                textState.padding.x = 5f
                isChecked = state.flipUV

                if (state.option != 0) { // disable
                    isEnabled = false
                    textState.textColor = Config.colorPalette.darkestColor.toColor()
                    (iconChecked as CharIcon).color = Config.colorPalette.darkestColor.toColor()
                    (iconUnchecked as CharIcon).color = Config.colorPalette.darkestColor.toColor()
                } else { // enable
                    textState.textColor = Config.colorPalette.textColor.toColor()
                    (iconChecked as CharIcon).color = Config.colorPalette.whiteColor.toColor()
                    (iconUnchecked as CharIcon).color = Config.colorPalette.whiteColor.toColor()
                }

                listenerMap.addListener(CheckBoxChangeValueEvent::class.java) {
                    replaceState(state.copy(flipUV = it.isNewValue))
                }
            }

            //fifth line
            +TextButton("", "Replace", 180f, 200f, 80f, 24f).apply {
                listenerMap.addListener(MouseClickEvent::class.java) {
                    props.popup.returnFunc(ImportProperties(
                            path = state.text,
                            format = ImportFormat.values()[state.option],
                            flipUV = state.flipUV,
                            append = false
                    ))
                }
            }

            +TextButton("", "Append", 270f, 200f, 80f, 24f).apply {
                listenerMap.addListener(MouseClickEvent::class.java) {
                    props.popup.returnFunc(ImportProperties(
                            path = state.text,
                            format = ImportFormat.values()[state.option],
                            flipUV = state.flipUV,
                            append = true
                    ))
                }
            }

            +TextButton("", "Cancel", 360f, 200f, 80f, 24f).apply {
                listenerMap.addListener(MouseClickEvent::class.java) {
                    props.popup.returnFunc(null)
                }
            }
        }
    }


    override fun shouldComponentUpdate(nextProps: Props, nextState: State): Boolean {
        return state.flipUV != nextState.flipUV || state.option != nextState.option || state.forceUpdate != nextState.forceUpdate
    }

    class Props(val popup: Popup)

    data class State(val text: String, val option: Int, val flipUV: Boolean, val forceUpdate: Boolean)

    companion object : RComponentSpec<ImportDialog, Props, State> {
        private val options = listOf(
                "Obj (*.obj)",
                "Techne (*.tcn, *.zip)",
                "Minecraft (*.json)",
                "Tabula (*.tbl)",
                "MCX (*.mcx)"
        )
        private val extensions = Arrays.asList("*.obj", "*.tcn", "*.json", "*.tbl", "*.mcx").toPointerBuffer()
    }
}