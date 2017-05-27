package com.cout970.modeler

import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWLoader
import com.cout970.modeler.core.config.ConfigManager
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.view.GuiInitializer
import com.cout970.modeler.view.event.EventController
import com.cout970.modeler.view.render.RenderManager
import com.cout970.modeler.view.window.Loop
import com.cout970.modeler.view.window.WindowHandler
import java.io.File

/**
 * Created by cout970 on 2016/11/29.
 */
class Initializer {

    fun init(programArguments: List<String>): ProgramSate {

        Debugger.setInit(this)

        log(Level.FINE) { "Loading config" }
        ConfigManager.loadConfig()
        log(Level.FINE) { "Config loaded" }

        log(Level.FINE) { "Creating ResourceLoader" }
        val resourceLoader = ResourceLoader()
        log(Level.FINE) { "Creating Timer" }
        val timer = Timer()
        log(Level.FINE) { "Creating WindowHandler" }
        val windowHandler = WindowHandler(timer)

        log(Level.FINE) { "Creating EventController" }
        val eventController = EventController()
        log(Level.FINE) { "Creating ProjectManager" }
        val projectManager = ProjectManager()
        log(Level.FINE) { "Creating ModelController" }
//        modelEditor = ModelEditor(projectManager)
        log(Level.FINE) { "Creating ExportManager" }
        val exportManager = ExportManager(projectManager, resourceLoader)

        log(Level.FINE) { "Creating GuiResources" }
//        guiResources = GuiResources(resourceLoader)

        log(Level.FINE) { "Creating RenderManager" }
        val renderManager = RenderManager()

        log(Level.FINE) { "Creating GuiInitializer" }
        val guiState = GuiInitializer(eventController, windowHandler,
                projectManager, renderManager, resourceLoader, timer).init()

        log(Level.FINE) { "Creating Loop" }
        val mainLoop = Loop(listOf(renderManager, guiState.listeners, eventController, windowHandler),
                timer, windowHandler::shouldClose)

        parseArgs(programArguments, exportManager, projectManager)

        val state = ProgramSate(
                resourceLoader,
                windowHandler,
                eventController,
                projectManager,
                renderManager,
                mainLoop,
                exportManager,
                guiState
        )

        log(Level.FINE) { "Starting GLFW" }
        GLFWLoader.init()
        log(Level.FINE) { "Starting GLFW window" }
        windowHandler.create()
        log(Level.FINE) { "Binding listeners and callbacks to window" }
        eventController.bindWindow(windowHandler.window)

        log(Level.FINE) { "Initializing renderers" }
        renderManager.initOpenGl(resourceLoader, windowHandler, eventController)
        log(Level.FINE) { "Registering Input event listeners" }
        guiState.listeners.initListeners(eventController, guiState)

        log(Level.FINE) { "Adding placeholder cube" }
//        modelEditor.addCube(vec3Of(16, 16, 16))
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
                    val project = exportManager.loadProject(programArguments[0])
                    log(Level.NORMAL) { "Project loaded" }
                    projectManager.project = project
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

    fun start(program: ProgramSate) {
        log(Level.FINE) { "Starting loop" }
        program.mainLoop.run()
        log(Level.FINE) { "Ending loop" }
        stop()
    }

    private fun stop() {
        GLFWLoader.terminate()
    }
}