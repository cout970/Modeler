package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.tasks.*
import com.cout970.modeler.core.model.Object
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.text
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextInput

/**
 * Created by cout970 on 2017/10/29.
 */

@UseCase("model.obj.change.name")
fun changeObjectName(component: Component, modelAccessor: IModelAccessor): ITask {
    val model = modelAccessor.model
    val selection = modelAccessor.modelSelection

    val name = component.asNullable()
            .filterIsInstance<TextInput>()
            .map { it.text }
            .filter { !it.isBlank() }

    val objRef = selection
            .filter { it.size == 1 }
            .flatMap { it.refs.firstOrNull() }
            .filterIsInstance<IObjectRef>()

    return name.zip(objRef).map { (name, ref) ->

        val newModel = model.modifyObjects({ it == ref }) { _, obj ->
            obj.withName(name)
        }

        TaskUpdateModel(newModel = newModel, oldModel = model) as ITask
    }.getOr(TaskNone)
}

@UseCase("model.obj.join")
fun joinObjects(modelAccessor: IModelAccessor): ITask {
    val selection = modelAccessor.modelSelection.getOrNull() ?: return TaskNone
    if (selection.selectionType != SelectionType.OBJECT || selection.size < 2) return TaskNone

    val model = modelAccessor.model
    val objs = model.getSelectedObjects(selection)
    val objsRefs = model.getSelectedObjectRefs(selection)
    val newObj = Object(
            name = objs.first().name,
            mesh = objs.map { it.mesh }.reduce { acc, mesh -> acc.merge(mesh) },
            material = objs.first().material
    )
    val newModel = model.removeObjects(objsRefs).addObjects(listOf(newObj))

    return TaskChain(listOf(
            TaskUpdateModelSelection(
                    oldSelection = modelAccessor.modelSelection,
                    newSelection = Nullable.castNull()
            ),
            TaskUpdateTextureSelection(
                    oldSelection = modelAccessor.textureSelection,
                    newSelection = Nullable.castNull()
            ),
            TaskUpdateModel(oldModel = model, newModel = newModel)
    ))
}

//@UseCase("model.face.extrude")
//fun extrudeFace(modelAccessor: IModelAccessor): ITask {
//    val selection = modelAccessor.modelSelection.getOrNull() ?: return TaskNone
//    if (selection.selectionType != SelectionType.FACE) return TaskNone
//
//    return TaskNone
//}