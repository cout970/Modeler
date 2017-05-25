package com.cout970.modeler

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
class Initializer(val programArguments: List<String>) {

    val resourceLoader: ResourceLoader
    val windowHandler: WindowHandler
    val eventController: EventController
    val projectManager: ProjectManager
    //    val modelEditor: ModelEditor
    val guiInitializer: GuiInitializer
    val renderManager: RenderManager
    val mainLoop: Loop
    val exportManager: ExportManager
//    val guiResources: GuiResources

    init {
        Debugger.setInit(this)

        log(Level.FINE) { "Loading config" }
        ConfigManager.loadConfig()
        log(Level.FINE) { "Config loaded" }

        log(Level.FINE) { "Creating ResourceLoader" }
        resourceLoader = ResourceLoader()
        log(Level.FINE) { "Creating WindowHandler" }
        windowHandler = WindowHandler()
        log(Level.FINE) { "Creating EventController" }
        eventController = EventController()

        log(Level.FINE) { "Creating ProjectManager" }
        projectManager = ProjectManager()
        log(Level.FINE) { "Creating ModelController" }
//        modelEditor = ModelEditor(projectManager)
        log(Level.FINE) { "Creating ExportManager" }
        exportManager = ExportManager(projectManager, resourceLoader)

        log(Level.FINE) { "Creating GuiResources" }
//        guiResources = GuiResources(resourceLoader)

        log(Level.FINE) { "Creating RenderManager" }
        renderManager = RenderManager()
        log(Level.FINE) { "Creating GuiInitializer" }
        guiInitializer = GuiInitializer(eventController, windowHandler, projectManager,
                renderManager, resourceLoader)

        guiInitializer.init()

        log(Level.FINE) { "Creating Loop" }
        mainLoop = Loop(listOf(renderManager, guiInitializer.guiUpdater, eventController, windowHandler),
                windowHandler.timer, windowHandler::shouldClose)

        parseArgs()

        log(Level.FINE) { "Starting GLFW" }
        GLFWLoader.init()
        log(Level.FINE) { "Starting GLFW window" }
        windowHandler.create()
        log(Level.FINE) { "Binding listeners and callbacks to window" }
        eventController.bindWindow(windowHandler.window)

        log(Level.FINE) { "Initializing renderers" }
        renderManager.initOpenGl(resourceLoader, windowHandler, eventController)
//        log(Level.FINE) { "Registering listeners for ViewEventHandler" }
//        guiInitializer.eventListeners.registerListeners(eventController)

        log(Level.FINE) { "Adding placeholder cube" }
//        modelEditor.addCube(vec3Of(16, 16, 16))
        log(Level.FINE) { "Initialization done" }
    }

    private fun parseArgs() {
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

    fun start() {
        log(Level.FINE) { "Starting loop" }
        mainLoop.run()
        log(Level.FINE) { "Ending loop" }
        stop()
    }

    private fun stop() {
        GLFWLoader.terminate()
    }
}