package com.cout970.modeler.view.gui.editor.centerpanel

import com.cout970.modeler.util.BooleanPropertyWrapper
import com.cout970.modeler.view.GuiResources
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.gui.comp.CToggleButton
import org.joml.Vector2f
import org.liquidengine.legui.color.ColorConstants
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.icon.ImageIcon

/**
 * Created by cout970 on 2017/07/16.
 */

class CenterPanel : CPanel() {
    val topMenu = TopMenu()
    val canvasPanel = CPanel()
    val backgroundPanel = CanvasBackgroundPanel()

    init {
        backgroundColor = ColorConstants.transparent()
        add(topMenu)
        add(canvasPanel)
        add(backgroundPanel)
        canvasPanel.backgroundColor = ColorConstants.transparent()
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
            backgroundColor = ColorConstants.transparent()
            (backgroundLabelsKey + backgroundLabelsValue).forEach {
                add(it)
                it.textState.apply {
                    textColor = ColorConstants.white()
                    fontSize = 20f
                }
            }
        }
    }

    class TopMenu : CPanel() {

        val showModelGridsButton = CToggleButton(5f, 2f, 24f, 22f, true) {
            BooleanPropertyWrapper(it::drawModelGridLines)
        }
        val showTextureGridsButton = CToggleButton(34f, 2f, 24f, 22f, true) {
            BooleanPropertyWrapper(it::drawTextureGridLines)
        }

        init {
            add(showModelGridsButton)
            add(showTextureGridsButton)
        }

        override fun loadResources(resources: GuiResources) {
            showModelGridsButton.setImage(
                    active = ImageIcon(resources.showGridsIcon).also { it.size = Vector2f(16f) },
                    notActive = ImageIcon(resources.hideGridsIcon).also { it.size = Vector2f(16f) }
            )
            showTextureGridsButton.setImage(
                    active = ImageIcon(resources.showGridsIcon).also { it.size = Vector2f(16f) },
                    notActive = ImageIcon(resources.hideGridsIcon).also { it.size = Vector2f(16f) }
            )
        }
    }
}