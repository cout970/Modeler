package com.cout970.modeler.controller

import com.cout970.modeler.ProgramState
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.core.export.ImportFormat
import com.cout970.modeler.core.export.ImportProperties
import com.cout970.modeler.functional.tasks.TaskImportModel
import com.cout970.modeler.util.size
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.gui.comp.CButton
import com.cout970.modeler.view.gui.editor.EditorPanel
import com.cout970.modeler.view.gui.editor.rightpanel.RightPanel
import com.cout970.modeler.view.gui.popup.importTexture
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Container
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.listener.ListenerMap

/**
 * Created by cout970 on 2017/05/14.
 */

class CommandExecutor {

    lateinit var programState: ProgramState

    val ProgramState.actionTrigger get() = actionExecutor.actionTrigger
    val ProgramState.selection get() = gui.selectionHandler.getSelection()

    fun execute(command: String, comp: Component? = null) {
        programState.execute(command, comp)
    }

    fun ProgramState.execute(command: String, comp: Component?) {
        when (command) {
            "model.selection.delete" -> actionTrigger.delete(selection, gui.selectionHandler)
            "tree.view.delete.item" -> comp.parent<RightPanel.ListItem>()?.let {
                actionTrigger.delete(it.ref, gui.selectionHandler)
            }
            "model.selection.copy" -> actionTrigger.copy(selection)
            "model.selection.cut" -> actionTrigger.cut(selection, gui.selectionHandler)
            "model.selection.paste" -> actionTrigger.paste()
            "tree.view.hide.item" -> {
                actionTrigger.modifyVisibility(comp.parent<RightPanel.ListItem>()!!.ref, false)
            }
            "tree.view.show.item" -> {
                actionTrigger.modifyVisibility(comp.parent<RightPanel.ListItem>()!!.ref, true)
            }
            "view.switch.ortho" -> gui.canvasContainer.selectedCanvas?.let {
                it.cameraHandler.setOrtho(it.cameraHandler.camera.perspective)
            }
            "view.set.texture.mode" -> gui.canvasContainer.selectedCanvas?.let { it.viewMode = SelectionTarget.TEXTURE }
            "view.set.model.mode" -> gui.canvasContainer.selectedCanvas?.let { it.viewMode = SelectionTarget.MODEL }
            "material.view.apply" -> comp.parent<RightPanel.MaterialListItem>()?.let {
                actionTrigger.applyMaterial(selection, it.ref)
            }
            "material.view.load" -> comp.parent<RightPanel.MaterialListItem>()?.let {
                importTexture(projectManager, it.ref)
                projectManager.loadedMaterials.forEach { it.loadTexture(resourceLoader) }
            }
            "debug.reset.gui" -> {
                gui.editorPanel = EditorPanel()
                gui.root.mainPanel = gui.editorPanel
                gui.guiUpdater.initGui(gui)
                gui.root.updateSizes(gui.root.size.toIVector())
            }
            "debug.import.model" -> {
                val prop = ImportProperties(
                        "./model.tbl",
                        ImportFormat.TBL,
                        false
                )
                taskHistory.processTask(TaskImportModel(projectManager.model, prop))
            }
        }
    }

    inline fun <reified T> Component?.parent(): T? = this?.parent as? T

    fun bindButtons(panel: Container<*>) {
        panel.childs.forEach {
            if (it is CButton) {
                it.listenerMap.setButtonListener { execute(it.command, it) }
            } else if (it is Container<*>) {
                bindButtons(it)
            }
        }
    }

    fun ListenerMap.setButtonListener(function: () -> Unit) {
        getListeners(MouseClickEvent::class.java).toList().forEach {
            removeListener(MouseClickEvent::class.java, it)
        }
        addListener(MouseClickEvent::class.java,
                { if (it.action == MouseClickEvent.MouseClickAction.CLICK) function() })
    }
}