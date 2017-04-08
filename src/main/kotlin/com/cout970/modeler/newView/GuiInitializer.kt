package com.cout970.modeler.newView

import com.cout970.modeler.event.EventController

/**
 * Created by cout970 on 2017/04/08.
 */
class GuiInitializer {

    lateinit var contentPanel: ContentPanel
    lateinit var viewEventHandler: ViewEventHandler

    fun init(eventController: EventController) {
        contentPanel = ContentPanel()
        viewEventHandler = ViewEventHandler(contentPanel, eventController)
        viewEventHandler.registerListeners(eventController)
    }
}