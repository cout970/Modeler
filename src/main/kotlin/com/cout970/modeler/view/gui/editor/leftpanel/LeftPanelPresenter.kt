package com.cout970.modeler.view.gui.editor.leftpanel

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.model.ObjectCube
import com.cout970.modeler.core.model.getSelectedObjectRefs
import com.cout970.modeler.core.model.getSelectedObjects
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.text
import com.cout970.modeler.view.gui.ComponentPresenter
import com.cout970.modeler.view.gui.comp.CTextInput
import com.cout970.modeler.view.gui.editor.leftpanel.editcubepanel.EditCubePanelPresenter
import org.liquidengine.legui.component.Container
import org.liquidengine.legui.event.FocusEvent
import org.liquidengine.legui.event.KeyEvent
import org.liquidengine.legui.listener.ListenerMap
import org.liquidengine.legui.system.context.Context
import org.lwjgl.glfw.GLFW

/**
 * Created by cout970 on 2017/07/08.
 */

class LeftPanelPresenter(
        val panel: LeftPanel,
        val module: ModuleLeftPanel
) : ComponentPresenter() {

    val model get() = gui.actionExecutor.model
    val leguiContext: Context get() = gui.guiUpdater.leguiContext
    val editCubePresenter = EditCubePanelPresenter(panel.editCubePanel)

    override fun onModelUpdate(old: IModel, new: IModel) {
        onSelectionUpdate(null, gui.selectionHandler.getSelection())
    }

    override fun onSelectionUpdate(old: ISelection?, new: ISelection?) {
        val editCube = panel.editCubePanel
        val model = gui.actionExecutor.model

        if (new != null && isSelectingOneCube(new)) {
            editCubePresenter.showCube(model.getSelectedObjectRefs(new).first())
        } else {
            if (editCubePresenter.monitoredCube != null && leguiContext.focusedGui is CTextInput) {
                editCubePresenter.onTextInput(leguiContext.focusedGui as CTextInput)
            }
            editCubePresenter.monitoredCube = null
            editCube.hide()
        }
    }

    fun isSelectingOneCube(new: ISelection): Boolean {
        if (new.selectionType != SelectionType.OBJECT) return false
        if (new.selectionTarget != SelectionTarget.MODEL) return false
        if (new.size != 1) return false
        val selectedObj = model.getSelectedObjects(new).firstOrNull() ?: return false
        return selectedObj is ObjectCube
    }

    fun handleKeyPress(input: CTextInput, event: KeyEvent<*>) {
        if (event.key == Keyboard.KEY_ENTER) {
            editCubePresenter.onTextInput(input)
        }
    }

    fun handleFocusChange(input: CTextInput, event: FocusEvent<*>) {
        if (event.isFocused) {
            if (input.text.isNotEmpty()) {
                input.startSelectionIndex = 0
                input.endSelectionIndex = input.text.length
            }
        } else {
            editCubePresenter.onTextInput(input)
        }
    }

    override fun handleScroll(e: EventMouseScroll): Boolean {
        val target = leguiContext.mouseTargetGui
        if (target is CTextInput) {
            editCubePresenter.onTextInput(target, e.offsetY.toFloat())
        }
        return false
    }

    override fun bindTextInputs(panel: Container<*>) {
        panel.childs.forEach {
            if (it is CTextInput) {
                it.listenerMap.setKeyboardListener { e -> handleKeyPress(it, e) }
                it.listenerMap.setFocusListener { e -> handleFocusChange(it, e) }
            } else if (it is Container<*>) {
                bindTextInputs(it)
            }
        }
    }

    fun ListenerMap.setKeyboardListener(function: (KeyEvent<*>) -> Unit) {
        getListeners(KeyEvent::class.java).toList().forEach {
            removeListener(KeyEvent::class.java, it)
        }
        addListener(KeyEvent::class.java, { if (it.action == GLFW.GLFW_PRESS) function(it) })
    }

    fun ListenerMap.setFocusListener(function: (FocusEvent<*>) -> Unit) {
        getListeners(FocusEvent::class.java).toList().forEach {
            removeListener(FocusEvent::class.java, it)
        }
        addListener(FocusEvent::class.java, { function(it) })
    }
}