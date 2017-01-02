package com.cout970.modeler

import com.cout970.glutilities.window.GLFWLoader
import com.cout970.modeler.config.ConfigManager
import com.cout970.modeler.event.EventController
import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.model.Mesh
import com.cout970.modeler.modeleditor.ModelController
import com.cout970.modeler.view.ViewManager
import com.cout970.modeler.view.render.RenderManager
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/11/29.
 */
class Init {

    lateinit var windowController: WindowController
    lateinit var resourceManager: ResourceManager
    lateinit var modelController: ModelController
    lateinit var eventController: EventController
    lateinit var viewManager: ViewManager
    lateinit var renderManager: RenderManager
    lateinit var mainLoop: LoopController

    fun run() {
        start()
        log(Level.FINE) { "Starting loop" }
        mainLoop.run()
        log(Level.FINE) { "Ending loop" }
        stop()
    }

    private fun start() {
        log(Level.FINEST) { "Creating WindowController" }
        windowController = WindowController()
        log(Level.FINEST) { "Creating ResourceManager" }
        resourceManager = ResourceManager()
        log(Level.FINEST) { "Creating ModelController" }
        modelController = ModelController()
        log(Level.FINEST) { "Creating EventController" }
        eventController = EventController()
        log(Level.FINEST) { "Creating ViewManager" }
        viewManager = ViewManager()
        log(Level.FINEST) { "Creating RenderManager" }
        renderManager = RenderManager(viewManager)
        log(Level.FINEST) { "Creating LoopController" }
        mainLoop = LoopController(listOf(renderManager, viewManager, eventController, modelController, windowController))

        windowController.stop = { mainLoop.stop = true }
        windowController.timer = mainLoop.timer
        log(Level.FINEST) { "Registering listeners for modelController" }
        modelController.registerListeners(eventController)

        log(Level.FINE) { "Loading config" }
        ConfigManager.loadConfig()
        log(Level.FINE) { "Config loaded" }

        log(Level.FINE) { "Starting GLFW" }
        GLFWLoader.init()
        log(Level.FINEST) { "Starting GLFW window" }
        windowController.show()
        log(Level.FINEST) { "Binding listeners and callbacks to window" }
        eventController.bindWindow(windowController.window)

        log(Level.FINEST) { "Initializing renderers" }
        renderManager.initOpenGl(resourceManager, mainLoop.timer, windowController.window)
        log(Level.FINEST) { "Initializing GUI" }
        viewManager.init(renderManager, modelController, windowController)
        log(Level.FINEST) { "Registering listeners for sceneController" }
        viewManager.sceneController.registerListeners(eventController)
        log(Level.FINEST) { "Registering listeners for moduleController" }
        viewManager.moduleController.registerListeners(eventController)

        log(Level.FINE) { "Adding placeholder cube" }
        modelController.inserter.insertComponent(Mesh.createCube(vec3Of(16, 16, 16)))
    }

    private fun stop() {
        GLFWLoader.terminate()
    }
}