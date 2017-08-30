package com.cout970.modeler.gui.editor

import com.cout970.modeler.gui.MutablePanel
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.editor.bottompanel.ModuleBottomPanel
import com.cout970.modeler.gui.editor.centerpanel.ModuleCenterPanel
import com.cout970.modeler.gui.editor.leftpanel.ModuleLeftPanel
import com.cout970.modeler.gui.editor.rightpanel.ModuleRightPanel
import com.cout970.modeler.gui.editor.toppanel.ModuleTopPanel
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/06/09.
 */
class EditorPanel : MutablePanel() {

    val topPanelModule = ModuleTopPanel()
    val leftPanelModule = ModuleLeftPanel()
    val rightPanelModule = ModuleRightPanel()
    val centerPanelModule = ModuleCenterPanel()
    val bottomPanelModule = ModuleBottomPanel()

    init {
        add(topPanelModule.panel)
        add(leftPanelModule.panel)
        add(rightPanelModule.panel)
        add(centerPanelModule.panel)
        add(bottomPanelModule.panel)
        setTransparent()
        setBorderless()
    }

    override fun updateSizes(newSize: IVector2) {
        size = newSize.toJoml2f()
        position = Vector2f()

        topPanelModule.apply {
            panel.size = Vector2f(newSize.xf, 48f)
            panel.position = Vector2f()
            layout.rescale()
        }

        leftPanelModule.apply {
            panel.size = Vector2f(280f, newSize.yf - topPanelModule.panel.size.y)
            panel.position = Vector2f(0f, topPanelModule.panel.size.y)
            layout.rescale()
        }

        rightPanelModule.apply {
            panel.size = Vector2f(190f, newSize.yf - topPanelModule.panel.size.y)
            panel.position = Vector2f(newSize.xf - panel.size.x, topPanelModule.panel.size.y)
            layout.rescale()
        }

        bottomPanelModule.apply {
            if (panel.isEnabled) {
                panel.size = Vector2f(newSize.xf - (leftPanelModule.panel.size.x + rightPanelModule.panel.size.x), 160f)
            } else {
                panel.size = Vector2f()
            }
            panel.position = Vector2f(leftPanelModule.panel.size.x, newSize.yf - panel.size.y)
            layout.rescale()
        }

        centerPanelModule.apply {
            panel.size = Vector2f(
                    newSize.xf - (leftPanelModule.panel.size.x + rightPanelModule.panel.size.x),
                    newSize.yf - (topPanelModule.panel.size.y) - (bottomPanelModule.panel.size.y)
            )
            panel.position = Vector2f(leftPanelModule.panel.size.x, topPanelModule.panel.size.y)
            layout.rescale()
            presenter.updateBackground()
            layout.rescale()
        }
    }
}
