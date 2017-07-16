package com.cout970.modeler.view.gui.editor.centerpanel

/**
 * Created by cout970 on 2017/07/16.
 */

class ModuleCenterPanel {

    val panel = CenterPanel()
    val layout = CenterPanelLayout(panel)
    val presenter = CenterPanelPresenter(panel, this)
}