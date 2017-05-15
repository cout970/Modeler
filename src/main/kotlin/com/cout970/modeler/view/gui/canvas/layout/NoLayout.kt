package com.cout970.modeler.view.gui.canvas.layout

import com.cout970.modeler.view.gui.canvas.CanvasContainer
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/05/14.
 */
class NoLayout(override val container: CanvasContainer) : ICanvasLayout {

    override fun updateCanvas() {
        container.canvas.firstOrNull()?.apply {
            size = Vector2f(container.panel.size)
            position = Vector2f()
        }
    }
}