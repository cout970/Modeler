package com.cout970.modeler.view

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.controller.GuiState
import com.cout970.modeler.controller.selector.Selector
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.functional.Dispatcher
import com.cout970.modeler.functional.binders.ButtonBinder
import com.cout970.modeler.functional.binders.KeyboardBinder
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
        val canvasContainer = CanvasContainer(editorPanel.centerPanelModule.panel.canvasPanel)
        log(Level.FINE) { "[GuiInitializer] Creating Selector" }
        val selector = Selector()
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
                selector = selector,
                resources = guiResources,
                state = guiState,
                dispatcher = dispatcher,
                buttonBinder = buttonBinder,
                keyboardBinder = keyboardBinder
        )
    }
}