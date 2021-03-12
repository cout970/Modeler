package com.cout970.reactive.internal.hello

import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.core.Renderer
import com.cout970.reactive.dsl.*
import com.cout970.reactive.internal.demoWindow
import com.cout970.reactive.nodes.*
import org.liquidengine.legui.component.optional.align.HorizontalAlign

fun main(args: Array<String>) {
    demoWindow { env ->
        Renderer.render(env.frame.container) {
            child(HelloMessage::class, HelloMessageProps("World"))
        }
    }
}

data class HelloMessageProps(val name: String) : RProps

class HelloMessage : RStatelessComponent<HelloMessageProps>() {

    override fun RBuilder.render() {
        div {
            postMount {
                sizeX = 150f
                sizeY = 50f
                center()
            }

            label("Hello ${props.name}") {

                style {
                    horizontalAlign = HorizontalAlign.CENTER
                }

                postMount {
                    centerX()
                }
            }

            selectBox {
                style {
                    posX = 0f
                    posY = 20f
                    sizeX = 100f
                    sizeY = 20f
                    addElement("A")
                    addElement("B")
                    addElement("C")
                    addElement("D")
                    addElement("E")
                    addElement("F")
                    visibleCount = 2
                    elementHeight = 20f
                }
                childrenAsNodes()

                postMount {
                    centerX()
                }
            }
        }
    }
}