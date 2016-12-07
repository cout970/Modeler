package com.cout970.modeler.render.controller

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.device.Mouse
import com.cout970.modeler.event.EventController

/**
 * Created by cout970 on 2016/12/06.
 */
class LambdaViewController(val func: (EventController) -> Unit) : IViewController {

    lateinit var mouse: Mouse
    lateinit var keyboard: Keyboard

    override fun registerListeners(eventController: EventController) {
        func(eventController)
        mouse = eventController.mouse
        keyboard = eventController.keyboard
    }
}