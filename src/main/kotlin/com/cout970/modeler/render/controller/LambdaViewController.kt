package com.cout970.modeler.render.controller

import com.cout970.modeler.event.EventController

/**
 * Created by cout970 on 2016/12/06.
 */
class LambdaViewController(val func: (EventController) -> Unit) : IViewController {

    override fun registerListeners(eventController: EventController) {
        func(eventController)
    }
}