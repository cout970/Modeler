package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.canvas.GridLines
import com.cout970.modeler.gui.leguicomp.background
import com.cout970.modeler.gui.leguicomp.clear
import com.cout970.modeler.gui.leguicomp.color
import com.cout970.modeler.render.tool.Animator
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RState
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.scrollablePanel
import com.cout970.reactive.nodes.style
import org.liquidengine.legui.event.ScrollEvent

data class LeftPanelProps(
        val visible: Boolean, val modelAccessor: IModelAccessor,
        val grids: GridLines, val animator: Animator
) : RProps

class LeftPanel : RStatelessComponent<LeftPanelProps>() {

    override fun RBuilder.render() = div("LeftPanel") {
        style {
            background { darkestColor }
            borderless()
            posX = 0f
            posY = 48f

            if (!props.visible)
                hide()
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
                sizeY = parent.sizeY - posY

                child("Container")?.listenerMap?.clear(ScrollEvent::class.java)
            }

            horizontalScroll { style { hide() } }

            verticalScroll {
                style {
                    isArrowsEnabled = false
                    scrollColor = color { lightBrightColor }
                    backgroundColor { color { darkColor } }
                    rectCorners()
                }
            }

            viewport {
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
                child(EditObjectName::class, ModelAccessorProps(props.modelAccessor))
                child(EditCubePanel::class, ModelAccessorProps(props.modelAccessor))
                child(EditAnimation::class, EditAnimationProps(props.animator, props.modelAccessor))
                child(EditGrids::class, EditGridsProps(props.grids))
            }
        }
    }
}

data class VisibleWidget(val on: Boolean) : RState
data class ModelAccessorProps(val access: IModelAccessor) : RProps
