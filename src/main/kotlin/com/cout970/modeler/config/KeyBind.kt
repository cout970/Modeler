package com.cout970.modeler.config

import com.cout970.glutilities.device.Keyboard
import com.cout970.modeler.event.IInput

/**
 * Created by cout970 on 2016/12/17.
 */
class KeyBind(val keycode: Int, vararg val mods: KeyboardModifiers) {


    fun check(input: IInput): Boolean = input.keyboard.isKeyPressed(keycode) && mods.all { it.check(input) }
}

enum class KeyboardModifiers(val id: Int) {
    CTRL(Keyboard.KEY_LEFT_CONTROL),
    SHIFT(Keyboard.KEY_LEFT_SHIFT),
    ALT(Keyboard.KEY_LEFT_ALT);

    fun check(input: IInput) = input.keyboard.isKeyPressed(id)
}