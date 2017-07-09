package com.cout970.modeler.view

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.controller.ActionExecutor
import com.cout970.modeler.controller.CommandExecutor
import com.cout970.modeler.controller.GuiState
import com.cout970.modeler.controller.selector.Selector
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.view.event.EventController
import com.cout970.modeler.view.gui.GuiUpdater
import com.cout970.modeler.view.gui.Root
import com.cout970.modeler.view.gui.comp.canvas.CanvasContainer
import com.cout970.modeler.view.gui.editor.EditorPanel
import com.cout970.modeler.view.render.RenderManager
import com.cout970.modeler.view.window.WindowHandler

/**
 * Created by cout970 on 2017/04/08.
 */
class GuiInitializer(
        val eventController: EventController,
        val windowHandler: WindowHandler,
        val renderManager: RenderManager,
        val resourceLoader: ResourceLoader,
        val timer: Timer,
        val projectManager: ProjectManager,
        val actionExecutor: ActionExecutor
) {

    fun init(): Gui {
        log(Level.FINE) { "[GuiInitializer] Initializing GUI" }
        log(Level.FINE) { "[GuiInitializer] Creating gui resources" }
        val guiResources = GuiResources()
        log(Level.FINE) { "[GuiInitializer] Creating CommandExecutor" }
        val commandExecutor = CommandExecutor()
        log(Level.FINE) { "[GuiInitializer] Creating GuiUpdater" }
        val guiUpdater = GuiUpdater()
        log(Level.FINE) { "[GuiInitializer] Creating Root Frame" }
        val root = Root()
        log(Level.FINE) { "[GuiInitializer] Creating Editor Panel" }
        val editorPanel = EditorPanel()
        root.mainPanel = editorPanel
        log(Level.FINE) { "[GuiInitializer] Creating CanvasContainer" }
        val canvasContainer = CanvasContainer(editorPanel.centerPanel.canvasPanel)
        editorPanel.centerPanel.canvasContainer = canvasContainer
        log(Level.FINE) { "[GuiInitializer] Creating Selector" }
        val selector = Selector()
        log(Level.FINE) { "[GuiInitializer] Creating Listeners" }
        val listeners = Listeners()
        log(Level.FINE) { "[GuiInitializer] Binding buttons" }
        commandExecutor.bindButtons(editorPanel)
        log(Level.FINE) { "[GuiInitializer] Binding text inputs" }
        guiUpdater.bindTextInputs(editorPanel)
        log(Level.FINE) { "[GuiInitializer] Vreating GuiState" }
        val guiState = GuiState()

        log(Level.FINE) { "[GuiInitializer] Creating initial canvas" }
        canvasContainer.newCanvas()
        log(Level.FINE) { "[GuiInitializer] GUI Initialization done" }

        return Gui(
                root = root,
                guiUpdater = guiUpdater,
                canvasContainer = canvasContainer,
                commandExecutor = commandExecutor,
                listeners = listeners,
                windowHandler = windowHandler,
                timer = timer,
                input = eventController,
                editorPanel = editorPanel,
                projectManager = projectManager,
                selector = selector,
                actionExecutor = actionExecutor,
                resources = guiResources,
                state = guiState
        ).also {
            renderManager.gui = it
        }
    }
}