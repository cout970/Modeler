package com.cout970.modeler.gui

import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.model.selection.SelectionHandler
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.util.Nullable

/**
 * Created by cout970 on 2017/09/27.
 */
interface IModelAccessor {

    val model: IModel

    val modelSelectionHandler: SelectionHandler
    val textureSelectionHandler: SelectionHandler
    val modelSelection: Nullable<ISelection> get() = modelSelectionHandler.getSelection()
    val textureSelection: Nullable<ISelection> get() = textureSelectionHandler.getSelection()
}

class ModelAccessor(
        val projectManager: ProjectManager,
        override val modelSelectionHandler: SelectionHandler,
        override val textureSelectionHandler: SelectionHandler
) : IModelAccessor {

    override val model: IModel get() = projectManager.model
    override val modelSelection: Nullable<ISelection> get() = modelSelectionHandler.getSelection()
    override val textureSelection: Nullable<ISelection> get() = textureSelectionHandler.getSelection()
}