package com.cout970.modeler.view.controller

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.event.EventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.modelcontrol.ModelController
import com.cout970.modeler.modelcontrol.action.ActionCreateCube
import com.cout970.modeler.modelcontrol.action.ActionCreatePlane
import com.cout970.modeler.modelcontrol.action.ActionDelete
import com.cout970.modeler.modelcontrol.selection.SelectionMode
import com.cout970.modeler.view.ViewManager

/**
 * Created by cout970 on 2016/12/27.
 */

class ModuleController(val viewManager: ViewManager, val modelController: ModelController) {

    fun registerListeners(eventController: EventController) {
        val keyboard = eventController.keyboard

        eventController.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean {
                if (e.keyState != EnumKeyState.RELEASE) {
                    when (e.keycode) {
                        Keyboard.KEY_DELETE -> modelController.historyRecord
                                .doAction(ActionDelete(modelController.selectionManager.selection, modelController))

                        Keyboard.KEY_Z -> if (keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL)) {
                            modelController.historyRecord.undo()
                        }
                        Keyboard.KEY_Y -> if (keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL)) {
                            modelController.historyRecord.redo()
                        }
                        Keyboard.KEY_C -> if (keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL)) {
                            modelController.clipboard.copy()
                        }
                        Keyboard.KEY_X -> if (keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL)) {
                            modelController.clipboard.cut()
                        }
                        Keyboard.KEY_V -> if (keyboard.isKeyPressed(Keyboard.KEY_LEFT_CONTROL)) {
                            modelController.clipboard.paste()
                        }
                    }
                }
                return false
            }
        })
    }

    fun onButtonPress(id: Int) {
        when (id) {
            0 -> modelController.selectionManager.selectionMode = SelectionMode.GROUP
            1 -> modelController.selectionManager.selectionMode = SelectionMode.COMPONENT
            2 -> modelController.selectionManager.selectionMode = SelectionMode.QUAD
            3 -> modelController.selectionManager.selectionMode = SelectionMode.VERTEX
            4 -> modelController.historyRecord.doAction(ActionCreateCube(modelController))
            5 -> modelController.historyRecord.doAction(ActionCreatePlane(modelController))
            8 -> modelController.historyRecord.undo()
            9 -> modelController.historyRecord.redo()
            10 -> modelController.clipboard.copy()
            11 -> modelController.clipboard.cut()
            12 -> modelController.clipboard.paste()
            else -> println("unregistered button ID: $id")
        }
    }
}