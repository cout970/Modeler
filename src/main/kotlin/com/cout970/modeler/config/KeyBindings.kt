package com.cout970.modeler.config

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
    var jumpCameraToCursor = MouseKeyBind(Mouse.BUTTON_RIGHT)

    var multipleSelection = KeyBind(Keyboard.KEY_LEFT_CONTROL)
    var disableGridMotion = KeyBind(Keyboard.KEY_LEFT_CONTROL)
    var disablePixelGridMotion = KeyBind(Keyboard.KEY_LEFT_SHIFT)
    var switchCameraAxis = KeyBind(Keyboard.KEY_P)
    var switchOrthoProjection = KeyBind(Keyboard.KEY_O)
    var slowCameraMovements = KeyBind(Keyboard.KEY_LEFT_SHIFT)
    var moveCameraToCursor = KeyBind(Keyboard.KEY_C)
}