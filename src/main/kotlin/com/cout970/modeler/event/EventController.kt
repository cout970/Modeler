package com.cout970.modeler.event

import com.cout970.glutilities.event.Event
import com.cout970.glutilities.event.EventManager
import com.cout970.modeler.ITickeable

/**
 * Created by cout970 on 2016/11/29.
 */
class EventController() : ITickeable, IEventController {

    private val listeners = mutableMapOf<Class<Event>, MutableList<IEventListener<Event>>>()
    private val eventQueue = mutableListOf<() -> Unit>()

    init {
        EventManager.registerListener(this::onEvent)
    }

    override fun tick() {
        EventManager.pollEvents()
        eventQueue.forEach { it() }
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
}