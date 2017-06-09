package com.cout970.modeler.controller

import com.cout970.modeler.ProgramSate
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.show
import com.cout970.modeler.view.gui.comp.CButton
import com.cout970.modeler.view.gui.popup.loadProject
import com.cout970.modeler.view.gui.popup.newProject
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.event.component.MouseClickEvent
import org.liquidengine.legui.listener.LeguiEventListenerMap

/**
 * Created by cout970 on 2017/05/14.
 */

class CommandExecutor {

    lateinit var programState: ProgramSate

    fun execute(command: String) {
        programState.apply {
            when (command) {
                "project.new" -> {
                    val panel = guiState.editorPanel.leftPanel
                    val res = newProject(projectController, panel.newProjectPanel.projectNameInput.textState.text)
                    if (res) {
                        panel.newProjectPanel.hide()
                        panel.createObjectPanel.show()
                    }
                }
                "cube.template.new" -> modelTransformer.addCubeTemplate()
                "cube.mesh.new" -> modelTransformer.addCubeMesh()
                "project.load" -> loadProject(projectController, exportManager)
            }
        }
    }

    fun bindButtons(panel: Panel) {
        panel.components.forEach {
            if (it is CButton) {
                it.leguiEventListeners.setButtonListener { execute(it.command) }
            } else if (it is Panel) {
                bindButtons(it)
            }
        }
    }
}

private fun LeguiEventListenerMap.setButtonListener(function: () -> Unit) {
    getListeners(MouseClickEvent::class.java).toList().forEach {
        removeListener(MouseClickEvent::class.java, it)
    }
    addListener(MouseClickEvent::class.java, { if (it.action == MouseClickEvent.MouseClickAction.CLICK) function() })
}
