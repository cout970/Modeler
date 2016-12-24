package com.cout970.modeler.render.controller

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.device.Mouse
import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.event.EventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.event.KeyBindings
import com.cout970.modeler.modelcontrol.ModelController
import com.cout970.modeler.modelcontrol.action.ActionCreateCube
import com.cout970.modeler.modelcontrol.action.ActionCreatePlane
import com.cout970.modeler.modelcontrol.action.ActionDelete
import com.cout970.modeler.modelcontrol.selection.SelectionMode
import com.cout970.modeler.render.layout.LayoutModelEdit
import com.cout970.modeler.util.toRads
import com.cout970.vector.extensions.*

class ViewControllerModelEdit(val layout: LayoutModelEdit) : IViewController {

    val modelController: ModelController get() = layout.renderManager.modelController
    lateinit var mouse: Mouse
    lateinit var keyboard: Keyboard
    lateinit var keyBindings: KeyBindings
    var enableControl = true

    val modelSelector = ModelSelector(this, layout, layout.renderManager)

    fun update() {
        if (!enableControl) return

        //keyborad
        if (keyboard.isKeyPressed(Keyboard.KEY_DOWN)) {
            layout.camera = layout.camera.run { copy(angleX = angleX + 0.05) }
        }
        if (keyboard.isKeyPressed(Keyboard.KEY_UP)) {
            layout.camera = layout.camera.run { copy(angleX = angleX - 0.05) }
        }
        if (keyboard.isKeyPressed(Keyboard.KEY_LEFT)) {
            layout.camera = layout.camera.run { copy(angleY = angleY + 0.05) }
        }
        if (keyboard.isKeyPressed(Keyboard.KEY_RIGHT)) {
            layout.camera = layout.camera.run { copy(angleY = angleY - 0.05) }
        }
        if (keyboard.isKeyPressed(Keyboard.KEY_Q)) {
            layout.camera = layout.camera.copy(zoom = layout.camera.zoom + 0.5f)
        }
        if (keyboard.isKeyPressed(Keyboard.KEY_E)) {
            layout.camera = layout.camera.copy(zoom = layout.camera.zoom - 0.5f)
        }

        //mouse
        mouse.update()
        if (keyBindings.moveCamera.check(mouse)) {
            val rotations = vec2Of(layout.camera.angleX, layout.camera.angleY).toDegrees()
            val axisX = vec2Of(Math.cos(rotations.x.toRads()), Math.sin(rotations.x.toRads()))
            var axisY = vec2Of(Math.cos((rotations.xd - 90).toRads()), Math.sin((rotations.xd - 90).toRads()))
            axisY *= Math.sin(rotations.y.toRads())
            var a = vec3Of(axisX.x, 0.0, axisX.y)
            var b = vec3Of(axisY.x, Math.cos(rotations.y.toRads()), axisY.y)

            a = a.normalize() * (mouse.getMousePosDiff().xd * layout.renderManager.timer.delta * 2.0)
            b = b.normalize() * (-mouse.getMousePosDiff().yd * layout.renderManager.timer.delta * 2.0)

            layout.camera = layout.camera.run { copy(position = position + a + b) }
        } else if (keyBindings.rotateCamera.check(mouse)) {
            layout.camera = layout.camera.run { copy(angleX = angleX + mouse.getMousePosDiff().xd * layout.renderManager.timer.delta * 0.5) }
            layout.camera = layout.camera.run { copy(angleY = angleY + mouse.getMousePosDiff().yd * layout.renderManager.timer.delta * 0.5) }
        }
        modelSelector.update()
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

    override fun registerListeners(eventController: EventController) {
        mouse = eventController.mouse
        keyboard = eventController.keyboard
        keyBindings = eventController.keyBindings
        eventController.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean {
                if (!enableControl) return false
                if (e.keyState != EnumKeyState.RELEASE) {
                    when (e.keycode) {
                        Keyboard.KEY_P -> {
                            layout.renderManager.modelRenderer.modelCache.clear()
                            layout.renderManager.modelRenderer.selectionCache.clear()
                        }
                        Keyboard.KEY_DELETE -> modelController.historyRecord.doAction(ActionDelete(modelController.selectionManager.selection, modelController))
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
        eventController.addListener(EventMouseScroll::class.java, object : IEventListener<EventMouseScroll> {
            override fun onEvent(e: EventMouseScroll): Boolean {
                if (!enableControl) return false
                layout.camera = layout.camera.copy(zoom = layout.camera.zoom - e.offsetY)
                return true
            }
        })
        modelSelector.registerListeners(eventController)
    }
}