package com.cout970.modeler.gui.components.left

import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.util.setBorderless
import com.cout970.reactive.dsl.height
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/08.
 */
class GridButtonPanel : RComponent<Unit, Unit>() {

    companion object : RComponentSpec<GridButtonPanel, Unit, Unit>

    init {
        state = Unit
    }

    override fun build(ctx: RBuilder): Component = panel {
        background { darkestColor }
        height = 28f
        fillX()
        setBorderless()


        +ToggleButton("drawTextureGridLines", "grid", true, 5f, 2f, 24f, 24f)
                .setBorderless()
                .apply { tooltip = InstantTooltip("Enable/Disable texture grid lines") }

        +ToggleButton("renderLights", "focus", false, 35f, 2f, 24f, 24f)
                .setBorderless()
                .apply { tooltip = InstantTooltip("Enable/Disable lights rendering") }

        +ToggleButton("useTexture", "texture", true, 65f, 2f, 24f, 24f)
                .setBorderless()
                .apply { tooltip = InstantTooltip("Enable/Disable model texture") }

        +ToggleButton("useColor", "color", false, 95f, 2f, 24f, 24f)
                .setBorderless()
                .apply { tooltip = InstantTooltip("Enable/Disable model coloring") }

        +ToggleButton("useLight", "light", true, 125f, 2f, 24f, 24f)
                .setBorderless()
                .apply { tooltip = InstantTooltip("Enable/Disable lightning") }

        +ToggleButton("showInvisible", "invisible", true, 155f, 2f, 24f, 24f)
                .setBorderless()
                .apply { tooltip = InstantTooltip("Enable/Disable transparent sides") }

        +ToggleButton("renderBase", "invisible", true, 185f, 2f, 24f, 24f)
                .setBorderless()
                .apply { tooltip = InstantTooltip("Enable/Disable base block") }

        +ToggleButton("drawTextureProjection", "invisible", true, 215f, 2f, 24f, 24f)
                .setBorderless()
                .apply { tooltip = InstantTooltip("Enable/Disable texture projection") }
    }
}