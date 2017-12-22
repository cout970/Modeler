package com.cout970.modeler.core.config

import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.input.event.IInput

/**
 * Created by cout970 on 2016/12/07.
 */
class MouseKeyBind(val button: Int) {

    fun check(eventHandler: IInput): Boolean = eventHandler.mouse.isButtonPressed(button)
    fun check(event: EventMouseClick): Boolean = event.button == button
}