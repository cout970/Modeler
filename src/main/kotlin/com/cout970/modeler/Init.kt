package com.cout970.modeler

import com.cout970.glutilities.window.GLFWLoader
import com.cout970.modeler.event.EventController
import com.cout970.modeler.model.*
import com.cout970.modeler.modelcontrol.ModelController
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

        GLFWLoader.init()
        windowController.show()
        eventController.bindWindow(windowController.window)

        renderManager.load(resourceManager, eventController, modelController, mainLoop.timer)
        modelController.registerListeners(eventController)

        modelController.model.objects += ModelObject().apply {
            groups += ModelGroup().apply {
                components += Cube.create(vec3Of(5, 1, 5))
                components += Plane(
                        Vertex(vec3Of(0, 0, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(1, 0, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(1, 1, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(0, 1, 0), vec2Of(1, 0)))

                components += Plane(
                        Vertex(vec3Of(0, 0, 1), vec2Of(1, 0)),
                        Vertex(vec3Of(1, 0, 1), vec2Of(1, 0)),
                        Vertex(vec3Of(1, 1, 1), vec2Of(1, 0)),
                        Vertex(vec3Of(0, 1, 1), vec2Of(1, 0)))

                components += Plane(
                        Vertex(vec3Of(0, 0, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(0, 1, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(0, 1, 1), vec2Of(1, 0)),
                        Vertex(vec3Of(0, 0, 1), vec2Of(1, 0)))

                components += Plane(
                        Vertex(vec3Of(1, 0, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(1, 1, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(1, 1, 1), vec2Of(1, 0)),
                        Vertex(vec3Of(1, 0, 1), vec2Of(1, 0)))

                components += Plane(
                        Vertex(vec3Of(0, 0, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(1, 0, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(1, 0, 1), vec2Of(1, 0)),
                        Vertex(vec3Of(0, 0, 1), vec2Of(1, 0)))

                components += Plane(
                        Vertex(vec3Of(0, 1, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(1, 1, 0), vec2Of(1, 0)),
                        Vertex(vec3Of(1, 1, 1), vec2Of(1, 0)),
                        Vertex(vec3Of(0, 1, 1), vec2Of(1, 0)))
            }
        }
        renderManager.initOpenGl(windowController.window)
    }

    fun stop() {
        GLFWLoader.terminate()
    }
}