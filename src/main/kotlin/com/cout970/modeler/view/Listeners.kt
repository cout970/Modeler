package com.cout970.modeler.view

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.view.event.EventController
import com.cout970.modeler.view.event.IEventListener
import com.cout970.modeler.view.gui.GuiUpdater

/**
 * Created by cout970 on 2017/05/16.
 */
class Listeners(eventController: EventController, val guiUpdater: GuiUpdater) {

    init {
        eventController.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean {
                if (e.keyState == EnumKeyState.PRESS) {
                    return guiUpdater.canvasContainer.layout.onEvent(e)
                }
                return false
            }
        })
    }
}