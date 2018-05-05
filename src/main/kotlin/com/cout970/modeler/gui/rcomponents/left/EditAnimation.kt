package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.event.EventAnimatorUpdate
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.joml.Vector2f
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.CharIcon
import org.liquidengine.legui.style.color.ColorConstants
import org.liquidengine.legui.style.font.FontRegistry


class EditAnimation : RComponent<ModelAccessorProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditAnimation") {
        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
            height = if (state.on) 64f else 24f
        }

        postMount {
            marginX(5f)
        }

        on<EventModelUpdate> {
            rerender()
        }
        on<EventSelectionUpdate> {
            rerender()
        }

        comp(FixedLabel()) {
            style {
                textState.apply {
                    this.text = "Animation"
                    textColor = Config.colorPalette.textColor.toColor()
                    horizontalAlign = HorizontalAlign.CENTER
                    fontSize = 20f
                }
            }

            postMount {
                posX = 50f
                posY = 0f
                sizeX = parent.sizeX - 100f
                sizeY = 24f
            }
        }

        // close button
        comp(IconButton()) {
            style {
                val charCode = if (state.on) 'X' else 'O'
                setImage(CharIcon(Vector2f(16f, 16f), FontRegistry.DEFAULT, charCode, ColorConstants.lightGray()))
                background { darkColor }
                posX = 250f
                posY = 4f
            }
            onRelease {
                setState { copy(on = !on) }
            }
        }

        // add channel button
        // select interpolation type for the selected channel
        div {
            style {
                transparent()
                borderless()
                posY = 24f
                sizeY = 24f
            }

            postMount {
                marginX(5f)
                floatLeft(5f, 0f)
            }

            +IconButton("animation.channel.add", "add_channel", 0f, 0f, 24f, 24f).apply {
                borderless()
                rectCorners()
                tooltip = InstantTooltip("Add new animation channel")
            }
        }

        on<EventAnimatorUpdate> { rerender() }
    }
}