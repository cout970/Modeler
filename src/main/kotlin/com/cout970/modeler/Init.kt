package com.cout970.modeler

import com.cout970.glutilities.window.GLFWLoader
import com.cout970.modeler.event.EventController
import com.cout970.modeler.model.Mesh
import com.cout970.modeler.modelcontrol.ModelController
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
        mainLoop.run()
        stop()
    }

    private fun start() {
        windowController = WindowController()
        resourceManager = ResourceManager()
        modelController = ModelController()
        eventController = EventController()
        viewManager = ViewManager()
        renderManager = RenderManager(viewManager)
        mainLoop = LoopController(listOf(viewManager, renderManager, eventController, modelController, windowController))

        windowController.stop = { mainLoop.stop = true }
        modelController.registerListeners(eventController)

        GLFWLoader.init()
        windowController.show()
        eventController.bindWindow(windowController.window)

        renderManager.initOpenGl(resourceManager, mainLoop.timer, windowController.window)
        viewManager.init(renderManager, modelController)
        viewManager.sceneController.registerListeners(eventController)
        viewManager.moduleController.registerListeners(eventController)

        modelController.inserter.insertComponent(Mesh.createCube(vec3Of(1, 1, 1)))
    }

    private fun stop() {
        GLFWLoader.terminate()
    }
}