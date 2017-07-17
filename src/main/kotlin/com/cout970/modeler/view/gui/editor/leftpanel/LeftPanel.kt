package com.cout970.modeler.view.gui.editor.leftpanel

import com.cout970.modeler.util.hide
import com.cout970.modeler.view.gui.comp.CButton
import com.cout970.modeler.view.gui.comp.CLabel
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.gui.editor.leftpanel.editcubepanel.EditCubePanel

/**
 * Created by cout970 on 2017/06/09.
 */
class LeftPanel : CPanel() {

    val createObjectPanel = CreateObjectPanel()
    val editCubePanel = EditCubePanel()

    init {
        add(createObjectPanel)
        add(editCubePanel)
        editCubePanel.position.y = 100f
        editCubePanel.hide()
    }

    class CreateObjectPanel : CPanel(width = 190f, height = 100f) {

        val createObjectLabel = CLabel("Create Object", 5f, 5f, 180f, 24f)
        val createTemplateCube = CButton("Create template cube", 5f, 35f, 180f, 24f, "cube.template.new")
        val createMeshCube = CButton("Create mesh cube", 5f, 65f, 180f, 24f, "cube.mesh.new")

        init {
            add(createObjectLabel)
            add(createTemplateCube)
            add(createMeshCube)
        }
    }
}