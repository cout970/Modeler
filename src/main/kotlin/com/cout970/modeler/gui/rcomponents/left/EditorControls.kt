package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.gui.leguicomp.InstantTooltip
import com.cout970.modeler.gui.leguicomp.ToggleButton
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.reactive.core.EmptyProps
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style

class EditorControls : RComponent<EmptyProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(true)

    override fun RBuilder.render() = div("EditorControls") {
        style {
            classes("left_panel_group", "editor_controls")
            height = if (state.on) 53f + 8f else 24f
        }

        postMount {
            marginX(5f)
        }

        child(GroupTitle::class.java, GroupTitleProps("Editor Controls", state.on) { setState { copy(on = !on) } })

        div {
            style {
                transparent()
                borderless()
                posY = 24f + 8f
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