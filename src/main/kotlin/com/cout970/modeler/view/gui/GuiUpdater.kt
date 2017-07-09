package com.cout970.modeler.view.gui

import com.cout970.glutilities.event.EventFrameBufferSize
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.util.size
import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.gui.editor.LeftPanelUpdater
import com.cout970.modeler.view.gui.editor.RightPanelUpdater
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.context.LeguiContext
import javax.script.ScriptEngineManager


/**
 * Created by cout970 on 2017/05/14.
 */

class GuiUpdater {

    val updaters = listOf(RightPanelUpdater(), LeftPanelUpdater())
    lateinit var leguiContext: LeguiContext
    lateinit var gui: Gui
    val scriptEngine = ScriptEngineManager().getEngineByName("JavaScript")!!

    fun initGui(gui: Gui) {
        this.gui = gui
        updaters.forEach { it.gui = gui }
    }

    fun onModelUpdate(old: IModel, new: IModel) {
        updaters.forEach { it.onModelUpdate(old, new) }
        gui.selector.updateCursorCenter(gui.selectionHandler.getSelection())
    }

    fun onSelectionUpdate(old: ISelection?, new: ISelection?) {
        updaters.forEach { it.onSelectionUpdate(old, new) }
    }

    fun onFramebufferSizeUpdated(event: EventFrameBufferSize): Boolean {
        if (event.height == 0 || event.width == 0) return false
        updateSizes(vec2Of(event.width, event.height))
        return false
    }

    fun updateSizes(newSize: IVector2) {
        gui.root.apply {
            size = newSize.toJoml2f()
            mainPanel?.updateSizes(newSize)
        }
        gui.canvasContainer.layout.updateCanvas()
    }

    fun bindTextInputs(editorPanel: Panel) {
        updaters.forEach { it.bindTextInputs(editorPanel) }
    }

    fun handleScroll(e: EventMouseScroll): Boolean {
        return updaters.any { it.handleScroll(e) }
    }
}