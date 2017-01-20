package com.cout970.modeler.event

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.device.Mouse
import com.cout970.glutilities.event.*
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.util.ITickeable
import java.util.*

/**
 * Created by cout970 on 2016/11/29.
 */
class EventController : ITickeable, IEventController {

    private val listeners = mutableMapOf<Class<Event>, MutableList<IEventListener<Event>>>()
    private val lock = Any()
    private val eventQueue = Collections.synchronizedList(mutableListOf<() -> Unit>())
    lateinit var keyboard: Keyboard
    lateinit var mouse: Mouse

    init {
        EventManager.registerListener(this::onEvent)
        addListener(EventCharTyped::class.java, CharCallback)
        addListener(EventFileDrop::class.java, DropCallback)
        addListener(EventKeyUpdate::class.java, KeyCallback)
        addListener(EventMouseScroll::class.java, ScrollCallback)
        addListener(EventCharMods::class.java, CharModsCallback)
        addListener(EventCursorEnter::class.java, CursorEnterCallback)
        addListener(EventFrameBufferSize::class.java, FramebufferSizeCallback)
        addListener(EventMouseClick::class.java, MouseButtonCallback)
        addListener(EventCursorPos::class.java, CursorPosCallback)
        addListener(EventWindowClose::class.java, WindowCloseCallback)
        addListener(EventWindowFocus::class.java, WindowFocusCallback)
        addListener(EventWindowIconify::class.java, WindowIconifyCallback)
        addListener(EventWindowPos::class.java, WindowPosCallback)
        addListener(EventWindowRefresh::class.java, WindowRefreshCallback)
        addListener(EventWindowSize::class.java, WindowSizeCallback)
    }

    override fun tick() {
        EventManager.pollEvents()
        eventQueue.forEach { it() }
        eventQueue.clear()
    }

    private fun onEvent(event: Event) {
        eventQueue.add {
            listeners[event.javaClass]?.run {
                any { it.onEvent(event) }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Event> addListener(clazz: Class<T>, listener: IEventListener<T>) {
        putListener(clazz as Class<Event>, listener as IEventListener<Event>)
    }

    private fun putListener(clazz: Class<Event>, listener: IEventListener<Event>) {
        if (listeners.containsKey(clazz)) {
            val list = listeners[clazz]!!
            list += listener
            list.sortBy { it.priority }
        } else {
            listeners.put(clazz, mutableListOf(listener))
        }
    }

    fun bindWindow(window: GLFWWindow) {
        EventManager.registerWindow(window.id)
        keyboard = Keyboard(window.id)
        mouse = Mouse(window.id)
    }
}
