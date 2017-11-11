package com.cout970.modeler.gui.canvas.layout

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.core.config.KeyboardModifiers
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.CanvasContainer
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/05/15.
 */
class ColumnLayout(override val container: CanvasContainer) : ICanvasLayout {

    var splitter = 0.75f

    override fun updateCanvas() {
        when (container.canvas.size) {
            1 -> container.canvas.firstOrNull()?.apply {
                size = Vector2f(container.panel.size)
                position = Vector2f()
            }
            2 -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x * splitter, container.panel.size.y)
                    position = Vector2f()
                }
                container.canvas[1].apply {
                    size = Vector2f(container.panel.size.x * (1 - splitter), container.panel.size.y)
                    position = Vector2f(container.panel.size.x * splitter, 0f)
                }
            }
            3, 4, 5, 6, 7, 8, 9, 10 -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x * splitter, container.panel.size.y)
                    position = Vector2f()
                }
                val count = container.canvas.size - 1
                repeat(count) { i ->
                    container.canvas[i + 1].apply {
                        size = Vector2f(container.panel.size.x * (1 - splitter), container.panel.size.y / count)
                        position = Vector2f(container.panel.size.x * splitter, (container.panel.size.y / count) * i)
                    }
                }
            }
        }
    }

    override fun onEvent(gui: Gui, e: EventKeyUpdate): Boolean {
        if (KeyboardModifiers.ALT.check(e)) {
            when (e.keycode) {
                Keyboard.KEY_J -> splitter -= 0.03125f
                Keyboard.KEY_K -> splitter += 0.03125f
                Keyboard.KEY_N -> container.newCanvas()
                Keyboard.KEY_D -> if (container.canvas.isNotEmpty()) {
                    container.removeCanvas(container.canvas.lastIndex)
                }
                else -> return false
            }
            gui.root.reRender()
        }
        return true
    }
}