package com.cout970.modeler.gui.editor.leftpanel

/**
 * Created by cout970 on 2017/07/16.
 */

class ModuleLeftPanel {

    val panel = LeftPanel()
    val layout = LayoutLeftPanel(panel)
    val presenter = LeftPanelPresenter(panel, this)
}