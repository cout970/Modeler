package com.cout970.modeler.gui.react.components

import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.IModelAccessor
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.core.invoke
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.toColor
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.extensions.vec2Of
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/07.
 */
class LeftPanel : RComponent<LeftPanel.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuildContext): Component = panel {
        backgroundColor = Config.colorPalette.darkestColor.toColor()
        posY = 48f
        size = vec2Of(280f, ctx.parentSize.yf - 48f).toJoml2f()
        setBorderless()

        if (props.hide) {
            hide()
        }

        add(GridButtonPanel {})
        add(SelectionTypeButtons { SelectionTypeButtons.Props(props.guiState) })
        add(EditCubePanel { EditCubePanel.Props(props.access, props.dispatcher) })
    }

    class Props(val access: IModelAccessor, val dispatcher: Dispatcher, val hide: Boolean, val guiState: GuiState)

    companion object : RComponentSpec<LeftPanel, Props, Unit>
}