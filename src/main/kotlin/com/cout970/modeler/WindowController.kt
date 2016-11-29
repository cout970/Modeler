package com.cout970.modeler

import com.cout970.glutilities.event.Event
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.glutilities.window.WindowBuilder
import com.cout970.modeler.event.IEventListener
import com.cout970.vector.extensions.vec2Of

/**
 * Created by cout970 on 2016/11/29.
 */
class WindowController : ITickeable, IEventListener {

    override val priority: Int = 0
    lateinit var stop: () -> Unit
    lateinit var window: GLFWWindow

    fun show() {
        window = WindowBuilder.build {
            title = WINDOW_TITILE
            size = vec2Of(800, 600)
        }
        window.center()
        window.show()
    }

    override fun tick() {
        if (window.shouldClose()) {
            stop()
        }
        window.swapBuffers()
    }

    override fun onEvent(e: Event): Boolean {
        window.setAspectRatio(window.size)
        return false
    }
}