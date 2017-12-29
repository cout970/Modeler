package com.cout970.modeler.gui.reactive.example

import com.cout970.modeler.gui.leguicomp.panel
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import org.joml.Vector2f
import org.liquidengine.legui.color.ColorConstants
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.event.MouseClickEvent

/**
 * Created by cout970 on 2017/09/23.
 */

class RButton : RComponent<RButton.Props, RButton.State>() {

    init {
        state = State(false)
    }

    override fun build(ctx: RBuilder): Component = panel {
        size = props.size
        position = Vector2f()

        listenerMap.addListener(MouseClickEvent::class.java) {
            if (it.action == MouseClickEvent.MouseClickAction.RELEASE)
                replaceState(State(!state.on))
        }

        if (state.on) {
            backgroundColor = ColorConstants.green()
            +Label("On")
        } else {
            backgroundColor = ColorConstants.red()
            +Label("Off")
        }
    }

    class Props(val size: Vector2f)

    data class State(val on: Boolean)

    companion object : RComponentSpec<RButton, Props, State>
}