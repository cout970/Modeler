package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.gui.canvas.GridLines
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.leguicomp.clear
import com.cout970.modeler.gui.leguicomp.color
import com.cout970.modeler.render.tool.Animator
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RState
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.ScrollEvent

data class LeftPanelProps(
        val visible: Boolean, val programState: IProgramState,
        val grids: GridLines, val animator: Animator
) : RProps

class LeftPanel : RStatelessComponent<LeftPanelProps>() {

    override fun RBuilder.render() = div("LeftPanel") {
        style {
            posX = 0f
            posY = 48f

            classes(if (!props.visible) "left_panel_hide" else "left_panel")
        }

        postMount {
            width = 288f
            height = parent.size.y - 48f
        }

        scrollablePanel {

            style {
                borderless()
                transparent()
            }

            postMount {
                posX = 0f
                posY = 5f
                sizeX = parent.sizeX
                sizeY = parent.sizeY - posY + 8f

                child("Container")?.listenerMap?.clear(ScrollEvent::class.java)
            }

            horizontalScroll { style { hide() } }

            verticalScroll {
                style {
                    isArrowsEnabled = false
                    scrollColor = color { bright1 }
                    rectCorners()
                    classes("left_panel_scroll")
                }
            }

            viewport {
                style {
                    transparent()
                    borderless()
                }

                postMount {
                    listenerMap.clear(ScrollEvent::class.java)
                }
            }

            container {
                style {
                    borderless()
                    transparent()
                    width = 288f - 8f
                    height = 1200f
                }

                postMount {
                    floatTop(6f)
                }

                child(EditorControls::class)
                child(EditObjectName::class, ModelAccessorProps(props.programState))
                child(EditObjectPanel::class, ModelAccessorProps(props.programState))
                child(EditKeyframe::class, EditKeyframeProps(props.animator, props.programState))
                child(EditGrids::class, EditGridsProps(props.grids))
            }
        }
    }
}

data class VisibleWidget(val on: Boolean) : RState
data class ModelAccessorProps(val access: IProgramState) : RProps

data class GroupTitleProps(val title: String, val on: Boolean, val toggle: () -> Unit) : RProps

class GroupTitle : RStatelessComponent<GroupTitleProps>() {

    override fun RBuilder.render() {

        comp(Button()) {
            style {
                textState.apply {
                    this.text = props.title
                    horizontalAlign = HorizontalAlign.CENTER
                    fontSize = 24f
                }
                classes(if (props.on) "group_title_pressed" else "group_title")
            }

            postMount {
                posX = 0f
                posY = 0f
                sizeX = parent.sizeX
                sizeY = 24f
            }

            onRelease {
                props.toggle()
            }
        }
    }
}

