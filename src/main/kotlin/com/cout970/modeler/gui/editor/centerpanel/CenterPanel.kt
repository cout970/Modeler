package com.cout970.modeler.gui.editor.centerpanel

import com.cout970.modeler.gui.comp.CPanel
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import org.liquidengine.legui.color.ColorConstants
import org.liquidengine.legui.component.Label

/**
 * Created by cout970 on 2017/07/16.
 */

class CenterPanel : CPanel() {
    val canvasPanel = CPanel()
    val backgroundPanel = CanvasBackgroundPanel()

    init {
        add(canvasPanel)
        add(backgroundPanel)
        setTransparent()
        setBorderless()
        backgroundPanel.setBorderless()
        canvasPanel.setBorderless()
        canvasPanel.setTransparent()
    }

    class CanvasBackgroundPanel : CPanel() {
        val backgroundLabelsKey: List<Label> = listOf(
                Label("Open new view:", 0f, 0f, 10f, 10f),
                Label("Close view:", 0f, 0f, 10f, 10f),
                Label("Resize view:", 0f, 0f, 10f, 10f),
                Label("Change mode:", 0f, 0f, 10f, 10f)
        )
        val backgroundLabelsValue: List<Label> = listOf(
                Label("Alt + N", 0f, 0f, 10f, 10f),
                Label("Alt + D", 0f, 0f, 10f, 10f),
                Label("Alt + J/K", 0f, 0f, 10f, 10f),
                Label("Alt + M", 0f, 0f, 10f, 10f)
        )

        init {
            setTransparent()
            (backgroundLabelsKey + backgroundLabelsValue).forEach {
                add(it)
                it.textState.apply {
                    textColor = ColorConstants.white()
                    fontSize = 20f
                }
            }
        }
    }
}