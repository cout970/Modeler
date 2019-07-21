package com.cout970.modeler.gui.leguicomp

import com.cout970.glutilities.device.Keyboard
import org.liquidengine.legui.component.TextArea
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.event.FocusEvent
import org.liquidengine.legui.event.KeyEvent

class MultilineStringInput(
        text: String,
        posX: Float = 0f,
        posY: Float = 0f,
        sizeX: Float = 80f,
        sizeY: Float = 24f
) : TextArea(posX, posY, sizeX, sizeY) {

    var onLoseFocus: (() -> Unit)? = null
    var onEnterPress: (() -> Unit)? = null
    var onTextChange: ((TextInputContentChangeEvent<*>) -> Unit)? = null

    init {
        textState.text = text
        defaultTextColor()
        classes("multiline_input")

        listenerMap.addListener(FocusEvent::class.java) {
            if (!it.isFocused) {
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
    }
}