package com.cout970.modeler.gui.components

import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.canvas.GridLines
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.reactive.invoke
import com.cout970.modeler.gui.views.VisibleElements
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.setBorderless
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.event.ScrollEvent

/**
 * Created by cout970 on 2017/09/07.
 */
class LeftPanel : RComponent<LeftPanel.Props, LeftPanel.State>() {

    init {
        state = State(true, true, true)
    }

    override fun build(ctx: RBuilder): Component = panel {
        background { darkestColor }
        posY = 48f
        width = 288f
        height = ctx.parentSize.yf - 48f
        setBorderless()

        if (!props.visibleElements.left) {
            hide()
        }

        +GridButtonPanel {}

        +VerticalPanel(0f, 32f, width, height).apply {
            verticalScrollBar.visibleAmount = 20f
            val listeners = viewport.listenerMap.getListeners(ScrollEvent::class.java)
            listeners.forEach {
                viewport.listenerMap.removeListener(ScrollEvent::class.java, it)
            }

            var posY = 0f
            +EditObjectName {
                EditObjectName.Props(props.access, props.dispatcher, posY, state.editObjectName) {
                    replaceState(state.copy(editObjectName = !state.editObjectName))
                }
            }
            posY += if (state.editObjectName) 64f else 24f
            posY += 6f

            +EditCubePanel {
                EditCubePanel.Props(props.access, props.dispatcher, posY, state.editCubePanel) {
                    replaceState(state.copy(editCubePanel = !state.editCubePanel))
                }
            }
            posY += if (state.editCubePanel) 484f else 24f
            posY += 6f

            +EditGrids {
                EditGrids.Props(props.dispatcher, props.gridLines, posY, state.editGrids) {
                    replaceState(state.copy(editGrids = !state.editGrids))
                }
            }

            posY += if (state.editGrids) 345f else 24f
            posY += 6f

            container.apply {
                width = 280f
                height = posY
            }
        }
    }

    class Props(val access: IModelAccessor, val dispatcher: Dispatcher, val visibleElements: VisibleElements,
                val gridLines: GridLines)

    data class State(val editObjectName: Boolean = true, val editCubePanel: Boolean, val editGrids: Boolean)

    companion object : RComponentSpec<LeftPanel, Props, State>
}