package com.cout970.modeler.gui.react.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.leguicomp.ToggleButton
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.gui.react.scalable.FixedYFillX
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
        FixedYFillX(280f).updateScale(this, ctx.parentSize)
        setBorderless()

        +ToggleButton(5f, 2f, 24f, 24f, true, "")
                .setBorderless()
                .apply { cornerRadius = 0f }

        +ToggleButton(34f, 2f, 24f, 24f, true, "drawTextureGridLines")
                .setBorderless()
                .apply { cornerRadius = 0f }
    }


}