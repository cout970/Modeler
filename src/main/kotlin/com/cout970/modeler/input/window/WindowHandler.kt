package com.cout970.modeler.input.window

import com.cout970.glutilities.texture.TextureLoader
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.glutilities.window.WindowBuilder
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.util.VSyncTimer
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import java.io.File
import java.util.*


/**
 * Created by cout970 on 2016/11/29.
 */
class WindowHandler : ITickeable {

    lateinit var window: GLFWWindow

    var viewport = Pair(vec2Of(0), vec2Of(1))
        set(value) {
            GL11.glViewport(value.first.xi, value.first.yi, value.second.xi, value.second.yi)
            GL11.glEnable(GL13.GL_MULTISAMPLE)
            field = value
        }

    private val viewportStack = Stack<Pair<IVector2, IVector2>>()
    private val vsync = VSyncTimer()


    companion object {
        const val WINDOW_TITLE = "TO BE NAMED"
    }

    fun create() {
        window = WindowBuilder.build {
            title = WINDOW_TITLE
            size = vec2Of(800, 600)
            vSync = false
            properties.put(GLFW_STENCIL_BITS, 24)
            properties.put(GLFW_SAMPLES, 8)
//            properties.put(GLFW_CONTEXT_VERSION_MAJOR, 3)
//            properties.put(GLFW_CONTEXT_VERSION_MINOR, 2)
//            properties.put(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
//            properties.put(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
        }
        window.center()
        window.show()
        resetViewport()
    }

    fun loadIcon(rl: ResourceLoader) {
        val tex16 = TextureLoader.loadTexture(rl.readResource("assets/textures/icon16.png"))
        val tex32 = TextureLoader.loadTexture(rl.readResource("assets/textures/icon32.png"))
        val tex48 = TextureLoader.loadTexture(rl.readResource("assets/textures/icon48.png"))
        val icons = GLFWImage.malloc(3)

        icons.position(0)
                .width(tex16.size.xi)
                .height(tex16.size.yi)
                .pixels(tex16.bitMap)
        icons.position(1)
                .width(tex32.size.xi)
                .height(tex32.size.yi)
                .pixels(tex32.bitMap)
        icons.position(2)
                .width(tex48.size.xi)
                .height(tex48.size.yi)
                .pixels(tex48.bitMap)

        icons.position(0)
        glfwSetWindowIcon(window.id, icons)
        icons.free()
    }

    fun close() {
        glfwSetWindowShouldClose(window.id, true)
    }

    fun shouldClose() = window.shouldClose()

    fun updateTitle(projectName: String){
        val projectPath = File(".").absolutePath
        window.setTitle("$projectName [$projectPath] - $WINDOW_TITLE")
    }

    override fun tick() {
        Profiler.startSection("windows")
        Profiler.startSection("swapBuffers")
        window.swapBuffers()
        Profiler.nextSection("vsyncWait")
        vsync.waitIfNecessary()
        Profiler.endSection()
        Profiler.endSection()
    }

    fun resetViewport() {
        viewport = Pair(vec2Of(0), window.getFrameBufferSize())
    }

    fun saveViewport(pos: IVector2, size: IVector2, funk: () -> Unit) {
        pushViewport(pos to size)
        funk()
        popViewport()
    }

    fun pushViewport(vp: Pair<IVector2, IVector2>) {
        viewportStack.push(viewport)
        viewport = vp
    }

    fun popViewport() {
        viewport = viewportStack.pop()
    }
}

