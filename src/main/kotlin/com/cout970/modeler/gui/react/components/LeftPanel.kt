package com.cout970.modeler.gui.react.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.react.IComponentFactory
import com.cout970.modeler.gui.react.ReactComponent
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.gui.react.scalable.FixedXFillY
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/07.
 */
class LeftPanel private constructor() : ReactComponent<Unit, Unit>() {

    init {
        setState(Unit)
    }

    override fun render(): Component = panel {
        backgroundColor = Config.colorPalette.darkestColor.toColor()
        scalable = FixedXFillY(0f, 280f)
        setBorderless()

        +GridButtonPanel {}
    }

    companion object : IComponentFactory<Unit, Unit, LeftPanel> {

        override fun createDefaultProps() = Unit

        override fun build(props: Unit): LeftPanel = LeftPanel()
    }
}