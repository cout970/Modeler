package com.cout970.modeler.view.gui.editor

import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.view.gui.MutablePanel
import com.cout970.modeler.view.gui.comp.setBorderless
import com.cout970.modeler.view.gui.comp.setTransparent
import com.cout970.modeler.view.gui.editor.bottompanel.ModuleBottomPanel
import com.cout970.modeler.view.gui.editor.centerpanel.ModuleCenterPanel
import com.cout970.modeler.view.gui.editor.leftpanel.ModuleLeftPanel
import com.cout970.modeler.view.gui.editor.rightpanel.ModuleRightPanel
import com.cout970.modeler.view.gui.editor.toppanel.ModuleTopPanel
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.xf
import com.cout970.vector.extensions.yf
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
            panel.size = Vector2f(newSize.xf, 36f)
            panel.position = Vector2f()
            layout.rescale()
        }

        leftPanelModule.apply {
            panel.size = Vector2f(190f, newSize.yf - 36f)
            panel.position = Vector2f(0f, 36f)
            layout.rescale()
        }

        rightPanelModule.apply {
            panel.size = Vector2f(190f, newSize.yf - 36f)
            panel.position = Vector2f(newSize.xf - 190f, 36f)
            layout.rescale()
        }

        bottomPanelModule.apply {
            panel.size = Vector2f(newSize.xf - (leftPanelModule.panel.size.x + rightPanelModule.panel.size.x), 160f)
            panel.position = Vector2f(leftPanelModule.panel.size.x, newSize.yf - 160f)
            layout.rescale()
        }

        centerPanelModule.apply {
            panel.size = Vector2f(
                    newSize.xf - (leftPanelModule.panel.size.x + rightPanelModule.panel.size.x),
                    newSize.yf - 36f - 160f
            )
            panel.position = Vector2f(leftPanelModule.panel.size.x, 36f)
            layout.rescale()
            presenter.updateBackground()
            layout.rescale()
        }
    }
}
