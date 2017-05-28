package com.cout970.modeler.view

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.controller.CommandExecutor
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.view.event.EventController
import com.cout970.modeler.view.gui.GuiUpdater
import com.cout970.modeler.view.gui.canvas.CanvasContainer
import com.cout970.modeler.view.render.control.RenderManager
import com.cout970.modeler.view.window.WindowHandler

/**
 * Created by cout970 on 2017/04/08.
 */
class GuiInitializer(
        val eventController: EventController,
        val windowHandler: WindowHandler,
        val projectManager: ProjectManager,
        val renderManager: RenderManager,
        val resourceLoader: ResourceLoader,
        val timer: Timer
) {

    fun init(): GuiState {
        log(Level.FINE) { "[GuiInitializer] Initializing GUI" }
        log(Level.FINE) { "[GuiInitializer] Creating CommandExecutor" }
        val commandExecutor = CommandExecutor()
        log(Level.FINE) { "[GuiInitializer] Creating GuiUpdater" }
        val guiUpdater = GuiUpdater()
        log(Level.FINE) { "[GuiInitializer] Creating Root Frame" }
        val root = guiUpdater.createRoot(commandExecutor)
        log(Level.FINE) { "[GuiInitializer] Creating CanvasContainer" }
        val canvasContainer = CanvasContainer(root.canvasPanel)

        log(Level.FINE) { "[GuiInitializer] Creating Listeners" }
        val listeners = Listeners()

        log(Level.FINE) { "[GuiInitializer] GUI Initialization done" }

        return GuiState(
                root, guiUpdater, canvasContainer,
                commandExecutor, listeners, windowHandler, timer, eventController
        ).also {
            renderManager.guiState = it
            guiUpdater.guiState = it
        }
    }
}