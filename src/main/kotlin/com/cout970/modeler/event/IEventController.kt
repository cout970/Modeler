package com.cout970.modeler.event

import com.cout970.glutilities.event.Event

/**
 * Created by cout970 on 2016/11/29.
 */
interface IEventController {

    fun <T : Event> addListener(clazz: Class<T>, listener: IEventListener<T>)
}