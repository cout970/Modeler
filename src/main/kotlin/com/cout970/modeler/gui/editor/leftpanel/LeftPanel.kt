package com.cout970.modeler.gui.editor.leftpanel

import com.cout970.modeler.gui.comp.CPanel
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.editor.leftpanel.editcubepanel.EditCubePanel
import com.cout970.modeler.util.hide

/**
 * Created by cout970 on 2017/06/09.
 */
class LeftPanel : CPanel() {

    val editCubePanel = EditCubePanel()

    init {
        add(editCubePanel)
        editCubePanel.position.y = 28f
        editCubePanel.hide()
        setBorderless()
    }
}