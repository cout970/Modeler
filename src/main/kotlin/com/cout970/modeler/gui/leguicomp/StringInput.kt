package com.cout970.modeler.gui.leguicomp

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.util.asNullable
import com.cout970.reactive.dsl.borderless
import com.cout970.reactive.dsl.rectCorners
import com.cout970.reactive.dsl.transparent
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
        transparent()
        borderless()
        defaultTextColor()
        focusedStrokeColor { greyColor }
        highlightColor { brightestColor }
        rectCorners()
        textState.fontSize = 18f

        listenerMap.addListener(MouseClickEvent::class.java, MouseClickEventListener())

        listenerMap.addListener(FocusEvent::class.java) {
            if (it.isFocused) {
                if (text.isNotEmpty()) {
                    startSelectionIndex = 0
                    endSelectionIndex = text.length
                    onGainFocus()
                }
            } else {
                onLoseFocus?.invoke()
            }
        }

        listenerMap.addListener(KeyEvent::class.java) {
            if (it.key == Keyboard.KEY_ENTER) {
                onEnterPress?.invoke()
            }
        }
        listenerMap.addListener(TextInputContentChangeEvent::class.java) {
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
            if (ignoreNextEvent) {
                ignoreNextEvent = false
                return
            }
            super.process(event)
        }
    }
}