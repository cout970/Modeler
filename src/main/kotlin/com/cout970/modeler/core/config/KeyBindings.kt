package com.cout970.modeler.core.config

import com.cout970.glutilities.device.Keyboard.Companion.KEY_1
import com.cout970.glutilities.device.Keyboard.Companion.KEY_2
import com.cout970.glutilities.device.Keyboard.Companion.KEY_3
import com.cout970.glutilities.device.Keyboard.Companion.KEY_4
import com.cout970.glutilities.device.Keyboard.Companion.KEY_A
import com.cout970.glutilities.device.Keyboard.Companion.KEY_B
import com.cout970.glutilities.device.Keyboard.Companion.KEY_C
import com.cout970.glutilities.device.Keyboard.Companion.KEY_D
import com.cout970.glutilities.device.Keyboard.Companion.KEY_DELETE
import com.cout970.glutilities.device.Keyboard.Companion.KEY_E
import com.cout970.glutilities.device.Keyboard.Companion.KEY_F
import com.cout970.glutilities.device.Keyboard.Companion.KEY_H
import com.cout970.glutilities.device.Keyboard.Companion.KEY_I
import com.cout970.glutilities.device.Keyboard.Companion.KEY_J
import com.cout970.glutilities.device.Keyboard.Companion.KEY_K
import com.cout970.glutilities.device.Keyboard.Companion.KEY_L
import com.cout970.glutilities.device.Keyboard.Companion.KEY_LEFT_CONTROL
import com.cout970.glutilities.device.Keyboard.Companion.KEY_LEFT_SHIFT
import com.cout970.glutilities.device.Keyboard.Companion.KEY_M
import com.cout970.glutilities.device.Keyboard.Companion.KEY_N
import com.cout970.glutilities.device.Keyboard.Companion.KEY_O
import com.cout970.glutilities.device.Keyboard.Companion.KEY_P
import com.cout970.glutilities.device.Keyboard.Companion.KEY_PAGE_DOWN
import com.cout970.glutilities.device.Keyboard.Companion.KEY_PAGE_UP
import com.cout970.glutilities.device.Keyboard.Companion.KEY_R
import com.cout970.glutilities.device.Keyboard.Companion.KEY_S
import com.cout970.glutilities.device.Keyboard.Companion.KEY_SPACE
import com.cout970.glutilities.device.Keyboard.Companion.KEY_T
import com.cout970.glutilities.device.Keyboard.Companion.KEY_TAB
import com.cout970.glutilities.device.Keyboard.Companion.KEY_U
import com.cout970.glutilities.device.Keyboard.Companion.KEY_V
import com.cout970.glutilities.device.Keyboard.Companion.KEY_X
import com.cout970.glutilities.device.Keyboard.Companion.KEY_Y
import com.cout970.glutilities.device.Keyboard.Companion.KEY_Z
import com.cout970.glutilities.device.Mouse.Companion.BUTTON_LEFT
import com.cout970.glutilities.device.Mouse.Companion.BUTTON_MIDDLE
import com.cout970.glutilities.device.Mouse.Companion.BUTTON_RIGHT
import com.cout970.modeler.core.config.KeyboardModifiers.*

/**
 * Created by cout970 on 2016/12/07.
 */
class KeyBindings {

    var rotateCamera = MouseKeyBind(BUTTON_RIGHT)
    var moveCamera = MouseKeyBind(BUTTON_MIDDLE)
    var selectModel = MouseKeyBind(BUTTON_LEFT)
    var jumpCameraToCursor = MouseKeyBind(BUTTON_RIGHT)

    var multipleSelection = KeyBind(KEY_LEFT_CONTROL)
    var disableGridMotion = KeyBind(KEY_LEFT_CONTROL)
    var disablePixelGridMotion = KeyBind(KEY_LEFT_SHIFT)
    var switchOrthoProjection = KeyBind(KEY_O)
    var slowCameraMovements = KeyBind(KEY_LEFT_SHIFT)
    var moveCameraToCursor = KeyBind(KEY_F)
    var delete = KeyBind(KEY_DELETE)
    var undo = KeyBind(KEY_Z, CTRL)
    var redo = KeyBind(KEY_Y, CTRL)
    var cut = KeyBind(KEY_X, CTRL)
    var copy = KeyBind(KEY_C, CTRL)
    var paste = KeyBind(KEY_V, CTRL)
    var addCube = KeyBind(KEY_C, CTRL, ALT)
    var addPlane = KeyBind(KEY_P, CTRL, ALT)

    var setObjectSelectionType = KeyBind(KEY_1)
    var setFaceSelectionType = KeyBind(KEY_2)
    var setEdgeSelectionType = KeyBind(KEY_3)
    var setVertexSelectionType = KeyBind(KEY_4)

    var setTranslationCursorMode = KeyBind(KEY_T)
    var setRotationCursorMode = KeyBind(KEY_R)
    var setScaleCursorMode = KeyBind(KEY_S)

    var importTexture = KeyBind(KEY_T, CTRL, ALT)
    var exportTexture = KeyBind(KEY_T, CTRL, ALT, SHIFT)
    var setTextureMode = KeyBind(KEY_T, CTRL)
    var setModelMode = KeyBind(KEY_M, CTRL)
    var toggleVisibility = KeyBind(KEY_V, SHIFT)

    var showLeftPanel = KeyBind(KEY_E, ALT)
    var showRightPanel = KeyBind(KEY_R, ALT)
    var showBottomPanel = KeyBind(KEY_B, ALT)
    var showSearchBar = KeyBind(KEY_TAB)

    var selectAll = KeyBind(KEY_A, CTRL)
    var splitTexture = KeyBind(KEY_P, CTRL)
    var scaleTextureUp = KeyBind(KEY_PAGE_UP, CTRL)
    var scaleTextureDown = KeyBind(KEY_PAGE_DOWN, CTRL)

    var joinObjects = KeyBind(KEY_J, CTRL)
    var arrangeUvs = KeyBind(KEY_L, CTRL)
    var extrudeFace = KeyBind(KEY_E, CTRL)
    var setIsometricView = KeyBind(KEY_I, ALT)

    var addAnimation = KeyBind(KEY_U, CTRL)
    var toggleAnimation = KeyBind(KEY_SPACE)

    var layoutChangeMode = KeyBind(KEY_M, ALT)
    var moveLayoutSplitterLeft = KeyBind(KEY_J, ALT)
    var moveLayoutSplitterRight = KeyBind(KEY_K, ALT)
    var moveLayoutSplitterUp = KeyBind(KEY_H, ALT)
    var moveLayoutSplitterDown = KeyBind(KEY_L, ALT)
    var newCanvas = KeyBind(KEY_N, ALT)
    var deleteCanvas = KeyBind(KEY_D, ALT)

    var newProject = KeyBind(KEY_N, CTRL, ALT, SHIFT)
    var openProject = KeyBind(KEY_O, CTRL, ALT, SHIFT)
    var saveProject = KeyBind(KEY_S, CTRL)
    var saveProjectAs = KeyBind(KEY_S, CTRL, SHIFT)
    var importModel = KeyBind(KEY_I, CTRL, SHIFT)
    var exportModel = KeyBind(KEY_E, CTRL, SHIFT)
}