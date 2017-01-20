package com.cout970.modeler.event

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.device.Mouse

/**
 * Created by cout970 on 2017/01/20.
 */
interface IInput {

    val mouse: Mouse
    val keyboard: Keyboard
}