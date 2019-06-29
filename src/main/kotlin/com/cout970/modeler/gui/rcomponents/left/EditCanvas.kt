package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.leguicomp.dispatch
import com.cout970.modeler.gui.leguicomp.onClick
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Label

data class EditCanvasProps(val container: CanvasContainer, val reRender: () -> Unit) : RProps


class EditCanvas : RComponent<EditCanvasProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(false)

    override fun RBuilder.render() = div("EditGrids") {
        val numCanvas = props.container.canvas.size
        style {
            classes("left_panel_group", "config_canvas")
            height = if (state.on) 146f + 112 * numCanvas else 24f
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
                    classes("btn_text")
                    onClick {
                        props.container.layout.runAction(action)
                        props.reRender()
                    }
                }
            }

            btn("Add Canvas", 4f, 4f, "canvas.new")
            btn("Remove Canvas", 146f, 4f, "canvas.delete")

            btn("Expand into left", 4f, 32f, "move.splitter.left")
            btn("Expand into right", 146f, 32f, "move.splitter.right")

            btn("Expand up", 4f, 60f, "move.splitter.up")
            btn("Expand down", 146f, 60f, "move.splitter.down")

            btn("Cycle layout", 4f, 88f, "layout.change.mode", width = 262f)

            fun btn2(index: Int, text: String, x: Float, y: Float, action: String, width: Float = 120f, height: Float = 24f, active: Boolean = false) {
                +Button(text, x, y, width, height).apply {
                    classes("btn_text")
                    metadata["canvas"] = index
                    onClick { dispatch(action) }
                    if (active) {
                        classes("btn_active")
                    }
                }
            }

            props.container.canvas.forEachIndexed { index, canvas ->
                val label = canvas.childComponents?.getOrNull(0) as? Label
                val name = label?.textState?.text ?: "None"
                val inc = index * 112f

                +Label(name, 4f, 116f + inc, 262f, 24f).also {
                    it.classes("canvas_label")
                }

                btn2(index, "Model mode", 4f, 144f + inc, "view.set.model.mode")
                btn2(index, "Texture mode", 146f, 144f + inc, "view.set.texture.mode")
                btn2(index, "Toggle Otho", 4f, 172f + inc, "view.switch.ortho")
                btn2(index, "Set isometric", 146f, 172f + inc, "camera.set.isometric")

                btn2(index, "Lock pos.", 4f, 200f + inc, "view.set.camera.lock.position", width = 84f,
                    active = canvas.cameraHandler.lockPos
                )
                btn2(index, "Lock rot.", 92f, 200f + inc, "view.set.camera.lock.rotation", width = 84f,
                    active = canvas.cameraHandler.lockRot
                )
                btn2(index, "Lock zoom", 182f, 200f + inc, "view.set.camera.lock.scale", width = 84f,
                    active = canvas.cameraHandler.lockScale
                )
            }
        }
    }
}