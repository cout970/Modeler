package com.cout970.modeler.input.window

import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.texture.TextureLoader
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.glutilities.window.WindowBuilder
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.util.ITickeable
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.opengl.GL11
import java.util.*

/**
 * Created by cout970 on 2016/11/29.
 */
class WindowHandler(private val timer: Timer) : ITickeable {

    lateinit var window: GLFWWindow

    var viewport = Pair(vec2Of(0), vec2Of(1))
        set(value) {
            GL11.glViewport(value.first.xi, value.first.yi, value.second.xi, value.second.yi)
            field = value
        }
    private val viewportStack = Stack<Pair<IVector2, IVector2>>()

    companion object {
        const val WINDOW_TITLE = ""
    }

    fun create() {
        window = WindowBuilder.build {
            title = WINDOW_TITLE
            size = vec2Of(800, 600)
            vSync = true
//            properties.put(GLFW_DECORATED, GLFW_FALSE)
//            properties.put(GLFW_MAXIMIZED, GLFW_TRUE)
        }
        window.setVSync(true)
        window.center()
        window.show()
        resetViewport()
    }

    fun loadIcon(rl: ResourceLoader) {
        val texture = TextureLoader.loadTexture(rl.readResource("assets/textures/icon.png"))
        val buffer = GLFWImage.create(1)
        val image = GLFWImage.malloc()
        image.set(texture.size.xi, texture.size.yi, texture.bitMap)
        buffer.put(image)
        GLFW.glfwSetWindowIcon(window.id, buffer)
//        image.free()
//        buffer.free()
    }

    fun close() {
        glfwSetWindowShouldClose(window.id, true)
    }

    fun shouldClose() = window.shouldClose()

    override fun tick() {
        window.swapBuffers()
        Thread.sleep(33)
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

