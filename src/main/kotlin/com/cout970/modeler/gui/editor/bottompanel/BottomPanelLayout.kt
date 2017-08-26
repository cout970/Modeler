package com.cout970.modeler.gui.editor.bottompanel

import com.cout970.modeler.gui.comp.module.ILayout
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/08/26.
 */
class BottomPanelLayout(val panel: BottomPanel) : ILayout {

    override fun rescale() {
        panel.buttonPanel.size = Vector2f(panel.size.x, 16f)
        panel.scrollBar.size = Vector2f(panel.size.x, 16f)
        panel.scrollBar.position = Vector2f(0f, panel.size.y - 16f)
        panel.timelinePanel.size = Vector2f(panel.size.x, panel.size.y - 16f - 16f)
        panel.timelinePanel.position = Vector2f(0f, 16f)
    }
}