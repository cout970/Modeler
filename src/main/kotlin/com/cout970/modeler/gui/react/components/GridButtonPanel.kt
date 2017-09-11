package com.cout970.modeler.gui.react.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.react.IComponentFactory
import com.cout970.modeler.gui.react.ReactComponent
import com.cout970.modeler.gui.react.leguicomp.ToggleButton
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.gui.react.scalable.FixedYFillX
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/08.
 */
class GridButtonPanel private constructor() : ReactComponent<Unit, Unit>() {

    init {
        setState(Unit)
    }

    override fun render(): Component = panel {
        backgroundColor = Config.colorPalette.darkestColor.toColor()
        scalable = FixedYFillX(0f, 280f)
        setBorderless()

        +ToggleButton(5f, 2f, 24f, 24f, true, "")
                .setBorderless()
                .apply { cornerRadius = 0f }

        +ToggleButton(34f, 2f, 24f, 24f, true, "drawTextureGridLines")
                .setBorderless()
                .apply { cornerRadius = 0f }
    }

    companion object : IComponentFactory<Unit, Unit, GridButtonPanel> {

        override fun createDefaultProps() = Unit

        override fun build(props: Unit): GridButtonPanel = GridButtonPanel()
    }
}