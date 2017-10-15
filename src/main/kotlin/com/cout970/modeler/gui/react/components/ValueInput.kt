package com.cout970.modeler.gui.react.components

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.leguicomp.IconButton
import com.cout970.modeler.gui.react.leguicomp.Panel
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.text
import com.cout970.modeler.util.toColor
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Container
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.misc.listener.textinput.TextInputMouseClickEventListener
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.FocusEvent
import org.liquidengine.legui.event.KeyEvent
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.event.ScrollEvent
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Created by cout970 on 2017/09/28.
 */
class ValueInput : RComponent<ValueInput.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuildContext): Component = panel {
        position = props.pos.toJoml2f()
        width = 75f
        height = 70f
        setTransparent()
        setBorderless()

        val input = TextInput(formatter.format(props.value()), 0f, 16f, 75f, 40f).apply {
            textState.textColor = Config.colorPalette.textColor.toColor()
            textState.horizontalAlign = HorizontalAlign.CENTER
            textState.fontSize = 24f
            backgroundColor = Config.colorPalette.greyColor.toColor()
        }

        +input.apply {
            listenerMap.addListener(ScrollEvent::class.java) {
                dispatch(it.yoffset.toFloat(), input.text)
            }

            listenerMap.addListener(FocusEvent::class.java) {
                if (it.isFocused) {
                    if (input.text.isNotEmpty()) {
                        input.startSelectionIndex = 0
                        input.endSelectionIndex = input.text.length
                        onGainFocus(input)
                    }
                } else {
                    dispatch(0f, input.text)
                }
            }

            listenerMap.addListener(KeyEvent::class.java) {
                if (it.key == Keyboard.KEY_ENTER) {
                    dispatch(0f, input.text)
                }
            }
        }

        +IconButton("", "button_up", 0f, 0f, 75f, 16f).apply {
            backgroundColor = Config.colorPalette.lightDarkColor.toColor()
            listenerMap.addListener(MouseClickEvent::class.java) {
                if (it.action == MouseClickEvent.MouseClickAction.RELEASE)
                    dispatch(1f, input.text)
            }
        }

        +IconButton("", "button_down", 0f, 56f, 75f, 16f).apply {
            backgroundColor = Config.colorPalette.lightDarkColor.toColor()
            listenerMap.addListener(MouseClickEvent::class.java) {
                if (it.action == MouseClickEvent.MouseClickAction.RELEASE)
                    dispatch(-1f, input.text)
            }
        }

        if (props.ref.objectIndex == -1) {
            disableInput()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun Component.disableInput() {
        when (this) {
            is Container<*> -> (this as Container<Component>).childs.forEach { it.disableInput() }
            is Button -> this.isEnabled = false
            is TextInput -> {
                this.isEnabled = false
                this.isEditable = false
            }
        }
    }

    fun dispatch(offset: Float, content: String) {
        val data = Panel().apply {
            metadata += "cube_ref" to props.ref
            metadata += "offset" to offset
            metadata += "command" to props.cmd
            metadata += "content" to content
        }
        props.dispatcher.onEvent("update.template.cube", data)
    }

    class Props(
            val dispatcher: Dispatcher,
            val value: () -> Float,
            val cmd: String,
            val ref: IObjectRef,
            val pos: IVector2
    )

    fun onGainFocus(input: TextInput) {
        input.listenerMap.getListeners(MouseClickEvent::class.java)
                .firstOrNull()
                .asNullable()
                .flatMap { it as? MouseClickEventListener }
                .map { it.ignoreNextEvent = true }
    }

    class MouseClickEventListener : TextInputMouseClickEventListener() {
        var ignoreNextEvent = false

        override fun process(event: MouseClickEvent<*>) {
            if (event.action != MouseClickEvent.MouseClickAction.PRESS) return
            if (ignoreNextEvent) {
                ignoreNextEvent = false
                return
            }
            super.process(event)
        }
    }

    companion object : RComponentSpec<ValueInput, ValueInput.Props, Unit> {
        val formatter = DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
    }
}

