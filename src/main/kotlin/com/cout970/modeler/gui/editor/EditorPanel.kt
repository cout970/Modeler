package com.cout970.modeler.gui.editor

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.MutablePanel
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.react.components.*
import com.cout970.modeler.gui.react.core.RComponentRenderer.render
import com.cout970.modeler.gui.react.core.invoke
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import org.joml.Vector2f

/**
 * Created by cout970 on 2017/06/09.
 */
class EditorPanel : MutablePanel() {

    lateinit var gui: Gui

    val reactBase = com.cout970.modeler.gui.react.panel {
        setTransparent()
        setBorderless()
    }

    init {
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

        reactBase.size = newSize.toJoml2f()
        render(reactBase, gui) {
            com.cout970.modeler.gui.react.panel {
                size = newSize.toJoml2f()
                setTransparent()
                setBorderless()
                add(TopButtonPanel { })
                add(TopButtonPanel { })
                add(RightPanel {
                    RightPanel.Props(
                            modelAccessor = gui.modelAccessor,
                            selectedMaterial = { gui.state.selectedMaterial },
                            hide = !gui.state.showRightPanel
                    )
                })
                add(LeftPanel {
                    LeftPanel.Props(
                            access = gui.modelAccessor,
                            dispatcher = gui.dispatcher,
                            hide = !gui.state.showLeftPanel,
                            guiState = gui.state
                    )
                })
                add(CenterPanel {
                    CenterPanel.Props(
                            leftPanelHidden = !gui.state.showLeftPanel,
                            rightPanelHidden = !gui.state.showRightPanel,
                            canvasContainer = gui.canvasContainer
                    )
                })

                gui.state.popup?.let {
                    when (it.name) {
                        "import" -> {
                            add(ImportDialog { ImportDialog.Props(it) })
                        }
                        "export" -> {
                            add(ExportDialog { ExportDialog.Props(it) })
                        }
                        else -> {
                        }
                    }
                }
            }
        }
    }
}
