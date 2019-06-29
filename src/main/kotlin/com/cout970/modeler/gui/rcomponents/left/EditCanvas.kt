package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.leguicomp.onClick
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.Button

data class EditCanvasProps(val container: CanvasContainer, val reRender: () -> Unit) : RProps


class EditCanvas : RComponent<EditCanvasProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditGrids") {
        style {
            classes("left_panel_group", "config_canvas")
            height = if (state.on) 355f else 24f
        }

        postMount {
            marginX(5f)
        }

        child(GroupTitle::class.java, GroupTitleProps("Config Canvas", state.on) { setState { copy(on = !on) } })

        div("buttons") {
            style {
                posY = 30f
                posX = 0f
                transparent()
                borderless()
            }

            postMount {
                fillX()
                height = (childComponents.map { it.posY + it.sizeY }.max() ?: 0f) + 4f
            }

            fun btn(text: String, x: Float, y: Float, action: String, width: Float = 120f, height: Float = 24f) {
                +Button(text, x, y, width, height).apply {
                    onClick {
                        props.container.layout.runAction(action)
                        props.reRender()
                    }
                }
            }

            btn("Add Canvas", 4f, 4f, "canvas.new")
            btn("Remove Canvas", 146f, 4f, "canvas.delete")

            btn("Expand to the left", 4f, 32f, "move.splitter.left")
            btn("Expand to the right", 146f, 32f, "move.splitter.right")

            btn("Expand upwards", 4f, 60f, "move.splitter.up")
            btn("Expand downwards", 146f, 60f, "move.splitter.down")

            btn("Cycle mode", 4f, 88f, "layout.change.mode", width = 262f)
        }
    }
}