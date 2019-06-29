package com.cout970.modeler.gui.canvas.layout

import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.util.next
import org.joml.Vector2f
import org.liquidengine.legui.component.optional.Orientation

/**
 * Created by cout970 on 2017/06/09.
 */
class LayoutTwo(override val container: CanvasContainer) : ICanvasLayout {

    var splitter = 0.5f
    var orientation: Orientation = Orientation.HORIZONTAL

    override fun updateCanvas() {
        when (orientation) {
            Orientation.VERTICAL -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x, container.panel.size.y * splitter)
                    position = Vector2f()
                }
                container.canvas[1].apply {
                    size = Vector2f(container.panel.size.x, container.panel.size.y * (1 - splitter))
                    position = Vector2f(0f, container.panel.size.y * splitter)
                }
            }
            Orientation.HORIZONTAL -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x * splitter, container.panel.size.y)
                    position = Vector2f()
                }
                container.canvas[1].apply {
                    size = Vector2f(container.panel.size.x * (1 - splitter), container.panel.size.y)
                    position = Vector2f(container.panel.size.x * splitter, 0f)
                }
            }
        }
    }

    override fun onEvent(gui: Gui, e: EventKeyUpdate): Boolean {

        Config.keyBindings.apply {
            when {
                layoutChangeMode.check(e) -> runAction("layout.change.mode")
                moveLayoutSplitterLeft.check(e) -> runAction("move.splitter.left")
                moveLayoutSplitterRight.check(e) -> runAction("move.splitter.right")
                newCanvas.check(e) -> runAction("canvas.new")
                deleteCanvas.check(e) -> runAction("canvas.delete")
                else -> return false
            }
        }
        gui.root.reRender()
        return true
    }

    override fun runAction(action: String) {
        when (action) {
            "layout.change.mode" -> orientation = orientation.next()
            "move.splitter.left" -> if (orientation == Orientation.HORIZONTAL) splitter -= 1f / 32f
            "move.splitter.right" -> if (orientation == Orientation.HORIZONTAL) splitter += 1f / 32f
            "move.splitter.up" -> if (orientation == Orientation.VERTICAL) splitter -= 1f / 32f
            "move.splitter.down" -> if (orientation == Orientation.VERTICAL) splitter += 1f / 32f
            "canvas.new" -> {
                container.newCanvas()
                container.selectLayout()
            }
            "canvas.delete" -> {
                container.removeCanvas(container.canvas.lastIndex)
                container.selectLayout()
            }
        }
    }
}