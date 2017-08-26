package com.cout970.modeler.core.config

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.device.Mouse
import com.cout970.modeler.core.config.KeyboardModifiers.*

/**
 * Created by cout970 on 2016/12/07.
 */
class KeyBindings {

    var rotateCamera = MouseKeyBind(Mouse.BUTTON_RIGHT)
    var moveCamera = MouseKeyBind(Mouse.BUTTON_MIDDLE)
    var selectModel = MouseKeyBind(Mouse.BUTTON_LEFT)
    var selectTexture = MouseKeyBind(Mouse.BUTTON_LEFT)
    var selectModelControls = MouseKeyBind(Mouse.BUTTON_LEFT)
    var selectTextureControls = MouseKeyBind(Mouse.BUTTON_LEFT)
    var jumpCameraToCursor = MouseKeyBind(Mouse.BUTTON_RIGHT)

    var multipleSelection = KeyBind(Keyboard.KEY_LEFT_CONTROL)
    var disableGridMotion = KeyBind(Keyboard.KEY_LEFT_CONTROL)
    var disablePixelGridMotion = KeyBind(Keyboard.KEY_LEFT_SHIFT)
    var switchCameraAxis = KeyBind(Keyboard.KEY_P)
    var switchOrthoProjection = KeyBind(Keyboard.KEY_O)
    var slowCameraMovements = KeyBind(Keyboard.KEY_LEFT_SHIFT)
    var moveCameraToCursor = KeyBind(Keyboard.KEY_F)
    var delete = KeyBind(Keyboard.KEY_DELETE)
    var undo = KeyBind(Keyboard.KEY_Z, CTRL)
    var redo = KeyBind(Keyboard.KEY_Y, CTRL)
    var cut = KeyBind(Keyboard.KEY_X, CTRL)
    var copy = KeyBind(Keyboard.KEY_C, CTRL)
    var paste = KeyBind(Keyboard.KEY_V, CTRL)
    var showTopMenu = KeyBind(Keyboard.KEY_LEFT_ALT)
    var showSearchBar = KeyBind(Keyboard.KEY_SPACE, CTRL)
    var addCube = KeyBind(Keyboard.KEY_C, CTRL, ALT)
    var addPlane = KeyBind(Keyboard.KEY_P, CTRL, ALT)
    var setElementSelectionMode = KeyBind(Keyboard.KEY_1)
    var setQuadSelectionMode = KeyBind(Keyboard.KEY_2)
    var setEdgeSelectionMode = KeyBind(Keyboard.KEY_3)
    var setVertexSelectionMode = KeyBind(Keyboard.KEY_4)
    var setTranslationCursorMode = KeyBind(Keyboard.KEY_T)
    var setRotationCursorMode = KeyBind(Keyboard.KEY_R)
    var setScaleCursorMode = KeyBind(Keyboard.KEY_S)
    var importTexture = KeyBind(Keyboard.KEY_T, CTRL, ALT)
    var exportTexture = KeyBind(Keyboard.KEY_T, CTRL, ALT, SHIFT)
    var setTextureMode = KeyBind(Keyboard.KEY_T, CTRL)
    var setModelMode = KeyBind(Keyboard.KEY_M, CTRL)
    var toggleVisibility = KeyBind(Keyboard.KEY_V, SHIFT)

    var newProject = KeyBind(Keyboard.KEY_N, CTRL, ALT, SHIFT)
    var openProject = KeyBind(Keyboard.KEY_O, CTRL, ALT, SHIFT)
    var saveProject = KeyBind(Keyboard.KEY_S, CTRL)
    var saveProjectAs = KeyBind(Keyboard.KEY_S, CTRL, SHIFT)
    var importModel = KeyBind(Keyboard.KEY_I, CTRL, SHIFT)
    var exportModel = KeyBind(Keyboard.KEY_E, CTRL, SHIFT)
}