package com.cout970.modeler.gui.react.leguicomp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/09/16.
 */

class FixedLabel(text: String, x: Float = 0f, y: Float = 0f, width: Float = 10f, height: Float = 24f) : Label(text, x,
        y,
        width, height) {

    constructor() : this("")

    init {
        textState.horizontalAlign = HorizontalAlign.CENTER
        textState.textColor = Config.colorPalette.textColor.toColor()
    }
}