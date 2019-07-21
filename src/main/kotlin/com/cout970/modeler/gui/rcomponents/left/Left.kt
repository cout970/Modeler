package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.core.project.IProgramState
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.canvas.CanvasContainer
import com.cout970.modeler.gui.canvas.GridLines
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.leguicomp.clear
import com.cout970.modeler.render.tool.Animator
import com.cout970.reactive.core.*
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.misc.listener.scrollablepanel.ScrollablePanelViewportScrollListener
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.ScrollEvent
import org.liquidengine.legui.input.Mouse
import org.liquidengine.legui.system.handler.SehUtil

data class LeftPanelProps(
    val visible: Boolean, val programState: GuiState,
    val grids: GridLines, val animator: Animator,
    val canvasContainer: CanvasContainer,
    val reRender: () -> Unit
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
            }

            horizontalScroll { style { hide() } }

            verticalScroll {
                style {
                    isArrowsEnabled = false
                    classes("vertical_scroll", "left_panel_scroll")
                }
            }

            viewport {
                style {
                    transparent()
                    borderless()
                }

                postMount {
                    listenerMap.clear(ScrollEvent::class.java)
                    listenerMap.addListener(ScrollEvent::class.java, CustomScrollablePanelViewportScrollListener())
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

                child(EditObjectPanel::class, ModelAccessorProps(props.programState))
                child(EditGroupPanel::class, ModelAccessorProps(props.programState))
                child(EditKeyframe::class, EditKeyframeProps(props.animator, props.programState))
                child(EditChannel::class, EditChannelProps(props.animator, props.programState))
                child(EditorControls::class, EditorControlsProps(props.programState))
                child(EditGrids::class, EditGridsProps(props.grids))
                child(EditCanvas::class, EditCanvasProps(props.canvasContainer, props.reRender))
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

class CustomScrollablePanelViewportScrollListener : ScrollablePanelViewportScrollListener() {
    override fun process(event: ScrollEvent<*>) {
        val targetList = mutableListOf<Component>()
        SehUtil.recursiveTargetComponentListSearch(Mouse.getCursorPosition(), event.targetComponent, targetList)
        for (component in targetList) {
            if (component is TextInput || component.metadata[Renderer.METADATA_KEY] == "TinyFloatInput") {
                return
            }
        }
        super.process(event)
    }
}