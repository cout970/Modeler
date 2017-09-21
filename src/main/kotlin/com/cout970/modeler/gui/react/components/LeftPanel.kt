package com.cout970.modeler.gui.react.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.react.IComponentFactory
import com.cout970.modeler.gui.react.ReactComponent
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.gui.react.scalable.FixedXFillY
import com.cout970.modeler.util.toColor
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/07.
 */
class LeftPanel private constructor() : ReactComponent<Unit, Unit>(Unit) {

    init {
        setState(Unit)
    }

    override fun render(parentSize: IVector2): Component = panel {
        backgroundColor = Config.colorPalette.darkestColor.toColor()
        FixedXFillY(280f).updateScale(this, parentSize)
        setBorderless()

        +GridButtonPanel {}
    }

    companion object : IComponentFactory<Unit, Unit, LeftPanel> {

        override fun createDefaultProps() = Unit

        override fun build(props: Unit): LeftPanel = LeftPanel()
    }
}