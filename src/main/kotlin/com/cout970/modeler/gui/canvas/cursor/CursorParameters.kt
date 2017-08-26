package com.cout970.modeler.gui.canvas.cursor

import com.cout970.modeler.core.config.Config
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.yd

/**
 * Created by cout970 on 2017/02/04.
 */
class CursorParameters(
        val length: Double,
        val width: Double
) {

    companion object {

        fun create(zoom: Double, containerSize: IVector2): CursorParameters {
            val scale = zoom / 10 * Config.cursorArrowsScale * (1000 / containerSize.yd)
            val length = Config.cursorArrowsDispersion * scale
            val width = 0.0625 * scale * 3
            return CursorParameters(length, width)
        }
    }
}