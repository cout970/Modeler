package com.cout970.modeler.modelcontrol

import com.cout970.modeler.ITickeable
import com.cout970.modeler.event.EventController
import com.cout970.modeler.model.Model
import com.cout970.modeler.modelcontrol.action.HistoricalRecord
import com.cout970.modeler.modelcontrol.action.HistoryLog

/**
 * Created by cout970 on 2016/11/29.
 */
class ModelController : ITickeable {

    lateinit var eventController: EventController
    val selectionManager = SelectionManager(this)
    val historyLog = HistoryLog()
    val historyRecord = HistoricalRecord(historyLog)
    var model = Model()

    fun registerListeners(eventController: EventController) {
        this.eventController = eventController
    }

    override fun tick() {

    }
}