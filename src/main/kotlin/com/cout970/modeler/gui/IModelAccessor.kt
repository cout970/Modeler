package com.cout970.modeler.gui

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.controller.SelectionHandler
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.util.Nullable
import com.cout970.modeler.util.toNullable

/**
 * Created by cout970 on 2017/09/27.
 */
interface IModelAccessor {

    val model: IModel
    val selection: Nullable<ISelection>
}

class ModelAccessor(val projectManager: ProjectManager, val selectionHandler: SelectionHandler) : IModelAccessor {

    override val model: IModel get() = projectManager.model
    override val selection: Nullable<ISelection> get() = selectionHandler.getModelSelection().toNullable()
}