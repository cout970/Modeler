package com.cout970.modeler.input.event

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.device.Mouse
import com.cout970.glutilities.event.*
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.core.log.print
import com.cout970.modeler.util.ITickeable
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


/**
 * Created by cout970 on 2016/11/29.
 */
class EventController : ITickeable, IEventController, IInput {

    private val listeners = mutableMapOf<Class<Event>, MutableList<IEventListener<Event>>>()
    private val eventQueue = Collections.synchronizedCollection(mutableListOf<() -> Unit>())
    override lateinit var keyboard: Keyboard
    override lateinit var mouse: Mouse

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
        Profiler.startSection("pollEvents")
        EventManager.pollEvents()
        val events = eventQueue.toList()
        eventQueue.clear()
        events.forEach { it() }

        // Fuck X11, glfwGetCursorPos sometimes get stuck, this is the only way to avoid it, and it sucks
        runWithTimeout { mouse.update() }

        Profiler.endSection()
    }

    val executor = Executors.newCachedThreadPool()

    fun runWithTimeout(func: () -> Unit) {
        Profiler.startSection("hacks")
        val future = executor.submit(func)
        try {
            future.get(2, TimeUnit.MILLISECONDS)
        } catch (e: TimeoutException) {
            // ignored
        } catch (e: Exception) {
            e.print()
        } finally {
            future.cancel(true)
        }
        Profiler.endSection()
    }

    private fun onEvent(event: Event) {
        Profiler.startSection("onEvent")
        eventQueue.add {
            listeners[event.javaClass]?.run {
                any { it.onEvent(event) }
            }
        }
        Profiler.endSection()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Event> addListener(clazz: Class<T>, listener: IEventListener<T>) {
        putListener(clazz as Class<Event>, listener as IEventListener<Event>)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Event> addListener(clazz: Class<T>, listener: (T) -> Boolean) {
        putListener(clazz as Class<Event>, object : IEventListener<Event> {
            override fun onEvent(e: Event): Boolean {
                return listener.invoke(e as T)
            }
        })
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
