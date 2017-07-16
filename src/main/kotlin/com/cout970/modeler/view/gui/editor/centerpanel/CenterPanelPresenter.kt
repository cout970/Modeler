package com.cout970.modeler.view.gui.editor.centerpanel

import com.cout970.modeler.view.gui.ComponentPresenter

/**
 * Created by cout970 on 2017/07/16.
 */

class CenterPanelPresenter(
        val panel: CenterPanel,
        val moduleCenterPanel: ModuleCenterPanel
) : ComponentPresenter() {

    fun updateBackground() {
        val canvasContainer = gui.canvasContainer

        canvasContainer.layout.updateCanvas()
        panel.canvasPanel.isEnabled = canvasContainer.canvas.isNotEmpty()
    }
}