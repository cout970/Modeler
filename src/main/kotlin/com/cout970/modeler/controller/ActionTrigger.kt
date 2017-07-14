package com.cout970.modeler.controller

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.Object
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.record.action.*
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
                material = MaterialRef(-1)
        )
        exec.enqueueAction(ActionAddObject(setter, model, obj))
    }

    fun addCubeMesh(size: IVector3 = vec3Of(8, 8, 8)) {
        val mesh = MeshFactory.createCube(size, Vector3.ORIGIN)
        val obj = Object("Shape${model.objects.size}", mesh)

        exec.enqueueAction(ActionAddObject(setter, model, obj))
    }

    fun delete(ref: IObjectRef, handler: SelectionHandler) {
        delete(Selection(
                SelectionTarget.MODEL,
                SelectionType.OBJECT,
                listOf(ref)
        ), handler)
    }

    fun delete(selection: ISelection?, handler: SelectionHandler) {
        if (selection == null) return
        val newModel = EditTool.delete(model, selection)

        exec.enqueueAction(ActionDelete(setter, newModel, handler))
    }

    fun changeObject(ref: IObjectRef, obj: IObject) {
        val newModel = model.modifyObjects(listOf(ref)) { _, _ -> obj }
        exec.enqueueAction(ActionChangeObject(setter, newModel))
    }

    fun loadTmpModel(model: IModel) {
        exec.enqueueAction(ActionModifyModelShape(setter, model))
    }

    fun copy(selection: ISelection?) {
        if (selection != null) {
            exec.projectManager.clipboard = model to selection
        }
    }

    fun cut(selection: ISelection?, handler: SelectionHandler) {
        copy(selection)
        delete(selection, handler)
    }

    fun paste() {
        val clipboard = exec.projectManager.clipboard ?: return
        val (oldModel, selection) = clipboard

        if (selection.selectionTarget == SelectionTarget.MODEL && selection.selectionType == SelectionType.OBJECT) {
            val selectedObjects = oldModel.getSelectedObjects(selection)
            val newModel = model.addObjects(selectedObjects)
            exec.enqueueAction(ActionPaste(setter, newModel))
        }
    }

    fun modifyVisibility(ref: IObjectRef, value: Boolean) {
        val newModel = model.setVisible(ref, value)
        exec.enqueueAction(ActionUpdateVisibility(setter, newModel))
    }

    fun applyMaterial(selection: ISelection?, materialRef: IMaterialRef) {
        if (selection == null) return
        val newModel = model.modifyObjects(model.getSelectedObjectRefs(selection)) { _, obj ->
            obj.transformer.withMaterial(obj, materialRef)
        }
        exec.enqueueAction(ActionUpdateMaterial(setter, newModel))
    }
}