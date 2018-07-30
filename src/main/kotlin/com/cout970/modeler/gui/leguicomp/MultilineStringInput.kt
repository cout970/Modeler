package com.cout970.modeler.gui.leguicomp

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.util.rectangularCorners
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import org.liquidengine.legui.component.TextArea
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.component.optional.align.VerticalAlign
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
        setTransparent()
        setBorderless()
        defaultTextColor()
        focusedStrokeColor { grey }
        highlightColor { bright3 }
        rectangularCorners()
        // TODO
        textState.fontSize = 18f
        textState.horizontalAlign = HorizontalAlign.LEFT
        textState.verticalAlign = VerticalAlign.TOP

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