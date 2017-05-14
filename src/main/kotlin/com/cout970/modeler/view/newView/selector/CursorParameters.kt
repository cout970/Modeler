package com.cout970.modeler.view.newView.selector

import com.cout970.modeler.core.config.Config
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.yd

/**
 * Created by cout970 on 2017/02/04.
 */
class CursorParameters(
        val distanceFromCenter: Double,
        val minSizeOfSelectionBox: Double,
        val maxSizeOfSelectionBox: Double
) {

    companion object {

        fun create(zoom: Double, containerSize: IVector2): CursorParameters {
            val scale = zoom / 10 * Config.cursorArrowsScale * (1000 / containerSize.yd)
            val distanceFromCenter = Config.cursorArrowsDispersion * scale
            val minSizeOfSelectionBox = 0.0625 * scale
            val maxSizeOfSelectionBox = 0.0625 * scale * 5
            return CursorParameters(distanceFromCenter, minSizeOfSelectionBox, maxSizeOfSelectionBox)
        }
    }
}