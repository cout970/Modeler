package com.cout970.modeler

import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWLoader
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.controller.AutoRunner
import com.cout970.modeler.controller.FutureExecutor
import com.cout970.modeler.controller.TaskHistory
import com.cout970.modeler.core.config.ConfigManager
import com.cout970.modeler.core.export.ExportManager
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.model.selection.SelectionHandler
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.project.ProjectPropertyHolder
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.gui.GuiInitializer
import com.cout970.modeler.gui.event.pushNotification
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

        log(Level.FINE) { "Creating ResourceLoader" }
        val resourceLoader = ResourceLoader()
        log(Level.FINE) { "Creating Timer" }
        val timer = Timer()
        log(Level.FINE) { "Creating WindowHandler" }
        val windowHandler = WindowHandler()
        log(Level.FINE) { "Creating EventController" }
        val eventController = EventController()
        log(Level.FINE) { "Creating ModelSelectionHandler" }
        val modelSelectionHandler = SelectionHandler(SelectionTarget.MODEL)
        log(Level.FINE) { "Creating ModelSelectionHandler" }
        val textureSelectionHandler = SelectionHandler(SelectionTarget.TEXTURE)
        log(Level.FINE) { "Creating ProjectManager" }
        val projectManager = ProjectManager(modelSelectionHandler, textureSelectionHandler)
        log(Level.FINE) { "Creating FutureExecutor" }
        val futureExecutor = FutureExecutor()
        log(Level.FINE) { "Creating TaskHistory" }
        val taskHistory = TaskHistory(futureExecutor)
        log(Level.FINE) { "Creating ExportManager" }
        val exportManager = ExportManager(resourceLoader)
        log(Level.FINE) { "Creating RenderManager" }
        val renderManager = RenderManager()
        log(Level.FINE) { "Creating AutoRunner" }
        val autoRunner = AutoRunner(resourceLoader, projectManager, exportManager, taskHistory)

        log(Level.FINE) { "Creating GuiInitializer" }
        val gui = GuiInitializer(
                eventController = eventController,
                windowHandler = windowHandler,
                resourceLoader = resourceLoader,
                timer = timer,
                programState = projectManager,
                propertyHolder = ProjectPropertyHolder(projectManager)
        ).init()

        log(Level.FINE) { "Creating Loop" }
        val mainLoop = Loop(
                listOf(eventController, gui.listeners, renderManager, windowHandler, futureExecutor, autoRunner, Profiler),
                timer, windowHandler::shouldClose
        )

        log(Level.FINE) { "Initializing and linking program components" }
        val program = Program(
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

        Debugger.setInit(program)

        gui.cursorManager.taskProcessor = taskHistory
        gui.cursorManager.setGui(gui)
        gui.cursorManager.updateCanvas = gui.canvasManager::updateSelectedCanvas

        modelSelectionHandler.typeGetter = gui.state::selectionType

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
        gui.listeners.initListeners(eventController, projectManager, gui)

        log(Level.FINE) { "Reloading gui resources" }
        gui.resources.reload(resourceLoader)
        gui.root.loadResources(gui.resources)

        log(Level.FINE) { "Loading usecases" }
        futureExecutor.programState = program
        gui.dispatcher.state = program
        gui.dispatcher.checkUseCases()

        log(Level.FINE) { "Binding buttons" }
        gui.root.bindButtons(gui.buttonBinder)

        log(Level.FINE) { "Updating gui scale" }
        gui.root.updateSizes(windowHandler.window.getFrameBufferSize())

        log(Level.FINE) { "Processing args" }
        parseArgs(programArguments, exportManager, projectManager, windowHandler)

        log(Level.FINE) { "Searching for last project" }
        exportManager.loadLastProjectIfExists(projectManager, gui)

        log(Level.FINE) { "Showing window" }
        windowHandler.window.show()

        log(Level.FINE) { "Initialization done" }
        return program
    }

    private fun parseArgs(args: List<String>, exportManager: ExportManager,
                          projectManager: ProjectManager, windowHandler: WindowHandler) {
        if (args.isEmpty()) {
            log(Level.FINE) { "No program arguments found, ignoring..." }
            return
        }

        log(Level.FINE) { "Parsing arguments..." }
        val file = File(args[0])
        if (!file.exists()) {
            log(Level.ERROR) { "Invalid program argument: '${args[0]}' is not a valid path to a save file" }
            return
        }

        try {
            log(Level.NORMAL) { "Loading Project at '${args[0]}'" }
            val save = exportManager.loadProject(args[0])
            projectManager.loadProjectProperties(save.projectProperties)
            projectManager.updateModel(save.model)
            windowHandler.updateTitle(save.projectProperties.name)
            log(Level.NORMAL) { "Project loaded" }

            pushNotification("Project loaded", "Loaded project successfully")

        } catch (e: Exception) {
            log(Level.ERROR) { "Unable to load project file at '${args[0]}'" }
            e.print()
            pushNotification("Error loading project", "Unable to load project, path: '${args[0]}': $e")
        }
        log(Level.FINE) { "Parsing arguments done" }
    }

    fun start(program: Program) {
        log(Level.FINE) { "Starting loop" }
        program.mainLoop.run()
        log(Level.FINE) { "Ending loop" }
        GLFWLoader.terminate()
    }
}