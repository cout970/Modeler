package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.leguicomp.onCmd
import com.cout970.modeler.gui.rcomponents.left.LeftPanel
import com.cout970.modeler.gui.rcomponents.left.LeftPanelProps
import com.cout970.modeler.gui.rcomponents.popup.PopUp
import com.cout970.modeler.gui.rcomponents.popup.PopUpProps
import com.cout970.modeler.gui.rcomponents.right.RightPanel
import com.cout970.modeler.gui.rcomponents.right.RightPanelProps
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RState
import com.cout970.reactive.dsl.*
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

        child(CenterPanel::class, CenterPanelProps(props.gui.canvasContainer, props.gui.timer))

        child(LeftPanel::class, LeftPanelProps(state.leftVisible, props.gui.state,
            props.gui.gridLines, props.gui.animator, props.gui.canvasContainer) { props.gui.root.reRender() })

        child(RightPanel::class, RightPanelProps(state.rightVisible, props.gui.programState,
            props.gui.state, props.gui.input))

        child(BottomPanel::class, BottomPanelProps(state.bottomVisible, props.gui.animator,
            props.gui.programState, props.gui.input))

        child(TopBar::class, TopBarProps(props.gui))

        child(Search::class)

        child(PopUp::class, PopUpProps(props.gui.state, props.gui.propertyHolder))

        onCmd("toggleLeft") { setState { copy(leftVisible = !leftVisible) } }
        onCmd("toggleRight") { setState { copy(rightVisible = !rightVisible) } }
        onCmd("toggleBottom") { setState { copy(bottomVisible = !bottomVisible) } }
    }
}
