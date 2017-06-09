package com.cout970.modeler.view.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/06/09.
 */
class CLabel(text: String, x: Float = 0f, y: Float = 0f, width: Float = 10f, height: Float = 24f) : Label(text, x, y,
        width, height) {

    init {
        textState.horizontalAlign = HorizontalAlign.CENTER
        textState.textColor = Config.colorPalette.textColor.toColor()
    }
}