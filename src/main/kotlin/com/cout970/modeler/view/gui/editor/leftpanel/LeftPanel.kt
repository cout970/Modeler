package com.cout970.modeler.view.gui.editor.leftpanel

import com.cout970.modeler.util.hide
import com.cout970.modeler.view.GuiResources
import com.cout970.modeler.view.gui.comp.CButton
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.gui.editor.leftpanel.editcubepanel.EditCubePanel
import org.joml.Vector2f
import org.liquidengine.legui.icon.ImageIcon

/**
 * Created by cout970 on 2017/06/09.
 */
class LeftPanel : CPanel() {

    val projectControlPanel = ProjectControlPanel()
    val editCubePanel = EditCubePanel()

    init {
        add(projectControlPanel)
        add(editCubePanel)
        editCubePanel.position.y = 36f
        editCubePanel.hide()
    }

    class ProjectControlPanel : CPanel(width = 190f, height = 44f) {
        val newProjectButton = CButton("", 7f, 6f, 32f, 32f, "project.new")
        val loadProjectButton = CButton("", 43f, 6f, 32f, 32f, "project.load")
        val saveProjectButton = CButton("", 79f, 6f, 32f, 32f, "project.save")
        val saveAsProjectButton = CButton("", 115f, 6f, 32f, 32f, "project.save.as")
        val editProjectButton = CButton("", 151f, 6f, 32f, 32f, "project.edit")

        init {
            add(newProjectButton)
            add(loadProjectButton)
            add(saveProjectButton)
            add(saveAsProjectButton)
            add(editProjectButton)

            newProjectButton.setTooltip("New Project")
            loadProjectButton.setTooltip("Load Project")
            saveProjectButton.setTooltip("Save Project")
            saveAsProjectButton.setTooltip("Save Project As")
            editProjectButton.setTooltip("Edit Project")
        }

        override fun loadResources(resources: GuiResources) {
            newProjectButton.setImage(ImageIcon(resources.newProjectIcon).also { it.size = Vector2f(32f) })
            loadProjectButton.setImage(ImageIcon(resources.loadProjectCubeIcon).also { it.size = Vector2f(32f) })
            saveProjectButton.setImage(ImageIcon(resources.saveProjectIcon).also { it.size = Vector2f(32f) })
            saveAsProjectButton.setImage(ImageIcon(resources.saveAsProjectIcon).also { it.size = Vector2f(32f) })
            editProjectButton.setImage(ImageIcon(resources.editProjectIcon).also { it.size = Vector2f(32f) })
        }
    }
}