package com.cout970.modeler.controller

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.record.HistoricalRecord
import com.cout970.modeler.core.record.HistoryLog
import com.cout970.modeler.core.record.action.IAction
import com.cout970.modeler.util.IFutureExecutor
import com.cout970.modeler.util.ITickeable
import java.util.*

/**
 * Created by cout970 on 2017/06/09.
 */
class ActionExecutor(val projectManager: ProjectManager) : ITickeable, IFutureExecutor, IModelSetter {

    val actionTrigger = ActionTrigger(this, this)

    override var model: IModel get() = projectManager.model
        set(value) = projectManager.updateModel(value)

    private val actionQueue = LinkedList<() -> Unit>()

    val log = HistoryLog()
    val historicalRecord = HistoricalRecord(log, this::addToQueue)

    override fun addToQueue(function: () -> Unit) {
        actionQueue.add(function)
    }

    override fun tick() = Unit

    override fun postTick() {
        while (actionQueue.isNotEmpty()) {
            actionQueue.poll().invoke()
        }
    }

    fun enqueueAction(act: IAction) {
        historicalRecord.doAction(act)
    }
}

interface IModelGetter {
    val model: IModel
}

interface IModelSetter : IModelGetter {
    override var model: IModel
}