package com.cout970.modeler.controller

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObject
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.record.action.*
import com.cout970.modeler.core.tool.EditTool

/**
 * Created by cout970 on 2017/07/09.
 */
class ActionTrigger(val exec: ActionExecutor, val setter: IModelSetter) {

    val model get() = setter.model


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