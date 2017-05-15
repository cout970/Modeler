package com.cout970.modeler.view.gui.canvas.layout

import com.cout970.modeler.view.gui.canvas.CanvasContainer
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/05/15.
 */
class ColumnLayout(override val container: CanvasContainer) : ICanvasLayout {

    override fun updateCanvas() {
        when (container.canvas.size) {
            1 -> container.canvas.firstOrNull()?.apply {
                size = Vector2f(container.panel.size)
                position = Vector2f()
            }
            2 -> {
                container.canvas[0].apply {
                    size = Vector2f(container.panel.size.x / 2, container.panel.size.y)
                    position = Vector2f()
                }
                container.canvas[1].apply {
                    size = Vector2f(container.panel.size.x / 2, container.panel.size.y)
                    position = Vector2f(container.panel.size.x / 2, 0f)
                }
            }
        }
    }
}