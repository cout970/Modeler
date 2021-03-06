package com.cout970.modeler.input.event

import com.cout970.glutilities.event.Event

/**
 * Created by cout970 on 2016/11/29.
 */
interface IEventController {

    fun <T : Event> addListener(clazz: Class<T>, listener: IEventListener<T>)

    fun <T : Event> addListener(clazz: Class<T>, listener: (T) -> Boolean)
}