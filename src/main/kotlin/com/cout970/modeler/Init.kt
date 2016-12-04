package com.cout970.modeler

import com.cout970.glutilities.event.EventManager
import com.cout970.glutilities.window.GLFWLoader
import com.cout970.modeler.event.EventController
import com.cout970.modeler.model.ModelGroup
import com.cout970.modeler.model.ModelObject
import com.cout970.modeler.model.Plane
import com.cout970.modeler.model.Vertex
import com.cout970.modeler.render.RenderManager
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2016/11/29.
 */
class Init {

    lateinit var windowController: WindowController
    lateinit var resourceManager: ResourceManager
    lateinit var modelController: ModelController
    lateinit var eventController: EventController
    lateinit var renderManager: RenderManager
    lateinit var mainLoop: LoopController

    fun run() {
        start()
        mainLoop.run()
        stop()
    }

    fun start() {
        windowController = WindowController()
        resourceManager = ResourceManager()
        modelController = ModelController()
        eventController = EventController()
        renderManager = RenderManager()
        mainLoop = LoopController(listOf(renderManager, eventController, modelController, windowController))

        windowController.stop = { mainLoop.stop = true }
        windowController.registerListeners(eventController)

        GLFWLoader.init()
        windowController.show()
        EventManager.registerWindow(windowController.window.id)

        renderManager.allViews.forEach {
            it.loadResources(resourceManager)
            it.modelController = modelController
        }
        modelController.model.objects += ModelObject().apply {
            groups += ModelGroup().apply {
                components += Plane(
                        Vertex(vec3Of(-0.2, -0.2, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(0.9, -0.2, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(0.9, 0.9, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(-0.2, 0.9, 0), vec2Of(1, 0)))

                components += Plane(
                        Vertex(vec3Of(0, -0.2, -0.2), vec2Of(1, 0)),
                        Vertex(vec3Of(0, 0.9, -0.2), vec2Of(1, 0)),
                        Vertex(vec3Of(0, 0.9, 0.9), vec2Of(1, 0)),
                        Vertex(vec3Of(0, -0.2, 0.9), vec2Of(1, 0)))
            }
        }
        renderManager.initOpenGl(windowController.window)
    }

    fun stop() {
        GLFWLoader.terminate()
    }
}