package com.cout970.modeler.render.layout

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.device.Mouse
import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.event.EventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.event.KeyBindings
import com.cout970.modeler.render.controller.IViewController
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.inside
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toRads
import com.cout970.vector.extensions.*
import org.joml.Math

class ViewControllerModelEdit(val layout: LayoutModelEdit) : IViewController {
    lateinit var mouse: Mouse
    lateinit var keyboard: Keyboard
    lateinit var keyBindings: KeyBindings
    var enableControl = true

    override fun registerListeners(eventController: EventController) {
        mouse = eventController.mouse
        keyboard = eventController.keyboard
        keyBindings = eventController.keyBindings
        eventController.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean {
                if (!enableControl) return false
                if (e.keyState == EnumKeyState.PRESS && e.keycode == Keyboard.KEY_P) {
                    layout.renderManager.modelRenderer.cache.clear()
                }
                return false
            }
        })
        eventController.addListener(EventMouseScroll::class.java, object : IEventListener<EventMouseScroll> {
            override fun onEvent(e: EventMouseScroll): Boolean {
                if (!enableControl) return false
                layout.zoom += e.offsetY.toFloat()
                return true
            }
        })
        eventController.addListener(EventMouseClick::class.java, object : IEventListener<EventMouseClick> {
            override fun onEvent(e: EventMouseClick): Boolean {
                if (!enableControl || e.keyState != EnumKeyState.PRESS || !keyBindings.selectModel.check(mouse)) return false
                if (inside(mouse.getMousePos(), layout.modelPanel.absolutePosition, layout.modelPanel.size.toIVector())) {
                    layout.renderManager.modelController.selectionManager
                            .mouseTrySelect(mouse.getMousePos() - layout.modelPanel.absolutePosition, layout.renderManager.modelRenderer, layout.modelPanel.size.toIVector())
                    return true
                }
                return false
            }
        })
    }

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
            layout.zoom += 0.5f
        }
        if (keyboard.isKeyPressed(Keyboard.KEY_E)) {
            layout.zoom -= 0.5f
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
    }

    fun onButtonPress(id: Int) {
        println("click $id")
    }
}