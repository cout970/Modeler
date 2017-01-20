package com.cout970.modeler.config

import com.cout970.modeler.event.IInput

/**
 * Created by cout970 on 2016/12/07.
 */
class MouseKeyBind(val keycode: Int) {

    fun check(eventHandler: IInput): Boolean = eventHandler.mouse.isButtonPressed(keycode)
}