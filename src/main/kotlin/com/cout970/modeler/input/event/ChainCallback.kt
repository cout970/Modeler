package com.cout970.modeler.input.event

import com.cout970.glutilities.event.*
import org.liquidengine.cbchain.*
import org.lwjgl.glfw.*

/**
 * Created by cout970 on 2016/12/01.
 */

object CharCallback : AbstractChainCallback<GLFWCharCallbackI>(), IChainCharCallback, IEventListener<EventCharTyped> {
    override fun invoke(window: Long, codePoint: Int) {
        callbackChain.forEach { it.invoke(window, codePoint) }
    }

    override fun onEvent(e: EventCharTyped): Boolean {
        invoke(e.windowID, e.code)
        return false
    }
}

object DropCallback : AbstractChainCallback<GLFWDropCallbackI>(), IChainDropCallback, IEventListener<EventFileDrop> {
    override fun invoke(window: Long, count: Int, names: Long) {
        callbackChain.forEach { it.invoke(window, count, names) }
    }

    override fun onEvent(e: EventFileDrop): Boolean {
        invoke(e.windowID, e.count, e.names)
        return false
    }
}

object KeyCallback : AbstractChainCallback<GLFWKeyCallbackI>(), IChainKeyCallback, IEventListener<EventKeyUpdate> {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        callbackChain.forEach { it.invoke(window, key, scancode, action, mods) }
    }

    override fun onEvent(e: EventKeyUpdate): Boolean {
        invoke(e.windowID, e.keycode, e.scanCode, e.keyState.ordinal, e.mods)
        return false
    }
}

object ScrollCallback : AbstractChainCallback<GLFWScrollCallbackI>(), IChainScrollCallback, IEventListener<EventMouseScroll> {
    override fun invoke(window: Long, xoffset: Double, yoffset: Double) {
        callbackChain.forEach { it.invoke(window, xoffset, yoffset) }
    }

    override fun onEvent(e: EventMouseScroll): Boolean {
        invoke(e.windowID, e.offsetX, e.offsetY)
        return false
    }
}

object CharModsCallback : AbstractChainCallback<GLFWCharModsCallbackI>(), IChainCharModsCallback, IEventListener<EventCharMods> {
    override fun invoke(window: Long, codepoint: Int, mods: Int) {
        callbackChain.forEach { it.invoke(window, codepoint, mods) }
    }

    override fun onEvent(e: EventCharMods): Boolean {
        invoke(e.windowID, e.codePoint, e.mods)
        return false
    }
}

object CursorEnterCallback : AbstractChainCallback<GLFWCursorEnterCallbackI>(), IChainCursorEnterCallback, IEventListener<EventCursorEnter> {
    override fun invoke(window: Long, entered: Boolean) {
        callbackChain.forEach { it.invoke(window, entered) }
    }

    override fun onEvent(e: EventCursorEnter): Boolean {
        invoke(e.windowID, e.entered)
        return false
    }
}

object FramebufferSizeCallback : AbstractChainCallback<GLFWFramebufferSizeCallbackI>(), IChainFramebufferSizeCallback, IEventListener<EventFrameBufferSize> {
    override fun invoke(window: Long, width: Int, height: Int) {
        callbackChain.forEach { it.invoke(window, width, height) }
    }

    override fun onEvent(e: EventFrameBufferSize): Boolean {
        invoke(e.windowID, e.width, e.height)
        return false
    }
}

object MouseButtonCallback : AbstractChainCallback<GLFWMouseButtonCallbackI>(), IChainMouseButtonCallback, IEventListener<EventMouseClick> {
    override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
        callbackChain.forEach { it.invoke(window, button, action, mods) }
    }

    override fun onEvent(e: EventMouseClick): Boolean {
        invoke(e.windowID, e.button, e.keyState.ordinal, e.mods)
        return false
    }
}

object CursorPosCallback : AbstractChainCallback<GLFWCursorPosCallbackI>(), IChainCursorPosCallback, IEventListener<EventCursorPos> {
    override fun invoke(window: Long, xpos: Double, ypos: Double) {
        callbackChain.forEach { it.invoke(window, xpos, ypos) }
    }

    override fun onEvent(e: EventCursorPos): Boolean {
        invoke(e.windowID, e.x, e.y)
        return false
    }
}

object WindowCloseCallback : AbstractChainCallback<GLFWWindowCloseCallbackI>(), IChainWindowCloseCallback, IEventListener<EventWindowClose> {
    override fun invoke(window: Long) {
        callbackChain.forEach { it.invoke(window) }
    }

    override fun onEvent(e: EventWindowClose): Boolean {
        invoke(e.windowID)
        return false
    }
}

object WindowFocusCallback : AbstractChainCallback<GLFWWindowFocusCallbackI>(), IChainWindowFocusCallback, IEventListener<EventWindowFocus> {
    override fun invoke(window: Long, focused: Boolean) {
        callbackChain.forEach { it.invoke(window, focused) }
    }

    override fun onEvent(e: EventWindowFocus): Boolean {
        invoke(e.windowID, e.focused)
        return false
    }
}

object WindowIconifyCallback : AbstractChainCallback<GLFWWindowIconifyCallbackI>(), IChainWindowIconifyCallback, IEventListener<EventWindowIconify> {
    override fun invoke(window: Long, iconified: Boolean) {
        callbackChain.forEach { it.invoke(window, iconified) }
    }

    override fun onEvent(e: EventWindowIconify): Boolean {
        invoke(e.windowID, e.iconified)
        return false
    }
}

object WindowPosCallback : AbstractChainCallback<GLFWWindowPosCallbackI>(), IChainWindowPosCallback, IEventListener<EventWindowPos> {
    override fun invoke(window: Long, xpos: Int, ypos: Int) {
        callbackChain.forEach { it.invoke(window, xpos, ypos) }
    }

    override fun onEvent(e: EventWindowPos): Boolean {
        invoke(e.windowID, e.x, e.y)
        return false
    }
}

object WindowRefreshCallback : AbstractChainCallback<GLFWWindowRefreshCallbackI>(), IChainWindowRefreshCallback, IEventListener<EventWindowRefresh> {
    override fun invoke(window: Long) {
        callbackChain.forEach { it.invoke(window) }
    }

    override fun onEvent(e: EventWindowRefresh): Boolean {
        invoke(e.windowID)
        return false
    }
}

object WindowSizeCallback : AbstractChainCallback<GLFWWindowSizeCallbackI>(), IChainWindowSizeCallback, IEventListener<EventWindowSize> {
    override fun invoke(window: Long, width: Int, height: Int) {
        callbackChain.forEach { it.invoke(window, width, height) }
    }

    override fun onEvent(e: EventWindowSize): Boolean {
        invoke(e.windowID, e.width, e.height)
        return false
    }
}