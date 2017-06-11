package com.cout970.modeler.view.gui.editor

import com.cout970.modeler.controller.ProjectController
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.show
import com.cout970.modeler.view.gui.comp.CButton
import com.cout970.modeler.view.gui.comp.CLabel
import com.cout970.modeler.view.gui.comp.CPanel
import org.liquidengine.legui.component.TextInput

/**
 * Created by cout970 on 2017/06/09.
 */
class LeftPanel : CPanel() {

    val newProjectPanel = NewProjectPanel()
    val createObjectPanel = CreateObjectPanel()

    init {
        addComponent(newProjectPanel)
        addComponent(createObjectPanel)
    }

    class CreateObjectPanel : CPanel(width = 190f, height = 100f) {

        val createObjectLabel = CLabel("Create Object", 5f, 5f, 180f, 24f)
        val createTemplateCube = CButton("Create template cube", 5f, 35f, 180f, 24f, "cube.template.new")
        val createMeshCube = CButton("Create mesh cube", 5f, 65f, 180f, 24f, "cube.mesh.new")

        init {
            addComponent(createObjectLabel)
            addComponent(createTemplateCube)
            addComponent(createMeshCube)
        }
    }

    class NewProjectPanel : CPanel(width = 190f, height = 125f) {

        val projectNameLabel = CLabel("Project Name", 5f, 5f, 180f, 24f)
        val projectNameInput = TextInput("", 5f, 35f, 180f, 24f)
        val projectCreateButton = CButton("Create", 5f, 65f, 180f, 24f, "project.new")
        val projectLoadButton = CButton("Load Project", 5f, 95f, 180f, 24f, "project.load")

        init {
            addComponent(projectNameLabel)
            addComponent(projectNameInput)
            addComponent(projectCreateButton)
            addComponent(projectLoadButton)
        }
    }

    fun refresh(projectController: ProjectController) {
        if (projectController.project.creationTime == -1L) {
            newProjectPanel.show()
            createObjectPanel.hide()
        } else {
            newProjectPanel.hide()
            createObjectPanel.show()
        }
    }
}