package com.cout970.modeler.view.gui.editor

import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.view.gui.MutablePanel
import com.cout970.modeler.view.gui.editor.centerpanel.ModuleCenterPanel
import com.cout970.modeler.view.gui.editor.leftpanel.ModuleLeftPanel
import com.cout970.modeler.view.gui.editor.rightpanel.ModuleRightPanel
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.xf
import com.cout970.vector.extensions.yf
import org.joml.Vector2f
import org.liquidengine.legui.color.ColorConstants

/**
 * Created by cout970 on 2017/06/09.
 */
class EditorPanel : MutablePanel() {

    val leftPanelModule = ModuleLeftPanel()
    val rightPanelModule = ModuleRightPanel()
    val centerPanelModule = ModuleCenterPanel()

    init {
        backgroundColor = ColorConstants.transparent()
        add(leftPanelModule.panel)
        add(rightPanelModule.panel)
        add(centerPanelModule.panel)
    }

    override fun updateSizes(newSize: IVector2) {
        size = newSize.toJoml2f()
        position = Vector2f()

        leftPanelModule.apply {
            panel.size = Vector2f(190f, newSize.yf)
            panel.position = Vector2f()
            layout.rescale()
        }

        rightPanelModule.apply {
            panel.size = Vector2f(190f, newSize.yf)
            panel.position = Vector2f(newSize.xf - 190f, 0f)
            layout.rescale()
        }

        centerPanelModule.apply {
            panel.size = Vector2f(newSize.xf - (leftPanelModule.panel.size.x + rightPanelModule.panel.size.x),
                    newSize.yf)
            panel.position = Vector2f(leftPanelModule.panel.size.x, 0f)
            layout.rescale()
            presenter.updateBackground()
            layout.rescale()
        }
    }
}
