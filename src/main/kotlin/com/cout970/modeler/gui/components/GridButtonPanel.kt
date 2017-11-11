package com.cout970.modeler.gui.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.leguicomp.ToggleButton
import com.cout970.modeler.gui.leguicomp.fillX
import com.cout970.modeler.gui.leguicomp.panel
import com.cout970.modeler.gui.reactive.RBuildContext
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/08.
 */
class GridButtonPanel : RComponent<Unit, Unit>() {

    companion object : RComponentSpec<GridButtonPanel, Unit, Unit>

    init {
        state = Unit
    }

    override fun build(ctx: RBuildContext): Component = panel {
        backgroundColor = Config.colorPalette.darkestColor.toColor()
        height = 28f
        fillX(ctx)
        setBorderless()

        +ToggleButton("drawModelGridLines", "model_grid", true, 5f, 2f, 24f, 24f)
                .setBorderless()
                .apply { cornerRadius = 0f }

        +ToggleButton("drawTextureGridLines", "grid", true, 35f, 2f, 24f, 24f)
                .setBorderless()
                .apply { cornerRadius = 0f }

        +ToggleButton("renderLights", "focus", false, 65f, 2f, 24f, 24f)
                .setBorderless()
                .apply { cornerRadius = 0f }

        +ToggleButton("useTexture", "texture", true, 95f, 2f, 24f, 24f)
                .setBorderless()
                .apply { cornerRadius = 0f }

        +ToggleButton("useColor", "color", false, 125f, 2f, 24f, 24f)
                .setBorderless()
                .apply { cornerRadius = 0f }

        +ToggleButton("useLight", "light", true, 155f, 2f, 24f, 24f)
                .setBorderless()
                .apply { cornerRadius = 0f }

        +ToggleButton("showInvisible", "invisible", true, 185f, 2f, 24f, 24f)
                .setBorderless()
                .apply { cornerRadius = 0f }
    }
}