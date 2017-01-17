package com.cout970.modeler.modeleditor

import com.cout970.modeler.ResourceManager
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.EventController
import com.cout970.modeler.export.ExportManager
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.action.ActionDelete
import com.cout970.modeler.modeleditor.selection.SelectionManager
import com.cout970.modeler.project.Project
import com.cout970.modeler.util.ITickeable
import java.util.*

/**
 * Created by cout970 on 2016/11/29.
 */
class ModelController : ITickeable {

    var project = Project(Config.user, "Unnamed")

    val model get() = project.model

    var modelChange = true

    private val actionQueue = LinkedList<() -> Unit>()
    lateinit var eventController: EventController
    val selectionManager = SelectionManager(this)
    val clipboard = ModelClipboard(this)
    val historyLog = HistoryLog()
    val historyRecord = HistoricalRecord(historyLog, this)
    lateinit var exportManager: ExportManager
    val inserter = ModelInserter(this)

    fun init(resourceManager: ResourceManager) {
        exportManager = ExportManager(this, resourceManager)
    }

    fun registerListeners(eventController: EventController) {
        this.eventController = eventController
    }

    fun updateModel(newModel: Model) {
        historyLog.onModelChange(newModel, model)
        project.model = newModel
        modelChange = true
    }

    fun addToQueue(function: () -> Unit) {
        actionQueue.add(function)
    }

    override fun postTick() {
        while (actionQueue.isNotEmpty()) {
            actionQueue.poll().invoke()
        }
    }

    override fun tick() = Unit

    fun delete() {
        historyRecord.doAction(ActionDelete(selectionManager.selection, this))
    }
}