package com.cout970.modeler.core.project

import com.cout970.modeler.api.animation.IAnimation
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.model.selection.SelectionHandler
import com.cout970.modeler.util.Nullable

/**
 * Created by cout970 on 2017/09/27.
 */
interface IProgramState {

    val model: IModel
    val animation: IAnimation

    val modelSelectionHandler: SelectionHandler
    val textureSelectionHandler: SelectionHandler

    val modelSelection: Nullable<ISelection> get() = modelSelectionHandler.getSelection()
    val textureSelection: Nullable<ISelection> get() = textureSelectionHandler.getSelection()

    operator fun component1() = model
    operator fun component2() = modelSelection
    operator fun component3() = textureSelection
}

class ProgramState(
        val projectManager: ProjectManager
) : IProgramState {

    override val modelSelectionHandler: SelectionHandler = projectManager.modelSelectionHandler
    override val textureSelectionHandler: SelectionHandler = projectManager.textureSelectionHandler

    override val model: IModel get() = projectManager.model
    override val animation: IAnimation get() = projectManager.animation
    override val modelSelection: Nullable<ISelection> get() = modelSelectionHandler.getSelection()
    override val textureSelection: Nullable<ISelection> get() = textureSelectionHandler.getSelection()
}