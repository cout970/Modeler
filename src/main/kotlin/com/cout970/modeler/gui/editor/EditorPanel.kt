package com.cout970.modeler.gui.editor

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.MutablePanel
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.editor.bottompanel.ModuleBottomPanel
import com.cout970.modeler.gui.editor.centerpanel.ModuleCenterPanel
import com.cout970.modeler.gui.editor.leftpanel.ModuleLeftPanel
import com.cout970.modeler.gui.react.components.LeftPanel
import com.cout970.modeler.gui.react.components.RightPanel
import com.cout970.modeler.gui.react.components.TopButtonPanel
import com.cout970.modeler.gui.react.core.RComponentRenderer
import com.cout970.modeler.gui.react.core.invoke
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.gui.react.scalable.FillParent
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/06/09.
 */
class EditorPanel : MutablePanel() {

    lateinit var gui: Gui

    val leftPanelModule = ModuleLeftPanel()
    val centerPanelModule = ModuleCenterPanel()
    val bottomPanelModule = ModuleBottomPanel()

    val reactBase = panel {
        setTransparent()
        setBorderless()
    }

    init {
        add(leftPanelModule.panel)
        add(centerPanelModule.panel)
        add(bottomPanelModule.panel)
        add(reactBase)
        setTransparent()
        setBorderless()
    }

    fun reRender() {
        updateSizes(gui.windowHandler.window.getFrameBufferSize())
    }

    override fun updateSizes(newSize: IVector2) {
        size = newSize.toJoml2f()
        position = Vector2f()

        FillParent.updateScale(reactBase, newSize)
        RComponentRenderer.render(reactBase, gui) {
            panel {
                FillParent.updateScale(this, newSize)
                setTransparent()
                setBorderless()

                +TopButtonPanel {}
                +RightPanel { RightPanel.Props(gui.projectManager, gui.selectionHandler, gui.state) }
                +LeftPanel { LeftPanel.Props() }
            }
        }

        leftPanelModule.apply {
            panel.size = Vector2f(280f, newSize.yf - 48f)
            panel.position = Vector2f(0f, 48f)
            layout.rescale()
        }

        bottomPanelModule.apply {
            if (panel.isEnabled) {
                panel.size = Vector2f(newSize.xf - (leftPanelModule.panel.size.x + 190f), 160f)
            } else {
                panel.size = Vector2f()
            }
            panel.position = Vector2f(leftPanelModule.panel.size.x, newSize.yf - panel.size.y)
            layout.rescale()
        }

        centerPanelModule.apply {
            panel.size = Vector2f(
                    newSize.xf - (leftPanelModule.panel.size.x + 190f),
                    newSize.yf - (48f) - (bottomPanelModule.panel.size.y)
            )
            panel.position = Vector2f(leftPanelModule.panel.size.x, 48f)
            layout.rescale()
            presenter.updateBackground()
            layout.rescale()
        }
    }
}
