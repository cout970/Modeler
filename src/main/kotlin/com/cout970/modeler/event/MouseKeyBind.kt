package com.cout970.modeler.event

import com.cout970.glutilities.device.Mouse

/**
 * Created by cout970 on 2016/12/07.
 */
class MouseKeyBind(val keycode: Int) {

    fun check(mouse: Mouse): Boolean = mouse.isButtonPressed(keycode)
}