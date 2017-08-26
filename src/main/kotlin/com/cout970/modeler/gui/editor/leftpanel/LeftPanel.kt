package com.cout970.modeler.gui.editor.leftpanel

import com.cout970.modeler.gui.GuiResources
import com.cout970.modeler.gui.comp.CPanel
import com.cout970.modeler.gui.comp.CToggleButton
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.editor.leftpanel.editcubepanel.EditCubePanel
import com.cout970.modeler.util.BooleanPropertyWrapper
import com.cout970.modeler.util.hide
import org.joml.Vector2f
import org.liquidengine.legui.icon.ImageIcon

/**
 * Created by cout970 on 2017/06/09.
 */
class LeftPanel : CPanel() {

    val topButtonPanel = TopButtonPanel()
    val editCubePanel = EditCubePanel()

    init {
        add(topButtonPanel)
        add(editCubePanel)
        editCubePanel.position.y = 28f
        editCubePanel.hide()
        setBorderless()
    }

    class TopButtonPanel : CPanel(width = 180f, height = 28f) {

        val showModelGridsButton = CToggleButton(5f, 2f, 24f, 24f, true) {
            BooleanPropertyWrapper(it::drawModelGridLines)
        }
        val showTextureGridsButton = CToggleButton(34f, 2f, 24f, 24f, true) {
            BooleanPropertyWrapper(it::drawTextureGridLines)
        }

        init {
            add(showModelGridsButton)
            add(showTextureGridsButton)
            showModelGridsButton.setBorderless()
            showModelGridsButton.cornerRadius = 0f
            showTextureGridsButton.setBorderless()
            showTextureGridsButton.cornerRadius = 0f
            setBorderless()
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