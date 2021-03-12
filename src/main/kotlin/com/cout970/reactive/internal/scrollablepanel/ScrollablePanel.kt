package com.cout970.reactive.internal.scrollablepanel

import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.internal.demoWindow
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.scrollablePanel
import com.cout970.reactive.nodes.style
import org.joml.Vector4f
import org.liquidengine.legui.style.color.ColorConstants.black
import org.liquidengine.legui.style.color.ColorConstants.white
import java.awt.Color.getHSBColor

fun main(args: Array<String>) {
    demoWindow { env ->
        env.frame.container.backgroundColor { black() }

        env.context.isDebugEnabled = true

        Renderer.render(env.frame.container) {
            child(Scroll::class)
        }
    }
}

data class ScrollState(val seconds: Int) : RState

class Scroll : RComponent<EmptyProps, ScrollState>() {

    override fun getInitialState() = ScrollState(0)

    override fun RBuilder.render() = scrollablePanel("test") {

        postMount {
            fill()
        }

        verticalScroll {
            style {
                arrowColor = Vector4f(0xF1.toFloat(), 0xF1.toFloat(), 0xF1.toFloat(), 255f).div(255f)
                scrollColor = Vector4f(0xC1.toFloat(), 0xC1.toFloat(), 0xC1.toFloat(), 255f).div(255f)
                arrowSize = 17f
                style.setMinimumSize(17f, 17f)
                backgroundColor { arrowColor }
                rectCorners()
                borderless()
            }
        }

        horizontalScroll {
            style { hide() }
        }

        viewport {

            postMount {
                sizeX = parent.sizeX - 10f
            }
        }

        container {
            style {
                backgroundColor { white() }
                sizeY = 8f * 255f
            }

            postMount {
                sizeX = parent.sizeX - 10f
                // Place colors in column, starting at the top and going down
                floatTop(0f)
            }

            for (i in 1..255) {

                div(i.toString()) {

                    style {
                        borderless()
                        rectCorners()
                        backgroundColor {
                            val color = getHSBColor(i / 255f, 1.0f, 1.0f)
                            Vector4f(color.red / 255f, color.green / 255f, color.blue / 255f, 1f)
                        }
                        sizeY = 8f
                    }

                    postMount {
                        sizeX = parent.sizeX
                    }
                }
            }
        }
    }
}