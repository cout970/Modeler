package com.cout970.modeler.view.gui.editor.leftpanel

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.`object`.IObjectCube
import com.cout970.modeler.api.model.selection.IObjectRef
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
import com.cout970.modeler.view.gui.comp.Cache
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

    val model get() = gui.projectManager.model
    val leguiContext: Context get() = gui.guiUpdater.leguiContext
    val editCubePresenter = EditCubePanelPresenter(panel.editCubePanel)

    override fun onModelUpdate(old: IModel, new: IModel) {
        onSelectionUpdate(gui.selectionHandler.getSelection(), gui.selectionHandler.getSelection())
    }

    override fun onSelectionUpdate(old: ISelection?, new: ISelection?) {
        val editCube = panel.editCubePanel
        val model = gui.projectManager.model

        if (new != null && isSelectingOneCube(model, new)) {
            editCubePresenter.showCube(getSelectedCube(model, new)!!)
        } else {
            if (old != null && getSelectedCube(model, old) != null && leguiContext.focusedGui is CTextInput) {
                gui.dispatcher.onEvent("update.template.cube", leguiContext.focusedGui as CTextInput)
            }
            editCube.hide()
        }
    }

    fun getSelectedCube(model: IModel, sel: ISelection): IObjectCube? {
        if (!isSelectingOneCube(model, sel)) return null
        return model.getSelectedObjects(sel).firstOrNull() as? IObjectCube
    }

    fun getSelectedCubeRef(model: IModel, sel: ISelection): IObjectRef? {
        if (!isSelectingOneCube(model, sel)) return null
        return model.getSelectedObjectRefs(sel).firstOrNull()
    }

    fun isSelectingOneCube(model: IModel, new: ISelection): Boolean {
        if (new.selectionType != SelectionType.OBJECT) return false
        if (new.selectionTarget != SelectionTarget.MODEL) return false
        if (new.size != 1) return false
        val selectedObj = model.getSelectedObjects(new).firstOrNull() ?: return false
        return selectedObj is ObjectCube
    }

    fun handleKeyPress(input: CTextInput, event: KeyEvent<*>) {
        if (event.key == Keyboard.KEY_ENTER) {
            gui.dispatcher.onEvent("update.template.cube", input)
        }
    }

    fun handleFocusChange(input: CTextInput, event: FocusEvent<*>) {
        if (event.isFocused) {
            if (input.text.isNotEmpty()) {
                input.startSelectionIndex = 0
                input.endSelectionIndex = input.text.length
            }
        } else {
            gui.dispatcher.onEvent("update.template.cube", input)
        }
    }

    override fun handleScroll(e: EventMouseScroll): Boolean {
        val target = leguiContext.mouseTargetGui
        if (target is CTextInput) {
            val cache = Cache().apply {
                subComponents += target
                cache.put("offset", e.offsetY.toFloat())
            }
            gui.dispatcher.onEvent("update.template.cube", cache)
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