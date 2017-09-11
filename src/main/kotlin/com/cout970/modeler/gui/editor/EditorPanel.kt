package com.cout970.modeler.gui.editor

import com.cout970.modeler.gui.MutablePanel
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.editor.bottompanel.ModuleBottomPanel
import com.cout970.modeler.gui.editor.centerpanel.ModuleCenterPanel
import com.cout970.modeler.gui.editor.leftpanel.ModuleLeftPanel
import com.cout970.modeler.gui.editor.rightpanel.ModuleRightPanel
import com.cout970.modeler.gui.editor.toppanel.ModuleTopPanel
import com.cout970.modeler.gui.react.ReactRenderer.render
import com.cout970.modeler.gui.react.components.LeftPanel
import com.cout970.modeler.gui.react.leguicomp.Panel
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.gui.react.scalable.FillWindow
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Container

/**
 * Created by cout970 on 2017/06/09.
 */
class EditorPanel : MutablePanel() {

    val topPanelModule = ModuleTopPanel()
    val leftPanelModule = ModuleLeftPanel()
    val rightPanelModule = ModuleRightPanel()
    val centerPanelModule = ModuleCenterPanel()
    val bottomPanelModule = ModuleBottomPanel()

    val reactBase = panel {
        scalable = FillWindow()
        setTransparent()
        setBorderless()
    }

    init {
        add(topPanelModule.panel)
        add(leftPanelModule.panel)
        add(rightPanelModule.panel)
        add(centerPanelModule.panel)
        add(bottomPanelModule.panel)
//        add(reactBase)
        setTransparent()
        setBorderless()
        render(reactBase) {
            LeftPanel { }
        }
    }

    override fun updateSizes(newSize: IVector2) {
        size = newSize.toJoml2f()
        position = Vector2f()

//        recursiveUpdateSize(reactBase, Panel(), newSize)

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

    fun recursiveUpdateSize(c: Component, parent: Container<*>, windowSize: IVector2) {
        if (c is Panel) {
            c.scalable?.updateScale(c, parent, windowSize)
        }
        (c as? Container<*>)?.apply {
            childs?.forEach {
                recursiveUpdateSize(it, this, windowSize)
            }
        }
    }
}
