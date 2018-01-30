package com.cout970.modeler.gui.canvas.layout

import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.core.config.Config
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
        Config.keyBindings.apply {
            when {
                newCanvas.check(e) -> {
                    container.newCanvas()
                    container.selectLayout()
                }
                deleteCanvas.check(e) -> {
                    if (container.canvas.isNotEmpty()) {
                        container.removeCanvas(container.canvas.lastIndex)
                        container.selectLayout()
                    }
                }
                else -> return false
            }
        }
        gui.root.reRender()
        return true
    }
}