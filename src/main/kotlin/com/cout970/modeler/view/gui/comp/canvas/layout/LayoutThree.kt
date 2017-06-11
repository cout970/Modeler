package com.cout970.modeler.view.gui.comp.canvas.layout

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.core.config.KeyboardModifiers
import com.cout970.modeler.util.next
import com.cout970.modeler.view.GuiState
import com.cout970.modeler.view.gui.comp.canvas.CanvasContainer
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/06/09.
 */
class LayoutThree(override val container: CanvasContainer) : ICanvasLayout {

    var horizontalSplitter = 0.5f
    var verticalSplitter = 0.5f
    var mode: Mode = Mode.LEFT

    override fun updateCanvas() {
        when (mode) {
            Mode.LEFT -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x * horizontalSplitter, container.panel.size.y)
                    position = Vector2f()
                }
                container.canvas[1].apply {
                    size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                            container.panel.size.y * verticalSplitter)
                    position = Vector2f(container.panel.size.x * horizontalSplitter, 0f)
                }
                container.canvas[2].apply {
                    size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                            container.panel.size.y * (1 - verticalSplitter))
                    position = Vector2f(container.panel.size.x * horizontalSplitter,
                            container.panel.size.y * verticalSplitter)
                }
            }
            Mode.RIGHT -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x * horizontalSplitter, container.panel.size.y)
                    position = Vector2f(container.panel.size.x * (1 - horizontalSplitter), 0f)
                }
                container.canvas[1].apply {
                    size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                            container.panel.size.y * verticalSplitter)
                    position = Vector2f()
                }
                container.canvas[2].apply {
                    size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                            container.panel.size.y * (1 - verticalSplitter))
                    position = Vector2f(0f, container.panel.size.y * verticalSplitter)
                }
            }
            Mode.TOP -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x, container.panel.size.y * verticalSplitter)
                    position = Vector2f()
                }
                container.canvas[1].apply {
                    size = Vector2f(container.panel.size.x * horizontalSplitter,
                            container.panel.size.y * (1 - verticalSplitter))
                    position = Vector2f(0f, container.panel.size.y * verticalSplitter)
                }
                container.canvas[2].apply {
                    size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                            container.panel.size.y * (1 - verticalSplitter))
                    position = Vector2f(container.panel.size.x * horizontalSplitter,
                            container.panel.size.y * verticalSplitter)
                }
            }
            Mode.BOTTOM -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x, container.panel.size.y * verticalSplitter)
                    position = Vector2f(0f, container.panel.size.y * (1 - verticalSplitter))
                }
                container.canvas[1].apply {
                    size = Vector2f(container.panel.size.x * horizontalSplitter,
                            container.panel.size.y * (1 - verticalSplitter))
                    position = Vector2f()
                }
                container.canvas[2].apply {
                    size = Vector2f(container.panel.size.x * (1 - horizontalSplitter),
                            container.panel.size.y * (1 - verticalSplitter))
                    position = Vector2f(container.panel.size.x * horizontalSplitter, 0f)
                }
            }
        }
    }

    override fun onEvent(guiState: GuiState, e: EventKeyUpdate): Boolean {
        if (KeyboardModifiers.ALT.check(e)) {
            when (e.keycode) {
                Keyboard.KEY_M -> mode = mode.next()
                Keyboard.KEY_J -> horizontalSplitter -= 1f / 32f
                Keyboard.KEY_K -> horizontalSplitter += 1f / 32f
                Keyboard.KEY_L -> verticalSplitter -= 1f / 32f
                Keyboard.KEY_H -> verticalSplitter += 1f / 32f
                Keyboard.KEY_N -> {
                    container.newCanvas()
                    container.selectLayout()
                }
                Keyboard.KEY_D -> {
                    container.removeCanvas(container.canvas.lastIndex)
                    container.selectLayout()
                }
                else -> return false
            }
            guiState.guiUpdater.updateSizes(guiState.windowHandler.window.size)
            return true
        }
        return false
    }

    enum class Mode {
        LEFT, RIGHT, TOP, BOTTOM
    }
}