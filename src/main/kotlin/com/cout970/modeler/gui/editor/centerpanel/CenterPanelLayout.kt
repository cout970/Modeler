package com.cout970.modeler.gui.editor.centerpanel

import com.cout970.modeler.gui.comp.module.ILayout
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.show
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/07/16.
 */
class CenterPanelLayout(val panel: CenterPanel) : ILayout {

    override fun rescale() {
        panel.canvasPanel.let { canvasPanel ->
            canvasPanel.size = Vector2f(panel.size.x, panel.size.y)
            canvasPanel.position = Vector2f()
        }
        panel.backgroundPanel.let { canvasPanel ->
            canvasPanel.size = Vector2f(panel.size.x, panel.size.y)
            canvasPanel.position = Vector2f()
        }

        if (panel.canvasPanel.isEnabled) {
            panel.canvasPanel.show()
            panel.backgroundPanel.hide()
        } else {
            panel.canvasPanel.hide()
            panel.backgroundPanel.apply {
                show()
                backgroundLabelsKey.forEachIndexed { index, label ->
                    label.setPosition(panel.size.x / 3f, (index - backgroundLabelsKey.size / 2) * 45f)
                    label.setSize(panel.size.x - label.position.x, panel.size.y - label.position.y)
                }
                backgroundLabelsValue.forEachIndexed { index, label ->
                    label.setPosition(panel.size.x / 3f + 150f, (index - backgroundLabelsValue.size / 2) * 45f)
                    label.setSize(panel.size.x - label.position.x, panel.size.y - label.position.y)
                }
            }
        }
    }
}