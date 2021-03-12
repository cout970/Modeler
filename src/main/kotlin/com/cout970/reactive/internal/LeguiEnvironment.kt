package com.cout970.reactive.internal

import org.joml.Vector2f
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.listener.processor.EventProcessorProvider
import org.liquidengine.legui.system.context.CallbackKeeper
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.context.DefaultCallbackKeeper
import org.liquidengine.legui.system.handler.processor.SystemEventProcessor
import org.liquidengine.legui.system.handler.processor.SystemEventProcessorImpl
import org.liquidengine.legui.system.renderer.Renderer
import org.liquidengine.legui.system.renderer.nvg.NvgRenderer
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.glfw.GLFWWindowCloseCallbackI
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil

class LeguiEnvironment(windowSize: Vector2f) {

    val window: Long
    val context: Context
    val systemEventProcessor: SystemEventProcessor
    val renderer: Renderer
    val keeper: CallbackKeeper
    val frame: Frame

    @Volatile
    var running = false
    var update: () -> Unit = {}

    init {
        System.setProperty("joml.nounsafe", "true")
        System.setProperty("java.awt.headless", "true")
        if (!GLFW.glfwInit()) {
            throw RuntimeException("Can't initialize GLFW")
        }
        window = GLFW.glfwCreateWindow(
            windowSize.x.toInt(), windowSize.y.toInt(), "Example", MemoryUtil.NULL,
            MemoryUtil.NULL
        )
        GLFW.glfwShowWindow(window)

        GLFW.glfwMakeContextCurrent(window)
        GL.createCapabilities()
        GLFW.glfwSwapInterval(0)

        // Firstly we need to create frame component for window.
        frame = Frame(windowSize.x, windowSize.y)
        // we can add elements here or on the fly

        // We need to create legui context which shared by renderer and event processor.
        // Also we need to pass event processor for ui events such as click on component, key typing and etc.
        context = Context(window)

        // We need to create callback keeper which will hold all of callbacks.
        // These callbacks will be used in initialization of system event processor
        // (will be added callbacks which will push system events to event queue and after that processed by SystemEventProcessor)
        keeper = DefaultCallbackKeeper()

        // register callbacks for window. Note: all previously binded callbacks will be unbinded.
        CallbackKeeper.registerCallbacks(window, keeper)

        val glfwKeyCallbackI = GLFWKeyCallbackI { _, key, _, action, _ ->
            running = !(key == GLFW.GLFW_KEY_ESCAPE && action != GLFW.GLFW_RELEASE)
        }
        val glfwWindowCloseCallbackI = GLFWWindowCloseCallbackI { _ -> running = false }

        // if we want to create some callbacks for system events you should create and put them to keeper
        //
        // Wrong:
        // glfwSetKeyCallback(window, glfwKeyCallbackI);
        // glfwSetWindowCloseCallback(window, glfwWindowCloseCallbackI);
        //
        // Right:
        keeper.chainKeyCallback.add(glfwKeyCallbackI)
        keeper.chainWindowCloseCallback.add(glfwWindowCloseCallbackI)

        // Event processor for system events. System events should be processed and translated to gui events.
        systemEventProcessor = SystemEventProcessorImpl()
        SystemEventProcessor.addDefaultCallbacks(keeper, systemEventProcessor)

        // Also we need to create renderer provider
        // and create renderer which will render our ui components.
        renderer = NvgRenderer()

        // Initialization finished, so we can start render loop.
        running = true

        // Everything can be done in one thread as well as in separated threads.
        // Here is one-thread example.

        // before render loop we need to initialize renderer
        renderer.initialize()
    }

    fun loop() {
        while (running) {

            // Also we can do it in one line
            context.updateGlfwWindow()
            val windowSize = context.windowSize

            GL11.glClearColor(1f, 1f, 1f, 1f)
            // Set viewport size
            GL11.glViewport(0, 0, windowSize.x, windowSize.y)
            // Clear screen
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

            // render frame
            renderer.render(frame, context)

            // poll events to callbacks
            GLFW.glfwPollEvents()
            GLFW.glfwSwapBuffers(window)

            // Now we need to process events. Firstly we need to process system events.
            systemEventProcessor.processEvents(frame, context)

            // When system events are translated to GUI events we need to process them.
            // This event processor calls listeners added to ui components
            EventProcessorProvider.getInstance().processEvents()

            update()
        }
    }

    fun finalice() {
        // And when rendering is ended we need to destroy renderer
        renderer.destroy()

        GLFW.glfwDestroyWindow(window)
        GLFW.glfwTerminate()
    }
}