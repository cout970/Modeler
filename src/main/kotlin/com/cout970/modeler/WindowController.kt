package com.cout970.modeler

import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.glutilities.window.WindowBuilder
import com.cout970.modeler.util.ITickeable
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.xi
import com.cout970.vector.extensions.yi
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2016/11/29.
 */
class WindowController : ITickeable {

    lateinit var stop: () -> Unit
    lateinit var window: GLFWWindow
    lateinit var timer: Timer

    fun show() {
        window = WindowBuilder.build {
            title = WINDOW_TITLE
            size = vec2Of(800, 600)
        }
        window.setVSync(false)
        window.center()
        window.show()
    }

    override fun tick() {
        if (window.shouldClose()) {
            stop()
        }
        window.swapBuffers()
        GL11.glViewport(0, 0, window.size.xi, window.size.yi)
        window.setTitle("[${timer.fps} FPS]")
    }
}