package com.cout970.modeler

import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWLoader
import com.cout970.modeler.controller.AutoRunner
import com.cout970.modeler.controller.FutureExecutor
import com.cout970.modeler.controller.TaskHistory
import com.cout970.modeler.core.config.ConfigManager
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.Logger
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.gui.GuiInitializer
import com.cout970.modeler.input.event.EventController
import com.cout970.modeler.input.window.Loop
import com.cout970.modeler.input.window.WindowHandler
import com.cout970.modeler.render.RenderManager
import java.io.File

/**
 * Created by cout970 on 2016/11/29.
 */
class Initializer {

    fun init(programArguments: List<String>): Program {

        log(Level.FINE) { "Loading config" }
        ConfigManager.loadConfig()
        log(Level.FINE) { "Config loaded" }
        log(Level.NORMAL) { "Log level: ${Logger.level}" }

        log(Level.FINE) { "Creating ResourceLoader" }
        val resourceLoader = ResourceLoader()
        log(Level.FINE) { "Creating Timer" }
        val timer = Timer()
        log(Level.FINE) { "Creating WindowHandler" }
        val windowHandler = WindowHandler(timer)
        log(Level.FINE) { "Creating EventController" }
        val eventController = EventController()
        log(Level.FINE) { "Creating ProjectController" }
        val projectManager = ProjectManager()
        log(Level.FINE) { "Creating FutureExecutor" }
        val futureExecutor = FutureExecutor()
        log(Level.FINE) { "Creating TaskHistory" }
        val taskHistory = TaskHistory(futureExecutor)
        log(Level.FINE) { "Creating ExportManager" }
        val exportManager = ExportManager(resourceLoader)
        log(Level.FINE) { "Creating RenderManager" }
        val renderManager = RenderManager()
        log(Level.FINE) { "Creating AutoRunner" }
        val autoRunner = AutoRunner(resourceLoader, projectManager, taskHistory)

        log(Level.FINE) { "Creating GuiInitializer" }
        val gui = GuiInitializer(
                eventController,
                windowHandler,
                renderManager,
                resourceLoader,
                timer,
                projectManager
        ).init()

        log(Level.FINE) { "Creating Loop" }
        val mainLoop = Loop(
                listOf(renderManager, gui.listeners, eventController, windowHandler, futureExecutor, autoRunner),
                timer, windowHandler::shouldClose)

        parseArgs(programArguments, exportManager, projectManager)

        val state = Program(
                resourceLoader = resourceLoader,
                windowHandler = windowHandler,
                eventController = eventController,
                renderManager = renderManager,
                mainLoop = mainLoop,
                exportManager = exportManager,
                gui = gui,
                projectManager = projectManager,
                futureExecutor = futureExecutor,
                taskHistory = taskHistory
        )

        Debugger.setInit(state)
        gui.selectionHandler.listeners.add(gui.guiUpdater::onSelectionUpdate)
        gui.selectionHandler.listeners.add(gui.canvasManager::onSelectionUpdate)
        gui.projectManager.modelChangeListeners.add(gui.canvasManager::onModelUpdate)
        gui.canvasManager.processor = taskHistory


        log(Level.FINE) { "Starting GLFW" }
        GLFWLoader.init()
        log(Level.FINE) { "Starting GLFW window" }
        windowHandler.create()
        windowHandler.loadIcon(resourceLoader)
        log(Level.FINE) { "Binding listeners and callbacks to window" }
        eventController.bindWindow(windowHandler.window)

        log(Level.FINE) { "Initializing renderers" }
        renderManager.initOpenGl(resourceLoader, gui)
        log(Level.FINE) { "Registering Input event listeners" }
        gui.listeners.initListeners(eventController, gui)

        log(Level.FINE) { "Reloading gui resources" }
        gui.resources.reload(resourceLoader)
        gui.root.loadResources(gui.resources)

        log(Level.FINE) { "[GuiInitializer] Binding text inputs" }
        gui.dispatcher.state = state
        futureExecutor.programState = state
        gui.guiUpdater.bindTextInputs(gui.editorPanel)

        gui.buttonBinder.bindButtons(gui.root.mainPanel!!)
        gui.root.mainPanel!!.bindProperties(gui.state)

        log(Level.FINE) { "Searching for last project" }
        exportManager.loadLastProjectIfExists(projectManager)
        log(Level.FINE) { "Initialization done" }
        return state
    }

    private fun parseArgs(programArguments: List<String>, exportManager: ExportManager,
                          projectManager: ProjectManager) {
        if (programArguments.isNotEmpty()) {
            log(Level.FINE) { "Parsing arguments..." }
            if (File(programArguments[0]).exists()) {
                try {
                    log(Level.NORMAL) { "Loading Project at '${programArguments[0]}'" }
                    val (model, properties) = exportManager.loadProject(programArguments[0])
                    projectManager.loadProjectProperties(properties)
                    projectManager.updateModel(model)
                    log(Level.NORMAL) { "Project loaded" }
                } catch (e: Exception) {
                    log(Level.ERROR) { "Unable to load project file at '${programArguments[0]}'" }
                    e.print()
                }
            } else {
                log(Level.ERROR) { "Invalid program argument: '${programArguments[0]}' is not a valid path to a save file" }
            }
            log(Level.FINE) { "Parsing arguments done" }
        } else {
            log(Level.FINE) { "No program arguments found, ignoring..." }
        }
    }

    fun start(program: Program) {
        log(Level.FINE) { "Starting loop" }
        program.mainLoop.run()
        log(Level.FINE) { "Ending loop" }
        stop()
    }

    private fun stop() {
        GLFWLoader.terminate()
    }
}