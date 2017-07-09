package com.cout970.modeler.controller

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.IObject
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.model.Object
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.material.MaterialNone
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.core.model.transformObjects
import com.cout970.modeler.core.record.action.ActionAddObject
import com.cout970.modeler.core.record.action.ActionChangeObject
import com.cout970.modeler.core.record.action.ActionDelete
import com.cout970.modeler.core.record.action.ActionModifyModelShape
import com.cout970.modeler.core.tool.EditTool
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Quaternion
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/07/09.
 */
class ActionTrigger(val exec: ActionExecutor, val setter: IModelSetter) {

    val model get() = setter.model

    fun addCubeTemplate(size: IVector3 = vec3Of(8, 8, 8)) {
        val obj = ObjectCube(
                name = "Shape${model.objects.size}",
                pos = Vector3.ORIGIN,
                rotation = Quaternion.IDENTITY,
                size = size,
                material = MaterialNone
        )
        exec.enqueueAction(ActionAddObject(setter, model, obj))
    }

    fun addCubeMesh(size: IVector3 = vec3Of(8, 8, 8)) {
        val mesh = MeshFactory.createCube(size, Vector3.ORIGIN)
        val obj = Object("Shape${model.objects.size}", mesh)

        exec.enqueueAction(ActionAddObject(setter, model, obj))
    }

    fun delete(selection: ISelection?) {
        if (selection == null) return
        val newModel = EditTool.delete(model, selection)

        exec.enqueueAction(ActionDelete(setter, newModel))
    }

    fun changeObject(ref: IObjectRef, obj: IObject) {
        val newModel = model.transformObjects(listOf(ref)) { obj }
        exec.enqueueAction(ActionChangeObject(setter, newModel))
    }

    fun loadTmpModel(model: IModel) {
        exec.enqueueAction(ActionModifyModelShape(setter, model))
    }
}