package com.cout970.modeler.core.config

import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.view.event.IInput

/**
 * Created by cout970 on 2016/12/07.
 */
class MouseKeyBind(val keycode: Int) {

    fun check(eventHandler: IInput): Boolean = eventHandler.mouse.isButtonPressed(keycode)
    fun check(event: EventMouseClick): Boolean = event.button == keycode
}