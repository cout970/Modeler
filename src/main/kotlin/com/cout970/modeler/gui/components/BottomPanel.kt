package com.cout970.modeler.gui.components

import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.views.VisibleElements
import com.cout970.modeler.util.hide
import org.liquidengine.legui.component.Component

class BottomPanel : RComponent<BottomPanel.Props, Unit>() {

    override fun build(ctx: RBuilder): Component = panel {
        val left = if (props.visibleElements.left) 280f else 0f
        val right = if (props.visibleElements.right) 190f else 0f
        width = ctx.parentSize.xf - left - right
        height = 200f
        posX = left
        posY = ctx.parentSize.yf - 200f

        background { darkestColor }

        hide()
    }

    data class Props(val visibleElements: VisibleElements)

    companion object : RComponentSpec<BottomPanel, BottomPanel.Props, Unit>
}