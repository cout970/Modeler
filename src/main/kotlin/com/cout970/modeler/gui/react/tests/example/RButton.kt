package com.cout970.modeler.gui.react.tests.example

import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.gui.react.tests.RComponent
import com.cout970.modeler.gui.react.tests.RComponentSpec
import com.cout970.modeler.gui.react.tests.RContext
import org.joml.Vector2f
import org.liquidengine.legui.color.ColorConstants
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Label

/**
 * Created by cout970 on 2017/09/23.
 */

class RButton(props: Props) : RComponent<RButton.Props, RButton.State>(props) {

    init {
        state = State(false)
    }

    override fun render(context: RContext): Component {
        return panel {
            size = props.size
            position = Vector2f()

            if (state.on) {
                backgroundColor = ColorConstants.green()
                +Label("On")
            } else {
                backgroundColor = ColorConstants.red()
                +Label("Off")
            }
        }
    }

    class Props(val size: Vector2f)

    class State(val on: Boolean)

    companion object : RComponentSpec<RButton, Props, State> {

        override val defaultProps = Props(Vector2f(50f, 50f))

        override fun build(props: Props): RButton = RButton(props)
    }
}