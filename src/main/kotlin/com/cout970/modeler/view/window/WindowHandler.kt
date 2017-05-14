package com.cout970.modeler.view.window

import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.glutilities.window.WindowBuilder
import com.cout970.modeler.util.ITickeable
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.xi
import com.cout970.vector.extensions.yi
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.opengl.GL11
import java.util.*

/**
 * Created by cout970 on 2016/11/29.
 */
class WindowHandler : ITickeable {

    val timer = Timer()
    lateinit var window: GLFWWindow

    var viewport = Pair(vec2Of(0), vec2Of(1))
        set(value) {
            GL11.glViewport(value.first.xi, value.first.yi, value.second.xi, value.second.yi)
            field = value
        }
    private val viewportStack = Stack<Pair<IVector2, IVector2>>()

    companion object {
        const val WINDOW_TITLE = "TO BE NAMED"
    }

    fun create() {
        window = WindowBuilder.build {
            title = WINDOW_TITLE
            size = vec2Of(800, 600)
//            properties.put(GLFW_DECORATED, GLFW_FALSE)
//            properties.put(GLFW_MAXIMIZED, GLFW_TRUE)
        }
        window.setVSync(true)
        window.center()
        window.show()
        resetViewport()
    }

    fun close() {
        glfwSetWindowShouldClose(window.id, true)
    }

    fun shouldClose() = window.shouldClose()

    override fun tick() {
        window.swapBuffers()
        GL11.glViewport(0, 0, window.size.xi, window.size.yi)
        window.setTitle("$WINDOW_TITLE [${timer.fps} FPS]")
    }

    fun resetViewport() {
        viewport = Pair(vec2Of(0), window.getFrameBufferSize())
    }

    fun saveViewport(pos: IVector2, size: IVector2, funk: () -> Unit) {
        pushViewport()
        viewport = pos to size
        funk()
        popViewport()
    }

    fun pushViewport() {
        viewportStack.push(viewport)
    }

    fun popViewport() {
        viewport = viewportStack.pop()
    }
}

