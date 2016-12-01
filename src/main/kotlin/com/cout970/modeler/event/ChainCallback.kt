package com.cout970.modeler.event

import org.liquidengine.cbchain.*
import org.lwjgl.glfw.*

/**
 * Created by cout970 on 2016/12/01.
 */

object CharCallback : AbstractChainCallback<GLFWCharCallbackI>(), IChainCharCallback {
    override fun invoke(window: Long, codePoint: Int) {
        callbackChain.forEach { invoke(window, codePoint) }
    }
}

object DropCallback : AbstractChainCallback<GLFWDropCallbackI>(), IChainDropCallback {
    override fun invoke(window: Long, count: Int, names: Long) {
        callbackChain.forEach { invoke(window, count, names) }
    }
}

object KeyCallback : AbstractChainCallback<GLFWKeyCallbackI>(), IChainKeyCallback {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        callbackChain.forEach { invoke(window, key, scancode, action, mods) }
    }
}

object ScrollCallback : AbstractChainCallback<GLFWScrollCallbackI>(), IChainScrollCallback {
    override fun invoke(window: Long, xoffset: Double, yoffset: Double) {
        callbackChain.forEach { invoke(window, xoffset, yoffset) }
    }
}

object CharModsCallback : AbstractChainCallback<GLFWCharModsCallbackI>(), IChainCharModsCallback {
    override fun invoke(window: Long, codepoint: Int, mods: Int) {
        callbackChain.forEach { invoke(window, codepoint, mods) }
    }
}

object CursorEnterCallback : AbstractChainCallback<GLFWCursorEnterCallbackI>(), IChainCursorEnterCallback {
    override fun invoke(window: Long, entered: Boolean) {
        callbackChain.forEach { invoke(window, entered) }
    }
}

object FramebufferSizeCallback : AbstractChainCallback<GLFWFramebufferSizeCallbackI>(), IChainFramebufferSizeCallback {
    override fun invoke(window: Long, width: Int, height: Int) {
        callbackChain.forEach { invoke(window, width, height) }
    }
}

object MouseButtonCallback : AbstractChainCallback<GLFWMouseButtonCallbackI>(), IChainMouseButtonCallback {
    override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
        callbackChain.forEach { invoke(window, button, action, mods) }
    }
}

object CursorPosCallback : AbstractChainCallback<GLFWCursorPosCallbackI>(), IChainCursorPosCallback {
    override fun invoke(window: Long, xpos: Double, ypos: Double) {
        callbackChain.forEach { invoke(window, xpos, ypos) }
    }
}

object WindowCloseCallback : AbstractChainCallback<GLFWWindowCloseCallbackI>(), IChainWindowCloseCallback {
    override fun invoke(window: Long) {
        callbackChain.forEach { invoke(window) }
    }
}

object WindowFocusCallback : AbstractChainCallback<GLFWWindowFocusCallbackI>(), IChainWindowFocusCallback {
    override fun invoke(window: Long, focused: Boolean) {
        callbackChain.forEach { invoke(window, focused) }
    }
}

object WindowIconifyCallback : AbstractChainCallback<GLFWWindowIconifyCallbackI>(), IChainWindowIconifyCallback {
    override fun invoke(window: Long, iconified: Boolean) {
        callbackChain.forEach { invoke(window, iconified) }
    }
}

object WindowPosCallback : AbstractChainCallback<GLFWWindowPosCallbackI>(), IChainWindowPosCallback {
    override fun invoke(window: Long, xpos: Int, ypos: Int) {
        callbackChain.forEach { invoke(window, xpos, ypos) }
    }
}

object WindowRefreshCallback : AbstractChainCallback<GLFWWindowRefreshCallbackI>(), IChainWindowRefreshCallback {
    override fun invoke(window: Long) {
        callbackChain.forEach { invoke(window) }
    }
}

object WindowSizeCallback : AbstractChainCallback<GLFWWindowSizeCallbackI>(), IChainWindowSizeCallback {
    override fun invoke(window: Long, width: Int, height: Int) {
        callbackChain.forEach { invoke(window, width, height) }
    }
}