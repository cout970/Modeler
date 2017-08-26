package com.cout970.modeler.controller.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.controller.SelectionHandler
import com.cout970.modeler.controller.injection.Inject
import com.cout970.modeler.controller.tasks.ITask
import com.cout970.modeler.controller.tasks.TaskNone
import com.cout970.modeler.controller.tasks.TaskUpdateModel
import com.cout970.modeler.controller.tasks.TaskUpdateSelection
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.gui.editor.rightpanel.RightPanel
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.parent
import org.funktionale.option.Option
import org.funktionale.option.toOption
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/07/20.
 */

class DeleteItem : IUseCase {

    override val key: String = "tree.view.delete.item"

    @Inject lateinit var component: Component
    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        component.parent<RightPanel.ListItem>()?.let { item ->
            val selection = Selection(
                    SelectionTarget.MODEL,
                    SelectionType.OBJECT,
                    listOf(item.ref)
            )
            return DeleteSelected().also {
                it.selection = selection.toOption()
                it.model = model
            }.createTask()
        }
        return TaskNone
    }
}

class HideItem : IUseCase {

    override val key: String = "tree.view.hide.item"

    @Inject lateinit var component: Component
    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        component.parent<RightPanel.ListItem>()?.let { item ->
            val newModel = model.setVisible(item.ref, false)
            return TaskUpdateModel(oldModel = model, newModel = newModel)
        }
        return TaskNone
    }
}

class ShowItem : IUseCase {

    override val key: String = "tree.view.show.item"

    @Inject lateinit var component: Component
    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        component.parent<RightPanel.ListItem>()?.let { item ->
            val newModel = model.setVisible(item.ref, true)
            return TaskUpdateModel(oldModel = model, newModel = newModel)
        }
        return TaskNone
    }
}

class SelectModelPart : IUseCase {

    override val key: String = "tree.view.select"

    @Inject lateinit var component: Component
    @Inject lateinit var input: IInput
    @Inject lateinit var selection: Option<ISelection>
    @Inject lateinit var selectionHandler: SelectionHandler

    override fun createTask(): ITask {
        component.parent<RightPanel.ListItem>()?.let { item ->
            val multiSelection = Config.keyBindings.multipleSelection.check(input)
            val sel = selectionHandler.makeSelection(selection, multiSelection, item.ref)
            return TaskUpdateSelection(
                    oldSelection = selection.orNull(),
                    newSelection = sel.orNull()
            )
        }
        return TaskNone
    }
}

class ToggleVisibility : IUseCase {

    override val key: String = "model.toggle.visibility"

    @Inject lateinit var selection: Option<ISelection>
    @Inject lateinit var model: IModel

    override fun createTask(): ITask {
        val sel = selection.orNull() ?: return TaskNone
        val first = model.getSelectedObjectRefs(sel).first() ?: return TaskNone
        var newModel = model
        val target = !model.isVisible(first)

        model.getSelectedObjectRefs(sel).forEach {
            newModel = newModel.setVisible(it, target)
        }

        return TaskUpdateModel(model, newModel)
    }
}