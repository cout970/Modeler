package com.cout970.modeler.view.gui

import com.cout970.glutilities.event.EventFrameBufferSize
import com.cout970.modeler.controller.CommandExecutor
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.show
import com.cout970.modeler.util.size
import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.view.GuiState
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.gui.search.SearchFacade
import com.cout970.modeler.view.gui.search.SearchPanel
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector2f
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.util.ColorConstants

/**
 * Created by cout970 on 2017/05/14.
 */

class GuiUpdater {

    lateinit var guiState: GuiState

    fun createRoot(commandExecutor: CommandExecutor): Root {
        return Root().apply {
            searchPanel = SearchPanel(SearchFacade(commandExecutor))
            canvasPanel = CPanel().also { it.backgroundColor = ColorConstants.transparent() }
            backgroundLabels += Label("Open new view:  Alt + N", 0f, 0f, 10f, 10f)
            backgroundLabels += Label("Close view:         Alt + D", 0f, 0f, 10f, 10f)
            backgroundLabels += Label("Resize view:       Alt + J/K", 0f, 0f, 10f, 10f)
            backgroundLabels.forEach {
                it.textState.apply {
                    textColor = ColorConstants.white()
                    fontSize = 20f
                }
            }
            refreshComponents()
        }
    }

    fun onFramebufferSizeUpdated(event: EventFrameBufferSize): Boolean {
        if (event.height == 0 || event.width == 0) return false
        updateSizes(vec2Of(event.width, event.height))
        return false
    }

    fun updateSizes(newSize: IVector2) {
        guiState.root.apply {
            size = newSize.toJoml2f()
            canvasPanel.size = Vector2f(size)
            if (guiState.canvasContainer.canvas.isEmpty()) {
                backgroundLabels.forEachIndexed { index, label ->
                    label.show()
                    label.setPosition(size.x / 3f, (index - backgroundLabels.size / 2) * 45f)
                    label.setSize(size.x - label.position.x, size.y - label.position.y)
                }
            } else {
                backgroundLabels.forEach { it.hide() }
            }
        }
        guiState.canvasContainer.layout.updateCanvas()
    }
}