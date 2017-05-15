package com.cout970.modeler.view.gui

import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.util.size
import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.view.GuiInitializer
import com.cout970.modeler.view.gui.canvas.CanvasContainer
import com.cout970.modeler.view.gui.search.SearchFacade
import com.cout970.modeler.view.gui.search.SearchPanel
import com.cout970.modeler.view.newView.gui.comp.CPanel
import org.joml.Vector2f
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
            canvasPanel = CPanel().also {
                it.backgroundColor = ColorConstants.transparent()
            }
            refreshComponents()
        }
        canvasContainer = CanvasContainer(root.canvasPanel).also {
            it.newCanvas()
            it.newCanvas()
        }
    }

    override fun tick() {
        val window = initializer.windowHandler.window
        root.size = window.getFrameBufferSize().toJoml2f()
        root.canvasPanel.size = Vector2f(root.size)

        canvasContainer.layout.updateCanvas()

//        initializer.guiResources.updateMaterials(initializer.modelEditor.model)
        initializer.windowHandler.resetViewport()

//        initializer.cameraUpdater.updateCameras()
//        rescale()
//        contentPanel.sceneHandler.scaleScenes()
//        initializer.eventListeners.update()
    }
}