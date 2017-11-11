package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.controller.tasks.TaskUpdateModelSelection
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.toNullable
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/20.
 */

class DeleteItem : IUseCase {

    override val key: String = "tree.view.delete.item"

    @Inject lateinit var component: Component
    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        return component.asNullable()
                .map { it.metadata["ref"] }
                .flatMap { it as? IObjectRef }
                .map(this::delete)
                .getOr(TaskNone)
    }

    fun delete(ref: IObjectRef): ITask {
        val selection = Selection(
                SelectionTarget.MODEL,
                SelectionType.OBJECT,
                listOf(ref as IRef)
        )

        return DeleteSelected().also {
            it.selection = selection.asNullable()
            it.model = model
        }.createTask()
    }
}

class HideItem : IUseCase {

    override val key: String = "tree.view.hide.item"

    @Inject lateinit var component: Component
    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        return component.asNullable()
                .map { it.metadata["ref"] }
                .flatMap { it as? IObjectRef }
                .map { ref ->
                    val newModel = model.setVisible(ref, false)
                    TaskUpdateModel(oldModel = model, newModel = newModel) as ITask
                }
                .getOr(TaskNone)
    }
}

class ShowItem : IUseCase {

    override val key: String = "tree.view.show.item"

    @Inject lateinit var component: Component
    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        return component.asNullable()
                .map { it.metadata["ref"] }
                .flatMap { it as? IObjectRef }
                .map { ref ->
                    val newModel = model.setVisible(ref, true)
                    TaskUpdateModel(oldModel = model, newModel = newModel) as ITask
                }
                .getOr(TaskNone)
    }
}

class SelectModelPart : IUseCase {

    override val key: String = "tree.view.select"

    @Inject lateinit var component: Component
    @Inject lateinit var input: IInput
    @Inject lateinit var selection: Nullable<ISelection>
    @Inject lateinit var modelAccessor: IModelAccessor

    override fun createTask(): ITask {
        return component.asNullable()
                .map { it.metadata["ref"] }
                .flatMap { it as? IObjectRef }
                .map(this::select)
                .getOr(TaskNone)
    }

    fun select(ref: IObjectRef): ITask {
        val multiSelection = Config.keyBindings.multipleSelection.check(input)
        val sel = modelAccessor.modelSelectionHandler.updateSelection(selection, multiSelection, ref)
        return TaskUpdateModelSelection(
                oldSelection = selection,
                newSelection = sel
        )
    }
}

class ToggleVisibility : IUseCase {

    override val key: String = "model.toggle.visibility"

    @Inject lateinit var selection: Nullable<ISelection>
    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        return selection.toNullable()
                .map { it to model.getSelectedObjectRefs(it).first() }
                .map(this::toggle)
                .getOr(TaskNone)
    }

    fun toggle(pair: Pair<ISelection, IObjectRef>): ITask {
        val (sel, ref) = pair
        var newModel = model
        val target = !model.isVisible(ref)

        model.getSelectedObjectRefs(sel).forEach {
            newModel = newModel.setVisible(it, target)
        }

        return TaskUpdateModel(model, newModel)
    }
}