package com.cout970.modeler.functional.usecases

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.selection.Selection
import com.cout970.modeler.functional.injection.Inject
import com.cout970.modeler.functional.tasks.ITask
import com.cout970.modeler.functional.tasks.TaskNone
import com.cout970.modeler.functional.tasks.TaskUpdateModel
import com.cout970.modeler.util.parent
import com.cout970.modeler.view.gui.editor.rightpanel.RightPanel
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
                it.selection = selection
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