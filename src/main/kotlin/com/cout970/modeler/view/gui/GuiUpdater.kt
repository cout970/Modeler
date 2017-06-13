package com.cout970.modeler.view.gui

import com.cout970.glutilities.event.EventFrameBufferSize
import com.cout970.modeler.util.size
import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.view.Gui
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of

/**
 * Created by cout970 on 2017/05/14.
 */

class GuiUpdater {

    lateinit var gui: Gui

    fun onFramebufferSizeUpdated(event: EventFrameBufferSize): Boolean {
        if (event.height == 0 || event.width == 0) return false
        updateSizes(vec2Of(event.width, event.height))
        return false
    }

    fun updateSizes(newSize: IVector2) {
        gui.root.apply {
            size = newSize.toJoml2f()
            mainPanel?.updateSizes(newSize)
        }
        gui.canvasContainer.layout.updateCanvas()
    }
}