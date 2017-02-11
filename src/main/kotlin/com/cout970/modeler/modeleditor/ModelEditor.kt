package com.cout970.modeler.modeleditor

import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.selection.SelectionManager
import com.cout970.modeler.modeleditor.tool.EditTool
import com.cout970.modeler.project.ProjectManager
import com.cout970.modeler.util.ITickeable
import java.util.*

/**
 * Created by cout970 on 2016/11/29.
 */
class ModelEditor(val projectManager: ProjectManager) : ITickeable, IModelProvider {

    private val actionQueue = LinkedList<() -> Unit>()

    override val selectionManager = SelectionManager(this)
    val clipboard = ModelClipboard(this)
    val historyLog = HistoryLog()
    val historyRecord = HistoricalRecord(historyLog, this)
    val inserter = ModelInserter(this)
    val editTool = EditTool()
    val texturizer = ModelTexturizer(this)

    override val model get() = projectManager.project.model
    override var modelNeedRedraw = true

    init {
        projectManager.modelEditor = this
    }

    fun updateModel(newModel: Model) {
        historyLog.onModelChange(newModel, model)
        projectManager.project.model = newModel
        modelNeedRedraw = true
    }

    fun addToQueue(function: () -> Unit) {
        actionQueue.add(function)
    }

    override fun preTick() {
        super.preTick()

    }

    override fun tick() = Unit

    override fun postTick() {
        while (actionQueue.isNotEmpty()) {
            actionQueue.poll().invoke()
        }
    }
}