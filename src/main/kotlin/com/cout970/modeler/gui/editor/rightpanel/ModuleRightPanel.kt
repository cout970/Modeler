package com.cout970.modeler.gui.editor.rightpanel

/**
 * Created by cout970 on 2017/07/16.
 */

class ModuleRightPanel {

    val panel = RightPanel()
    val layout = LayoutRightPanel(panel)
    val presenter = RightPanelPresenter(panel, this)
}

