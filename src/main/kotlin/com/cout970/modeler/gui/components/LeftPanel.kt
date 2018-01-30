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
class LeftPanel : RComponent<LeftPanel.Props, Unit>() {

    init {
        state = Unit
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
            container.apply {
                width = 280f
                height = 132f + 444f + 320f + 500f
            }
            verticalScrollBar.visibleAmount = 20f
            val listeners = viewport.listenerMap.getListeners(ScrollEvent::class.java)
            listeners.forEach {
                viewport.listenerMap.removeListener(ScrollEvent::class.java, it)
            }

            +EditObjectName { EditObjectName.Props(props.access, props.dispatcher) }
            +EditCubePanel { EditCubePanel.Props(props.access, props.dispatcher) }
            +EditGrids { EditGrids.Props(props.dispatcher, props.gridLines) }
        }
    }

    class Props(val access: IModelAccessor, val dispatcher: Dispatcher, val visibleElements: VisibleElements,
                val gridLines: GridLines)

    companion object : RComponentSpec<LeftPanel, Props, Unit>
}