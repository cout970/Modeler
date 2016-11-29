package com.cout970.modeler.event

import com.cout970.glutilities.event.Event

/**
 * Created by cout970 on 2016/11/29.
 */
interface IEventListener<in T : Event> {

    val priority: Int get() = 0

    /**
     * Returns true if the vent was consumed
     */
    fun onEvent(e: T): Boolean
}