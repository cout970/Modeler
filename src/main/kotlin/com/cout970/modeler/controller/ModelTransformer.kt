package com.cout970.modeler.controller

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.model.Object
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.core.record.HistoricalRecord
import com.cout970.modeler.core.record.HistoryLog
import com.cout970.modeler.core.record.action.ActionAddObject
import com.cout970.modeler.core.record.action.ActionDelete
import com.cout970.modeler.core.tool.EditTool
import com.cout970.modeler.util.IFutureExecutor
import com.cout970.modeler.util.ITickeable
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Quaternion
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of
import java.util.*

/**
 * Created by cout970 on 2017/06/09.
 */
class ModelTransformer(val projectController: ProjectController) : ITickeable, IFutureExecutor {

    var model: IModel get() = projectController.project.model
        set(value) = projectController.updateModel(value)

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

    fun addCubeTemplate(size: IVector3 = vec3Of(8, 8, 8)) {
        val obj = ObjectCube(
                name = "Shape${model.objects.size}",
                pos = Vector3.ORIGIN,
                rotation = Quaternion.IDENTITY,
                size = size,
                material = MaterialNone
        )
        historicalRecord.doAction(ActionAddObject(this, model, obj))
    }

    fun addCubeMesh(size: IVector3 = vec3Of(8, 8, 8)) {
        val mesh = MeshFactory.createCube(size, Vector3.ORIGIN)
        val obj = Object("Shape${model.objects.size}", mesh)
        historicalRecord.doAction(ActionAddObject(this, model, obj))
    }

    fun delete(selection: ISelection) {
        historicalRecord.doAction(ActionDelete(this, EditTool.delete(model, selection)))
    }
}