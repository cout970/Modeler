package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.IObjectRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.text
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextInput

/**
 * Created by cout970 on 2017/10/29.
 */
class UpdateObjectName : IUseCase {
    override val key: String = "mode.obj.change.name"

    @Inject lateinit var component: Component
    @Inject lateinit var model: IModel
    @Inject lateinit var selection: Nullable<ISelection>

    override fun createTask(): ITask {
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
}