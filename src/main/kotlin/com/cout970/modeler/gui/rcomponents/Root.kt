package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.leguicomp.onCmd
import com.cout970.modeler.gui.leguicomp.postMount
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RState
import com.cout970.reactive.dsl.borderless
import com.cout970.reactive.dsl.sizeX
import com.cout970.reactive.dsl.sizeY
import com.cout970.reactive.dsl.transparent
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style

data class RootState(
        var leftVisible: Boolean = true,
        var rightVisible: Boolean = true,
        var bottomVisible: Boolean = false
) : RState

data class RootProps(val gui: Gui) : RProps

class RootComp : RComponent<RootProps, RootState>() {

    override fun getInitialState() = RootState()

    override fun RBuilder.render() = div("Root") {

        style {
            transparent()
            borderless()
        }

        postMount {
            sizeX = parent.sizeX
            sizeY = parent.sizeY
        }

        child(TopBar::class)
        child(CenterPanel::class, CenterPanelProps(props.gui.canvasContainer, props.gui.timer))

        child(LeftPanel::class, LeftPanelProps(state.leftVisible, props.gui.modelAccessor, props.gui.gridLines))
        child(RightPanel::class, RightPanelProps(state.rightVisible, props.gui.modelAccessor, props.gui.state))

        onCmd("toggleLeft") { setState { copy(leftVisible = !leftVisible) } }
        onCmd("toggleRight") { setState { copy(rightVisible = !rightVisible) } }
        onCmd("toggleBottom") { setState { copy(bottomVisible = !bottomVisible) } }
    }
}
