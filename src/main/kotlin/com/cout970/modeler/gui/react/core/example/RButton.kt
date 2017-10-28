package com.cout970.modeler.gui.react.core.example

import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.panel
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

    override fun build(ctx: RBuildContext): Component {
        return panel {
            size = props.size
            position = Vector2f()

            listenerMap.addListener(MouseClickEvent::class.java) {
                if (it.action == MouseClickEvent.MouseClickAction.RELEASE)
                    replaceState(State(!state.on))
            }

            if (state.on) {
                backgroundColor = ColorConstants.green()
                add(Label("On"))
            } else {
                backgroundColor = ColorConstants.red()
                add(Label("Off"))
            }
        }
    }

    class Props(val size: Vector2f)

    data class State(val on: Boolean)

    companion object : RComponentSpec<RButton, Props, State>
}