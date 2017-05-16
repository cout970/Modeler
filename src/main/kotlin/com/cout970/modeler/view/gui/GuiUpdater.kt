package com.cout970.modeler.view.gui

import com.cout970.modeler.util.*
import com.cout970.modeler.view.GuiInitializer
import com.cout970.modeler.view.gui.canvas.CanvasContainer
import com.cout970.modeler.view.gui.search.SearchFacade
import com.cout970.modeler.view.gui.search.SearchPanel
import com.cout970.modeler.view.newView.gui.comp.CPanel
import org.joml.Vector2f
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.util.ColorConstants

/**
 * Created by cout970 on 2017/05/14.
 */

class GuiUpdater(private val initializer: GuiInitializer) : ITickeable {

    val root: Root = Root()
    val canvasContainer: CanvasContainer

    init {
        root.apply {
            searchPanel = SearchPanel(SearchFacade(initializer.commandExecutor))
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
        canvasContainer = CanvasContainer(root.canvasPanel)
    }

    fun updateComponents() {
        val window = initializer.windowHandler.window
        root.apply {
            size = window.getFrameBufferSize().toJoml2f()
            canvasPanel.size = Vector2f(size)
            if (canvasContainer.canvas.isEmpty()) {
                backgroundLabels.forEachIndexed { index, label ->
                    label.show()
                    label.setPosition(size.x / 3f, (index - backgroundLabels.size / 2) * 45f)
                    label.setSize(size.x - label.position.x, size.y - label.position.y)
                }
            } else {
                backgroundLabels.forEach { it.hide() }
            }
        }
        canvasContainer.layout.updateCanvas()
    }

    override fun tick() {
        updateComponents()


//        initializer.guiResources.updateMaterials(initializer.modelEditor.model)
        initializer.windowHandler.resetViewport()

//        initializer.cameraUpdater.updateCameras()
//        rescale()
//        contentPanel.sceneHandler.scaleScenes()
//        initializer.eventListeners.update()
    }
}