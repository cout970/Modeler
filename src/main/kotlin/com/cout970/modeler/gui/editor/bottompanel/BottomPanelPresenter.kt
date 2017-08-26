package com.cout970.modeler.gui.editor.bottompanel

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.gui.ComponentPresenter
import org.liquidengine.legui.system.context.Context

/**
 * Created by cout970 on 2017/08/26.
 */
class BottomPanelPresenter(
        val panel: BottomPanel,
        val moduleBottomPanel: ModuleBottomPanel
) : ComponentPresenter() {

    val leguiContext: Context get() = gui.guiUpdater.leguiContext

    override fun handleScroll(e: EventMouseScroll): Boolean {
        if (leguiContext.mouseTargetGui is BottomPanel.TimelinePanel) {
            if (gui.input.keyboard.isKeyPressed(Keyboard.KEY_LEFT_SHIFT)) {
                panel.timelinePanel.offset += (e.offsetY.toFloat() * 8)
            } else {
                panel.timelinePanel.scale += (e.offsetY.toFloat() * 0.25f)
            }
            return true
        }
        return false
    }
}