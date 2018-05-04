package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.gui.leguicomp.*
import com.cout970.reactive.core.EmptyProps
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

class EditorControls : RComponent<EmptyProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditorControls") {
        style {
            transparent()
            border(2f) { greyColor }
            rectCorners()
            height = if (state.on) 53f else 24f
        }

        postMount {
            marginX(5f)
        }

        comp(FixedLabel()) {
            style {
                textState.apply {
                    this.text = "Editor Controls"
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

            val config: ToggleButton.() -> Unit = {
                borderless()
                rectCorners()
            }

            +ToggleButton("drawTextureGridLines", "grid", true, 0f, 0f, 24f, 24f)
                    .apply(config)
                    .apply { tooltip = InstantTooltip("Enable/Disable texture grid lines") }

            +ToggleButton("renderLights", "focus", false, 0f, 0f, 24f, 24f)
                    .apply(config)
                    .apply { tooltip = InstantTooltip("Enable/Disable lights rendering") }

            +ToggleButton("useTexture", "texture", true, 0f, 0f, 24f, 24f)
                    .apply(config)
                    .apply { tooltip = InstantTooltip("Enable/Disable model texture") }

            +ToggleButton("useColor", "color", false, 0f, 0f, 24f, 24f)
                    .apply(config)
                    .apply { tooltip = InstantTooltip("Enable/Disable model coloring") }

            +ToggleButton("useLight", "light", true, 0f, 0f, 24f, 24f)
                    .apply(config)
                    .apply { tooltip = InstantTooltip("Enable/Disable lightning") }

            +ToggleButton("showInvisible", "invisible", true, 0f, 0f, 24f, 24f)
                    .apply(config)
                    .apply { tooltip = InstantTooltip("Enable/Disable transparent sides") }

            +ToggleButton("renderBase", "invisible", true, 0f, 0f, 24f, 24f)
                    .apply(config)
                    .apply { tooltip = InstantTooltip("Enable/Disable base block") }

            +ToggleButton("drawTextureProjection", "invisible", true, 0f, 0f, 24f, 24f)
                    .apply(config)
                    .apply { tooltip = InstantTooltip("Enable/Disable texture projection") }

            +ToggleButton("renderSkybox", "invisible", true, 0f, 0f, 24f, 24f)
                    .apply(config)
                    .apply { tooltip = InstantTooltip("Enable/Disable skybox") }
        }
    }
}