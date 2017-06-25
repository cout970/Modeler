package com.cout970.modeler.view.gui.editor

import com.cout970.modeler.controller.ProjectController
import com.cout970.modeler.util.disable
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.show
import com.cout970.modeler.view.gui.comp.CButton
import com.cout970.modeler.view.gui.comp.CLabel
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.gui.comp.CTextInput
import org.liquidengine.legui.component.TextInput

/**
 * Created by cout970 on 2017/06/09.
 */
class LeftPanel : CPanel() {

    val newProjectPanel = NewProjectPanel()
    val createObjectPanel = CreateObjectPanel()
    val editCubePanel = EditTemplateCubePanel()

    init {
        addComponent(newProjectPanel)
        addComponent(createObjectPanel)
        addComponent(editCubePanel)
        editCubePanel.position.y = 100f
        editCubePanel.disable()
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

    class EditTemplateCubePanel : CPanel(width = 190f, height = 400f) {

        val editCubeLabel = CLabel("Edit cube", 5f, 5f, 180f, 24f)
        val sizePanel = CubeSizePanel()
        val posPanel = CubePosPanel()
        val rotationPanel = CubeRotationPanel()

        init {
            addComponent(editCubeLabel)
            addComponent(sizePanel)
            addComponent(posPanel)
            addComponent(rotationPanel)
            sizePanel.position.y = 30f
            posPanel.position.y = 105f
            rotationPanel.position.y = 180f
        }

        class CubeSizePanel : CPanel(width = 190f, height = 75f) {
            val sizeXLabel = CLabel("Size x", 5f, 5f, 60f, 18f)
            val sizeXInput = CTextInput("cube.size.x", "0.0", 65f, 5f, 110f, 18f)
            val sizeYLabel = CLabel("Size y", 5f, 28f, 60f, 18f)
            val sizeYInput = CTextInput("cube.size.y", "0.0", 65f, 28f, 110f, 18f)
            val sizeZLabel = CLabel("Size z", 5f, 51f, 60f, 18f)
            val sizeZInput = CTextInput("cube.size.z", "0.0", 65f, 51f, 110f, 18f)

            init {
                addComponent(sizeXLabel)
                addComponent(sizeXInput)
                addComponent(sizeYLabel)
                addComponent(sizeYInput)
                addComponent(sizeZLabel)
                addComponent(sizeZInput)
                setBorderless()
            }
        }

        class CubePosPanel : CPanel(width = 190f, height = 75f) {
            val posXLabel = CLabel("Pos. x", 5f, 5f, 60f, 18f)
            val posXInput = CTextInput("cube.pos.x", "0.0", 65f, 5f, 110f, 18f)
            val posYLabel = CLabel("Pos. y", 5f, 28f, 60f, 18f)
            val posYInput = CTextInput("cube.pos.y", "0.0", 65f, 28f, 110f, 18f)
            val posZLabel = CLabel("Pos. z", 5f, 51f, 60f, 18f)
            val posZInput = CTextInput("cube.pos.z", "8", 65f, 51f, 110f, 18f)

            init {
                addComponent(posXLabel)
                addComponent(posXInput)
                addComponent(posYLabel)
                addComponent(posYInput)
                addComponent(posZLabel)
                addComponent(posZInput)
                setBorderless()
            }
        }

        class CubeRotationPanel : CPanel(width = 190f, height = 75f) {
            val rotXLabel = CLabel("Rot. x", 5f, 5f, 60f, 18f)
            val rotXInput = CTextInput("cube.rot.x", "0", 65f, 5f, 110f, 18f)
            val rotYLabel = CLabel("Rot. y", 5f, 28f, 60f, 18f)
            val rotYInput = CTextInput("cube.rot.y", "0", 65f, 28f, 110f, 18f)
            val rotZLabel = CLabel("Rot. z", 5f, 51f, 60f, 18f)
            val rotZInput = CTextInput("cube.rot.z", "0", 65f, 51f, 110f, 18f)

            init {
                addComponent(rotXLabel)
                addComponent(rotXInput)
                addComponent(rotYLabel)
                addComponent(rotYInput)
                addComponent(rotZLabel)
                addComponent(rotZInput)
                setBorderless()
            }
        }
    }

    fun refresh(projectController: ProjectController) {
        if (projectController.project.creationTime == -1L) {
            newProjectPanel.show()
            createObjectPanel.hide()
            editCubePanel.hide()
        } else {
            newProjectPanel.hide()
            createObjectPanel.show()
            editCubePanel.show()
        }
    }
}