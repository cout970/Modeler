package com.cout970.modeler.event

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.device.Mouse

/**
 * Created by cout970 on 2016/12/07.
 */
class KeyBindings {

    var rotateCamera = MouseKeyBind(Mouse.BUTTON_RIGHT)
    var moveCamera = MouseKeyBind(Mouse.BUTTON_MIDDLE)
    var selectModel = MouseKeyBind(Mouse.BUTTON_LEFT)
    var selectModelControls = MouseKeyBind(Mouse.BUTTON_LEFT)
    var disableGridMotion = KeyBind(Keyboard.KEY_LEFT_CONTROL)
}