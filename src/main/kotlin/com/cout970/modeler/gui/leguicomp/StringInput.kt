package com.cout970.modeler.gui.leguicomp

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.text
import com.cout970.reactive.dsl.replaceListener
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.component.misc.listener.textinput.TextInputMouseClickEventListener
import org.liquidengine.legui.event.FocusEvent
import org.liquidengine.legui.event.KeyEvent
import org.liquidengine.legui.event.MouseClickEvent

class StringInput(
        var eventName: String? = null,
        text: String = "",
        posX: Float = 0f,
        posY: Float = 0f,
        sizeX: Float = 80f,
        sizeY: Float = 24f
) : TextInput(text, posX, posY, sizeX, sizeY) {


    var onLoseFocus: (() -> Unit)? = null
    var onEnterPress: (() -> Unit)? = null
    var onTextChange: ((TextInputContentChangeEvent<*>) -> Unit)? = null

    init {
        defaultTextColor()
        focusedStrokeColor { grey }
        highlightColor { bright3 }
        classes("string_input")
        textState.fontSize = 18f

        listenerMap.replaceListener(MouseClickEvent::class.java, MouseClickEventListener())

        listenerMap.addListener(FocusEvent::class.java) {
            if (!it.targetComponent.isEnabled) return@addListener
            if (it.isFocused) {
                if (this.text.isNotEmpty()) {
                    startSelectionIndex = 0
                    endSelectionIndex = this.text.length
                    onGainFocus()
                }
            } else {
                onLoseFocus?.invoke()
            }
        }

        listenerMap.addListener(KeyEvent::class.java) {
            if (!it.targetComponent.isEnabled) return@addListener
            if (it.key == Keyboard.KEY_ENTER) {
                onEnterPress?.invoke()
            }
        }
        listenerMap.addListener(TextInputContentChangeEvent::class.java) {
            if (!it.targetComponent.isEnabled) return@addListener
            onTextChange?.invoke(it)
        }

        eventName?.let { event ->
            onLoseFocus = {
                dispatch(event)
            }
            onEnterPress = {
                dispatch(event)
            }
            onTextChange = {
                dispatch(event)
            }
        }

    }

    fun onGainFocus() {
        listenerMap.getListeners(MouseClickEvent::class.java)
                .firstOrNull()
                .asNullable()
                .flatMap { it as? StringInput.MouseClickEventListener }
                .map { it.ignoreNextEvent = true }
    }


    class MouseClickEventListener : TextInputMouseClickEventListener() {
        var ignoreNextEvent = false

        override fun process(event: MouseClickEvent<*>) {
            if (event.action != MouseClickEvent.MouseClickAction.PRESS) return
            if (ignoreNextEvent || !event.targetComponent.isEnabled) {
                ignoreNextEvent = false
                return
            }
            super.process(event)
        }
    }
}
