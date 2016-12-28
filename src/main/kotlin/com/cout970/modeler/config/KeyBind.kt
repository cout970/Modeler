package com.cout970.modeler.config

import com.cout970.glutilities.device.Keyboard

/**
 * Created by cout970 on 2016/12/17.
 */
class KeyBind(val keycode: Int) {

    fun check(keyboard: Keyboard): Boolean = keyboard.isKeyPressed(keycode)
}