package com.cout970.modeler.gui.components

import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.reactive.invoke
import com.cout970.modeler.gui.views.VisibleElements
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.setBorderless
import org.liquidengine.legui.component.Component

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
        width = 280f
        height = ctx.parentSize.yf - 48f
        setBorderless()

        if (!props.visibleElements.left) {
            hide()
        }

        +GridButtonPanel {}
        +EditObjectName { EditObjectName.Props(props.access, props.dispatcher) }
        +EditCubePanel { EditCubePanel.Props(props.access, props.dispatcher) }
    }

    class Props(val access: IModelAccessor, val dispatcher: Dispatcher, val visibleElements: VisibleElements)

    companion object : RComponentSpec<LeftPanel, Props, Unit>
}