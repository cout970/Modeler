package com.cout970.modeler.gui.editor.bottompanel

/**
 * Created by cout970 on 2017/08/26.
 */
class ModuleBottomPanel {

    val panel = BottomPanel()
    val layout = BottomPanelLayout(panel)
    val presenter = BottomPanelPresenter(panel, this)
}