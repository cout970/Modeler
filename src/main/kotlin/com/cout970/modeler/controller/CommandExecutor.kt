package com.cout970.modeler.controller

import com.cout970.modeler.ProgramSate
import com.cout970.modeler.view.gui.comp.CButton
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

    fun ProgramSate.execute(command: String, comp: Component?) {
        when (command) {
            "project.new" -> newProject(projectManager)
            "cube.template.new" -> actionExecutor.actionTrigger.addCubeTemplate()
            "cube.mesh.new" -> actionExecutor.actionTrigger.addCubeMesh()
            "project.load" -> loadProject(projectManager, exportManager)
            "project.save" -> saveProject(projectManager, exportManager)
            "project.save.as" -> saveProjectAs(projectManager, exportManager)
            "project.export" -> showExportModelPopup(exportManager, actionExecutor, projectManager)
            "project.import" -> showImportModelPopup(exportManager, actionExecutor.historicalRecord, projectManager)
            "model.selection.delete" -> actionExecutor.actionTrigger.delete(gui.selectionHandler.getSelection())
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