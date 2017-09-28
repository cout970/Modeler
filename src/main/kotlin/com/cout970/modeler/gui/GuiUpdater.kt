package com.cout970.modeler.gui

import com.cout970.glutilities.event.EventFrameBufferSize
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.gui.react.event.EventMaterialUpdate
import com.cout970.modeler.gui.react.event.EventModelUpdate
import com.cout970.modeler.gui.react.event.EventSelectionUpdate
import com.cout970.modeler.util.asNullable
import com.cout970.modeler.util.getListeners
import com.cout970.vector.extensions.vec2Of
import org.liquidengine.legui.component.Container
import org.liquidengine.legui.system.context.Context


/**
 * Created by cout970 on 2017/05/14.
 */

class GuiUpdater {

    lateinit var leguiContext: Context
    lateinit var gui: Gui

    val presenters
        get() = listOf<ComponentPresenter>(
    )

    fun initGui(gui: Gui) {
        this.gui = gui
        presenters.forEach { it.gui = gui }
    }

    fun onModelUpdate(old: IModel, new: IModel) {
        gui.editorPanel.reactBase.getListeners<EventModelUpdate>().forEach { (comp, listener) ->
            listener.process(EventModelUpdate(comp, leguiContext, gui.root, new, old))
        }
        presenters.forEach { it.onModelUpdate(old, new) }
    }

    fun onSelectionUpdate(old: ISelection?, new: ISelection?) {
        gui.editorPanel.reactBase.getListeners<EventSelectionUpdate>().forEach { (comp, listener) ->
            listener.process(EventSelectionUpdate(comp, leguiContext, gui.root, new.asNullable(), old.asNullable()))
        }
        presenters.forEach { it.onSelectionUpdate(old, new) }
    }

    fun onMaterialUpdate(old: IMaterial?, new: IMaterial?) {
        gui.editorPanel.reactBase.getListeners<EventMaterialUpdate>().forEach { (comp, listener) ->
            listener.process(EventMaterialUpdate(comp, leguiContext, gui.root, new.asNullable(), old.asNullable()))
        }
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