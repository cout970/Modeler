package com.cout970.reactive.internal.counter

import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.internal.demoWindow
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.label
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.style.color.ColorConstants

fun main(args: Array<String>) {
    demoWindow { env ->
        Renderer.render(env.frame.container) {
            child(DemoComponent::class)
        }
    }
}

data class DemoState(val count: Int) : RState

class DemoComponent : RComponent<EmptyProps, DemoState>() {

    override fun getInitialState() = DemoState(0)

    override fun RBuilder.render() {

        label("You clicked me ${state.count} times!") {

            style {
                sizeX = 150f
                sizeY = 30f

                horizontalAlign = HorizontalAlign.CENTER

                backgroundColor { ColorConstants.lightBlue() }
                borderless()
            }

            postMount {
                center()
            }

            onClick {
                setState { DemoState(count + 1) }
            }
        }
    }
}
