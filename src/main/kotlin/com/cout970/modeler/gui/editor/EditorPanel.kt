package com.cout970.modeler.gui.editor

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.ModelAccessor
import com.cout970.modeler.gui.MutablePanel
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.editor.bottompanel.ModuleBottomPanel
import com.cout970.modeler.gui.react.components.CenterPanel
import com.cout970.modeler.gui.react.components.LeftPanel
import com.cout970.modeler.gui.react.components.RightPanel
import com.cout970.modeler.gui.react.components.TopButtonPanel
import com.cout970.modeler.gui.react.core.RComponentRenderer.render
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

    val bottomPanelModule = ModuleBottomPanel()

    val reactBase = panel {
        setTransparent()
        setBorderless()
    }

    init {
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
        render(reactBase, gui) {
            panel {
                FillParent.updateScale(this, newSize)
                setTransparent()
                setBorderless()

                +TopButtonPanel {

                }
                +RightPanel {
                    RightPanel.Props(
                            projectManager = gui.projectManager,
                            selectionHandler = gui.selectionHandler,
                            guiState = gui.state,
                            hide = !gui.state.showRightPanel
                    )
                }
                +LeftPanel {
                    LeftPanel.Props(
                            access = ModelAccessor(gui.projectManager, gui.selectionHandler),
                            dispatcher = gui.dispatcher,
                            hide = !gui.state.showLeftPanel
                    )
                }
                +CenterPanel {
                    CenterPanel.Props(
                            leftPanelHidden = !gui.state.showLeftPanel,
                            rightPanelHidden = !gui.state.showRightPanel,
                            canvasContainer = gui.canvasContainer
                    )
                }
            }
        }
    }
}
