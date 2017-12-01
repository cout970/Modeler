package com.cout970.modeler.gui.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.export.ExportFormat
import com.cout970.modeler.core.export.ExportProperties
import com.cout970.modeler.gui.Popup
import com.cout970.modeler.gui.leguicomp.DropDown
import com.cout970.modeler.gui.leguicomp.FixedLabel
import com.cout970.modeler.gui.leguicomp.TextButton
import com.cout970.modeler.gui.leguicomp.panel
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.util.toColor
import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.util.toPointerBuffer
import org.joml.Vector4f
import org.liquidengine.legui.border.SimpleLineBorder
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.event.selectbox.SelectBoxChangeSelectionEvent
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.MouseClickEvent
import org.lwjgl.PointerBuffer
import org.lwjgl.util.tinyfd.TinyFileDialogs

/**
 * Created by cout970 on 2017/09/30.
 */
class ExportDialog : RComponent<ExportDialog.Props, ExportDialog.State>() {

    init {
        state = State("", 0)
    }

    override fun build(ctx: RBuilder): Component = panel {
        size = ctx.parentSize.toJoml2f()
        backgroundColor = Vector4f(1f, 1f, 1f, 0.05f)

        +panel {
            backgroundColor = Config.colorPalette.darkestColor.toColor()
            border = SimpleLineBorder(Config.colorPalette.greyColor.toColor(), 2f)
            width = 460f
            height = 240f
            posX = (ctx.parentSize.xf - width) / 2f
            posY = (ctx.parentSize.yf - height) / 2f


            // first line
            +FixedLabel("Export Model", 0f, 8f, 460f, 24f).apply {
                textState.fontSize = 22f
            }

            //second line
            +FixedLabel("Format", 25f, 50f, 400f, 24f).apply {
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.LEFT
            }

            +DropDown("", 90f, 50f, 350f, 24f).apply {
                elementHeight = 22f
                buttonWidth = 22f
                visibleCount = 2
                options.forEach { addElement(it) }
                setSelected(state.selection, true)

                listenerMap.addListener(SelectBoxChangeSelectionEvent::class.java) {
                    replaceState(state.copy(selection = options.indexOf(it.newValue)))
                }
            }

            //third line
            +FixedLabel("Path", 25f, 100f, 400f, 24f).apply {
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.LEFT
            }

            +TextInput(state.text, 90f, 100f, 250f, 24f).apply {
                listenerMap.addListener(TextInputContentChangeEvent::class.java) {
                    replaceState(state.copy(text = it.newValue, forceUpdate = false))
                }
            }

            +TextButton("", "Select", 360f, 100f, 80f, 24f).apply {
                listenerMap.addListener(MouseClickEvent::class.java) {
                    if (it.action == MouseClickEvent.MouseClickAction.RELEASE) {
                        val file = TinyFileDialogs.tinyfd_saveFileDialog(
                                "Export",
                                "model." + ExportFormat.values()[state.selection].name.toLowerCase(),
                                getExportFileExtensions(ExportFormat.values()[state.selection]),
                                options[state.selection]
                        )

                        if (file != null) {
                            replaceState(state.copy(text = file, forceUpdate = true))
                        }
                    }
                }
            }

            //fourth line

            //fifth line
            +TextButton("", "Export", 270f, 200f, 80f, 24f).apply {
                listenerMap.addListener(MouseClickEvent::class.java) {
                    props.popup.returnFunc(ExportProperties(
                            path = state.text,
                            format = ExportFormat.values()[state.selection],
                            domain = "domain",
                            materialLib = "materials"
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


    class Props(val popup: Popup)
    data class State(val text: String, val selection: Int, var forceUpdate: Boolean = false)

    override fun shouldComponentUpdate(nextProps: Props, nextState: State): Boolean {
        return state.selection != nextState.selection || nextState.forceUpdate
    }

    companion object : RComponentSpec<ExportDialog, Props, State> {
        private val options = listOf("Obj (*.obj)", "MCX (*.mcx)")
        private val exportExtensionsObj = listOf("*.obj").toPointerBuffer()
        private val exportExtensionsMcx = listOf("*.mcx").toPointerBuffer()

        private fun getExportFileExtensions(format: ExportFormat): PointerBuffer = when (format) {
            ExportFormat.OBJ -> exportExtensionsObj
            ExportFormat.MCX -> exportExtensionsMcx
        }
    }

}