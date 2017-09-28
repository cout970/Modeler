package com.cout970.modeler.gui

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.controller.binders.ButtonBinder
import com.cout970.modeler.controller.binders.KeyboardBinder
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.canvas.CanvasManager
import com.cout970.modeler.gui.editor.EditorPanel
import com.cout970.modeler.gui.react.leguicomp.Panel
import com.cout970.modeler.input.event.EventController
import com.cout970.modeler.input.window.WindowHandler
import com.cout970.modeler.render.RenderManager

/**
 * Created by cout970 on 2017/04/08.
 */
class GuiInitializer(
        val eventController: EventController,
        val windowHandler: WindowHandler,
        val renderManager: RenderManager,
        val resourceLoader: ResourceLoader,
        val timer: Timer,
        val projectManager: ProjectManager
) {

    fun init(): Gui {
        log(Level.FINE) { "[GuiInitializer] Initializing GUI" }
        log(Level.FINE) { "[GuiInitializer] Creating gui resources" }
        val guiResources = GuiResources()
        log(Level.FINE) { "[GuiInitializer] Creating GuiUpdater" }
        val guiUpdater = GuiUpdater()
        log(Level.FINE) { "[GuiInitializer] Creating Root Frame" }
        val root = Root()
        log(Level.FINE) { "[GuiInitializer] Creating Editor Panel" }
        val editorPanel = EditorPanel()
        root.mainPanel = editorPanel
        log(Level.FINE) { "[GuiInitializer] Creating CanvasContainer" }
        val canvasContainer = CanvasContainer(Panel())
        log(Level.FINE) { "[GuiInitializer] Creating CanvasManager" }
        val canvasManager = CanvasManager()
        log(Level.FINE) { "[GuiInitializer] Creating Listeners" }
        val listeners = Listeners()
        log(Level.FINE) { "[GuiInitializer] Creating GuiState" }
        val guiState = GuiState()
        log(Level.FINE) { "[GuiInitializer] Creating Dispatcher" }
        val dispatcher = Dispatcher()
        log(Level.FINE) { "[GuiInitializer] Creating ButtonBinder" }
        val buttonBinder = ButtonBinder(dispatcher)
        log(Level.FINE) { "[GuiInitializer] Creating ButtonBinder" }
        val keyboardBinder = KeyboardBinder(dispatcher)
        log(Level.FINE) { "[GuiInitializer] Creating initial canvas" }
        canvasContainer.newCanvas()
        log(Level.FINE) { "[GuiInitializer] GUI Initialization done" }

        return Gui(
                root = root,
                guiUpdater = guiUpdater,
                canvasContainer = canvasContainer,
                listeners = listeners,
                windowHandler = windowHandler,
                timer = timer,
                input = eventController,
                editorPanel = editorPanel,
                projectManager = projectManager,
                resources = guiResources,
                state = guiState,
                dispatcher = dispatcher,
                buttonBinder = buttonBinder,
                keyboardBinder = keyboardBinder,
                canvasManager = canvasManager
        )
    }
}