package com.cout970.modeler.newView

import com.cout970.glutilities.device.Mouse
import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.event.IEventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.event.IInput
import com.cout970.modeler.newView.gui.ContentPanel
import com.cout970.vector.api.IVector2

/**
 * Created by cout970 on 2017/04/08.
 */

class ViewEventHandler(val contentPanel: ContentPanel, val input: IInput) {

    private var lastMousePos: IVector2? = null
    private var mousePress = false

    fun update() {
        if (!input.mouse.isButtonPressed(Mouse.BUTTON_LEFT)) {
            mousePress = false
            lastMousePos = null
        } else if (lastMousePos != null) {
            onDrag(EventMouseDrag(lastMousePos!!, input.mouse.getMousePos()))
        }
    }

    fun registerListeners(eventHandler: IEventController) {
        eventHandler.addListener(EventMouseScroll::class.java, object : IEventListener<EventMouseScroll> {
            override fun onEvent(e: EventMouseScroll): Boolean = onScroll(e)
        })
        eventHandler.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean = onKey(e)
        })
        eventHandler.addListener(EventMouseClick::class.java, object : IEventListener<EventMouseClick> {
            override fun onEvent(e: EventMouseClick): Boolean = onClick(e)
        })
    }

    private fun onScroll(e: EventMouseScroll): Boolean {
        return false
    }

    private fun onKey(e: EventKeyUpdate): Boolean {
        return false
    }

    private fun onClick(e: EventMouseClick): Boolean {
        if (e.keyState == EnumKeyState.PRESS) {
            mousePress = true
            lastMousePos = input.mouse.getMousePos()
        } else if (e.keyState == EnumKeyState.RELEASE) {
            mousePress = false
            lastMousePos = null
        }
        return false
    }

    private fun onDrag(e: EventMouseDrag) {

    }
}