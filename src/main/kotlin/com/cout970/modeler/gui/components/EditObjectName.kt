package com.cout970.modeler.gui.components

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.reactive.RBuildContext
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.FixedLabel
import com.cout970.modeler.gui.leguicomp.marginX
import com.cout970.modeler.gui.leguicomp.panel
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.disable
import com.cout970.modeler.util.setTransparent
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.FocusEvent
import org.liquidengine.legui.event.KeyEvent
import org.liquidengine.legui.event.MouseClickEvent

/**
 * Created by cout970 on 2017/10/29.
 */
class EditObjectName : RComponent<EditObjectName.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuildContext): Component = panel {

        marginX(ctx, 5f)
        posY = 70f
        height = 64f
        setTransparent()

        listenerMap.addListener(EventModelUpdate::class.java) {
            replaceState(state)
        }
        listenerMap.addListener(EventSelectionUpdate::class.java) {
            replaceState(state)
        }

        val model = props.access.model
        val selection = props.access.modelSelection
        val obj = selection
                .filter { it.size == 1 }
                .flatMap { it.refs.firstOrNull() }
                .filterIsInstance<IObjectRef>()
                .map { model.getObject(it) }

        val text = obj.map { it.name }.getOr("")

        +FixedLabel("Name", 0f, 0f, width, 24f).apply {
            textState.textColor = Config.colorPalette.textColor.toColor()
            textState.horizontalAlign = HorizontalAlign.CENTER
            textState.fontSize = 20f
        }

        +TextInput(text, 10f, 24f, width - 20f, 32f).apply {
            textState.textColor = Config.colorPalette.textColor.toColor()
            textState.horizontalAlign = HorizontalAlign.CENTER
            backgroundColor = Config.colorPalette.greyColor.toColor()
            textState.fontSize = 24f
            cornerRadius = 0f
            obj.ifNull {
                isEditable = false
                this.disable()
            }
            obj.ifNotNull {

                listenerMap.addListener(MouseClickEvent::class.java, ValueInput.MouseClickEventListener())

                listenerMap.addListener(FocusEvent::class.java) {
                    if (it.isFocused) {
                        if (text.isNotEmpty()) {
                            startSelectionIndex = 0
                            endSelectionIndex = text.length
                            onGainFocus(this)
                        }
                    } else {
                        props.dispatcher.onEvent("mode.obj.change.name", this)
                    }
                }

                listenerMap.addListener(KeyEvent::class.java) {
                    if (it.key == Keyboard.KEY_ENTER) {
                        props.dispatcher.onEvent("mode.obj.change.name", this)
                    }
                }
                listenerMap.addListener(TextInputContentChangeEvent::class.java) {
                    props.dispatcher.onEvent("mode.obj.change.name", this)
                }
            }
        }
    }

    fun onGainFocus(input: TextInput) {
        input.listenerMap.getListeners(MouseClickEvent::class.java)
                .firstOrNull()
                .asNullable()
                .flatMap { it as? ValueInput.MouseClickEventListener }
                .map { it.ignoreNextEvent = true }
    }

    class Props(val access: IModelAccessor, val dispatcher: Dispatcher)

    companion object : RComponentSpec<EditObjectName, EditObjectName.Props, Unit>
}