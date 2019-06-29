package com.cout970.modeler.gui.canvas.layout

import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.util.next
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/06/09.
 */
class LayoutFourth(override val container: CanvasContainer) : ICanvasLayout {

    var horizontalSplitter = 0.5f
    var verticalSplitter = 0.5f

    var mode = Mode.NORMAL

    override fun updateCanvas() {
        val max = 4
        var index = mode.ordinal

        container.canvas[(index++) % max].apply {
            size = Vector2f(container.panel.size.x * horizontalSplitter,
                container.panel.size.y * (1 - verticalSplitter))
            position = Vector2f(0f, container.panel.size.y * verticalSplitter)
        }
        container.canvas[(index++) % max].apply {
            size = Vector2f(container.panel.size.x * horizontalSplitter, container.panel.size.y * verticalSplitter)
            position = Vector2f()
        }
        container.canvas[(index++) % max].apply {
            size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                container.panel.size.y * verticalSplitter)
            position = Vector2f(container.panel.size.x * horizontalSplitter, 0f)
        }
        container.canvas[(index) % max].apply {
            size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                container.panel.size.y * (1 - verticalSplitter))
            position = Vector2f(container.panel.size.x * horizontalSplitter,
                container.panel.size.y * verticalSplitter)
        }
    }

    override fun onEvent(gui: Gui, e: EventKeyUpdate): Boolean {
        Config.keyBindings.apply {
            when {
                layoutChangeMode.check(e) -> runAction("layout.change.mode")
                moveLayoutSplitterLeft.check(e) -> runAction("move.splitter.left")
                moveLayoutSplitterRight.check(e) -> runAction("move.splitter.right")
                moveLayoutSplitterUp.check(e) -> runAction("move.splitter.up")
                moveLayoutSplitterDown.check(e) -> runAction("move.splitter.down")
                deleteCanvas.check(e) -> runAction("canvas.delete")
                else -> return false
            }
        }
        gui.root.reRender()
        return true
    }

    override fun runAction(action: String) {
        when (action) {
            "layout.change.mode" -> mode = mode.next()
            "move.splitter.left" -> horizontalSplitter -= 1f / 32f
            "move.splitter.right" -> horizontalSplitter += 1f / 32f
            "move.splitter.up" -> verticalSplitter -= 1f / 32f
            "move.splitter.down" -> verticalSplitter += 1f / 32f
            "canvas.delete" -> {
                container.removeCanvas(container.canvas.lastIndex)
                container.selectLayout()
            }
        }
    }

    enum class Mode {
        NORMAL, ROT90, ROT180, ROT270
    }
}