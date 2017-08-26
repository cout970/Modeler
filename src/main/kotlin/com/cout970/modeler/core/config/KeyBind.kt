package com.cout970.modeler.core.config

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.input.event.IInput
import org.lwjgl.glfw.GLFW

/**
 * Created by cout970 on 2016/12/17.
 */
class KeyBind(val keycode: Int, vararg val mods: KeyboardModifiers) {

    fun check(input: IInput): Boolean = input.keyboard.isKeyPressed(keycode) && mods.all { it.check(input) }
    fun check(event: EventKeyUpdate): Boolean = event.keycode == keycode && mods.all { it.check(event) }

    override fun toString(): String {
        var prefix = ""
        if (mods.isNotEmpty()) {
            prefix = mods.joinToString(" + ") + " + "
        }
        return prefix + GLFW.glfwGetKeyName(keycode, keycode)
    }
}

enum class KeyboardModifiers(val id: Int, val mask: Int) {
    CTRL(Keyboard.KEY_LEFT_CONTROL, GLFW.GLFW_MOD_CONTROL),
    SHIFT(Keyboard.KEY_LEFT_SHIFT, GLFW.GLFW_MOD_SHIFT),
    ALT(Keyboard.KEY_LEFT_ALT, GLFW.GLFW_MOD_ALT);

    fun check(input: IInput) = input.keyboard.isKeyPressed(id)
    fun check(event: EventKeyUpdate) = (event.mods and mask) == mask
}