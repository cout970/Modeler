package com.cout970.modeler.controller

import com.cout970.modeler.ProgramSate
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.view.gui.comp.CButton
import com.cout970.modeler.view.gui.editor.RightPanel
import com.cout970.modeler.view.gui.popup.*
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.event.component.MouseClickEvent
import org.liquidengine.legui.listener.LeguiEventListenerMap

/**
 * Created by cout970 on 2017/05/14.
 */

class CommandExecutor {

    lateinit var programState: ProgramSate

    fun execute(command: String, comp: Component? = null) {
        programState.execute(command, comp)
    }

    val ProgramSate.actionTrigger get() = actionExecutor.actionTrigger
    val ProgramSate.selection get() = gui.selectionHandler.getSelection()

    inline fun <reified T> Component?.parent(): T? = this?.parent as? T

    fun ProgramSate.execute(command: String, comp: Component?) {
        when (command) {
            "project.new" -> newProject(projectManager)
            "cube.template.new" -> actionTrigger.addCubeTemplate()
            "cube.mesh.new" -> actionTrigger.addCubeMesh()
            "project.load" -> loadProject(projectManager, exportManager)
            "project.save" -> saveProject(projectManager, exportManager)
            "project.save.as" -> saveProjectAs(projectManager, exportManager)
            "project.export" -> showExportModelPopup(exportManager, actionExecutor, projectManager)
            "project.import" -> showImportModelPopup(exportManager, actionExecutor.historicalRecord, projectManager)
            "model.selection.delete" -> actionTrigger.delete(selection)
            "tree.view.delete.item" -> (comp?.parent as? RightPanel.ListItem)?.let {
                actionTrigger.delete(it.ref)
            }
            "model.undo" -> actionExecutor.historicalRecord.undo()
            "model.redo" -> actionExecutor.historicalRecord.redo()
            "model.selection.copy" -> actionTrigger.copy(selection)
            "model.selection.cut" -> actionTrigger.cut(selection)
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
        }
    }

    fun bindButtons(panel: Panel) {
        panel.components.forEach {
            if (it is CButton) {
                it.leguiEventListeners.setButtonListener { execute(it.command, it) }
            } else if (it is Panel) {
                bindButtons(it)
            }
        }
    }

    fun LeguiEventListenerMap.setButtonListener(function: () -> Unit) {
        getListeners(MouseClickEvent::class.java).toList().forEach {
            removeListener(MouseClickEvent::class.java, it)
        }
        addListener(MouseClickEvent::class.java,
                { if (it.action == MouseClickEvent.MouseClickAction.CLICK) function() })
    }
}