package com.cout970.modeler.view.gui

import com.cout970.glutilities.event.EventFrameBufferSize
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.view.Gui
import com.cout970.vector.extensions.vec2Of
import org.liquidengine.legui.component.Container
import org.liquidengine.legui.system.context.Context


/**
 * Created by cout970 on 2017/05/14.
 */

class GuiUpdater {

    lateinit var leguiContext: Context
    lateinit var gui: Gui

    val presenters get() = listOf(
            gui.editorPanel.leftPanelModule.presenter,
            gui.editorPanel.centerPanelModule.presenter,
            gui.editorPanel.rightPanelModule.presenter,
            gui.editorPanel.leftPanelModule.presenter.editCubePresenter
    )

    fun initGui(gui: Gui) {
        this.gui = gui
        presenters.forEach { it.gui = gui }
    }

    fun onModelUpdate(old: IModel, new: IModel) {
        presenters.forEach { it.onModelUpdate(old, new) }
        gui.selector.updateCursorCenter(gui.selectionHandler.getSelection())
    }

    fun onSelectionUpdate(old: ISelection?, new: ISelection?) {
        presenters.forEach { it.onSelectionUpdate(old, new) }
    }

    fun onFramebufferSizeUpdated(event: EventFrameBufferSize): Boolean {
        if (event.height == 0 || event.width == 0) return false
        gui.root.updateSizes(vec2Of(event.width, event.height))
        return false
    }

    fun bindTextInputs(editorPanel: Container<*>) {
        presenters.forEach { it.bindTextInputs(editorPanel) }
    }

    fun handleScroll(e: EventMouseScroll): Boolean {
        return presenters.any { it.handleScroll(e) }
    }
}