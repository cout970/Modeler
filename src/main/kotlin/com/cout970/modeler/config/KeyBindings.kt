package com.cout970.modeler.config

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.device.Mouse

/**
 * Created by cout970 on 2016/12/07.
 */
class KeyBindings {

    var rotateCamera = MouseKeyBind(Mouse.Companion.BUTTON_RIGHT)
    var moveCamera = MouseKeyBind(Mouse.Companion.BUTTON_MIDDLE)
    var selectModel = MouseKeyBind(Mouse.Companion.BUTTON_LEFT)
    var selectModelControls = MouseKeyBind(Mouse.Companion.BUTTON_LEFT)
    var disableGridMotion = KeyBind(Keyboard.Companion.KEY_LEFT_CONTROL)
}