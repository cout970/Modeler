package com.cout970.modeler.gui.editor.toppanel

/**
 * Created by cout970 on 2017/07/30.
 */
class ModuleTopPanel {

    val panel = TopPanel()
    val layout = LayoutTopPanel(panel)
    val presenter = TopPanelPresenter(panel, this)
}