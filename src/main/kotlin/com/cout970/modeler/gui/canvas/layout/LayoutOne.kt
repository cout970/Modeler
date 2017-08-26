package com.cout970.modeler.gui.canvas.layout

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.core.config.KeyboardModifiers
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.CanvasContainer
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/05/14.
 */
class LayoutOne(override val container: CanvasContainer) : ICanvasLayout {

    override fun updateCanvas() {
        container.canvas.firstOrNull()?.apply {
            size = Vector2f(container.panel.size)
            position = Vector2f()
        }
    }

    override fun onEvent(gui: Gui, e: EventKeyUpdate): Boolean {
        if (KeyboardModifiers.ALT.check(e)) {
            when (e.keycode) {
                Keyboard.KEY_N -> {
                    container.newCanvas()
                    container.selectLayout()
                }
                Keyboard.KEY_D -> {
                    if (container.canvas.isNotEmpty()) {
                        container.removeCanvas(container.canvas.lastIndex)
                        container.selectLayout()
                    }
                }
                else -> return false
            }
            gui.root.updateSizes(gui.windowHandler.window.size)
            return true
        }
        return false
    }
}