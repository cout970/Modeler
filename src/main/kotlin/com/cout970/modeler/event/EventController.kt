package com.cout970.modeler.event

import com.cout970.glutilities.event.Event
import com.cout970.glutilities.event.EventManager
import com.cout970.modeler.ITickeable

/**
 * Created by cout970 on 2016/11/29.
 */
class EventController() : ITickeable {

    private val listeners = mutableListOf<IEventListener>()
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
            listeners.any {
                it.onEvent(event)
            }
        }
    }

    fun addListener(l: IEventListener) {
        listeners += l
        listeners.sortBy { it.priority }
    }
}