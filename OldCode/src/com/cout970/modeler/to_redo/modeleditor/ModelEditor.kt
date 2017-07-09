package com.cout970.modeler.to_redo.modeleditor

import com.cout970.modeler.core.clipboard.ElementClipboard
import com.cout970.modeler.core.clipboard.IClipboard
import com.cout970.modeler.core.clipboard.VertexClipboard
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.core.record.HistoricalRecord
import com.cout970.modeler.core.record.HistoryLog
import com.cout970.modeler.core.record.action.ActionModifyModelShape
import com.cout970.modeler.to_redo.model.Meshes
import com.cout970.modeler.to_redo.model.Model
import com.cout970.modeler.to_redo.modeleditor.tool.EditTool
import com.cout970.modeler.to_redo.selection.SelectionMode
import com.cout970.modeler.util.ITickeable
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import java.util.*

/**
 * Created by cout970 on 2016/11/29.
 */
class ModelEditor(val projectManager: ProjectManager) : ITickeable, IModelProvider {

    private val actionQueue = LinkedList<() -> Unit>()

    override val selectionManager = SelectionManager(this)
    val historyLog = HistoryLog()
    val historyRecord = HistoricalRecord(historyLog, this)
    val editTool = EditTool()

    val clipboard: IClipboard get() = when (selectionManager.selectionMode) {
        SelectionMode.ELEMENT -> elementClipboard
        SelectionMode.EDIT -> vertexClipboard
    }

    val elementClipboard = ElementClipboard(selectionManager, this, historyRecord)
    val vertexClipboard = VertexClipboard(selectionManager, this, historyRecord)

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

    override fun tick() = Unit

    override fun postTick() {
        while (actionQueue.isNotEmpty()) {
            actionQueue.poll().invoke()
        }
    }

    fun addCube(size: IVector3 = vec3Of(8, 8, 8)) {
        val element = Meshes.createCube(size)
        val newModel = editTool.insertElementLeaf(model, element)
        historyRecord.doAction(ActionModifyModelShape(this, newModel))
    }

    fun addPlane() {
        val element = Meshes.createPlane(vec2Of(16))
        val newModel = editTool.insertElementLeaf(model, element)
        historyRecord.doAction(ActionModifyModelShape(this, newModel))
    }
}