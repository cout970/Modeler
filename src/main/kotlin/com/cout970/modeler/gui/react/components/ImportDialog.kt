package com.cout970.modeler.gui.react.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.export.ImportFormat
import com.cout970.modeler.core.export.ImportProperties
import com.cout970.modeler.gui.Popup
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.leguicomp.DropDown
import com.cout970.modeler.gui.react.leguicomp.FixedLabel
import com.cout970.modeler.gui.react.leguicomp.TextButton
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.util.text
import com.cout970.modeler.util.toColor
import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.util.toPointerBuffer
import org.joml.Vector4f
import org.liquidengine.legui.border.SimpleLineBorder
import org.liquidengine.legui.component.CheckBox
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.icon.CharIcon
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.util.*

/**
 * Created by cout970 on 2017/09/28.
 */
class ImportDialog : RComponent<ImportDialog.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuildContext): Component = panel {
        size = ctx.parentSize.toJoml2f()
        backgroundColor = Vector4f(1f, 1f, 1f, 0.05f)

        panel {
            backgroundColor = Config.colorPalette.darkestColor.toColor()
            border = SimpleLineBorder(Config.colorPalette.greyColor.toColor(), 2f)
            width = 460f
            height = 240f
            posX = (ctx.parentSize.xf - width) / 2f
            posY = (ctx.parentSize.yf - height) / 2f

            val path = TextInput("", 90f, 50f, 250f, 24f)
            val dropdown = DropDown("", 90f, 100f, 350f, 24f)
            val uvCheckbox = CheckBox("Flip UV", 360f, 150f, 80f, 24f)

            // first line
            +FixedLabel("Import Model", 0f, 8f, 460f, 24f).apply {
                textState.fontSize = 22f
            }

            //second line
            +FixedLabel("Path", 25f, 50f, 400f, 24f).apply {
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.LEFT
            }

            +path

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
                            if (file.endsWith(".obj")) {
                                dropdown.setSelected(0, true)
                            } else if (file.endsWith(".zip") || file.endsWith(".tcn")) {
                                dropdown.setSelected(1, true)
                            } else if (file.endsWith(".json")) {
                                dropdown.setSelected(2, true)
                            } else if (file.endsWith(".tbl")) {
                                dropdown.setSelected(3, true)
                            } else if (file.endsWith(".mcx")) {
                                dropdown.setSelected(4, true)
                            }
                            path.text = file
                        }
                    }
                }
            }

            //third line
            +FixedLabel("Format", 25f, 100f, 400f, 24f).apply {
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.LEFT
            }

            +dropdown.apply {
                elementHeight = 22f
                buttonWidth = 22f
                visibleCount = 5
                addElement("Obj (*.obj)")
                addElement("Techne (*.tcn, *.zip)")
                addElement("Minecraft (*.json)")
                addElement("Tabula (*.tbl)")
                addElement("MCX (*.mcx)")
                setSelected(0, true)
            }

            //fourth line
            +uvCheckbox.apply {

                backgroundColor = Config.colorPalette.buttonColor.toColor()
                textState.fontSize = 18f
                textState.padding.x = 5f

//            if (dropdown.elements.indexOf(dropdown.selection) != 0) { // disable
//                isEnabled = false
//                textState.textColor = Config.colorPalette.darkestColor.toColor()
//                (iconChecked as CharIcon).color = Config.colorPalette.darkestColor.toColor()
//                (iconUnchecked as CharIcon).color = Config.colorPalette.darkestColor.toColor()
//            } else { // enable
                textState.textColor = Config.colorPalette.textColor.toColor()
                (iconChecked as CharIcon).color = Config.colorPalette.whiteColor.toColor()
                (iconUnchecked as CharIcon).color = Config.colorPalette.whiteColor.toColor()
//            }
            }

            //fifth line
            +TextButton("", "Import", 270f, 200f, 80f, 24f).apply {
                listenerMap.addListener(MouseClickEvent::class.java) {
                    props.popup.returnFunc(ImportProperties(
                            path = path.text,
                            format = ImportFormat.values()[dropdown.selectedIndex],
                            flipUV = uvCheckbox.isChecked
                    ))
                }
            }

            +TextButton("", "Cancel", 360f, 200f, 80f, 24f).apply {
                listenerMap.addListener(MouseClickEvent::class.java) {
                    props.popup.returnFunc(null)
                }
            }

            //TODO fix state
        }
    }


    class Props(val popup: Popup)

    companion object : RComponentSpec<ImportDialog, Props, Unit> {
        private val extensions = Arrays.asList("*.obj", "*.tcn", "*.json", "*.tbl", "*.mcx").toPointerBuffer()
    }
}