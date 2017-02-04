package com.cout970.modeler.util

import com.cout970.modeler.config.Config
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.yd

/**
 * Created by cout970 on 2017/02/04.
 */
class CursorParameters(val center: IVector3, zoom: Double, containerSize: IVector2) {

    val distanceFromCenter: Double
    val minSizeOfSelectionBox: Double
    val maxSizeOfSelectionBox: Double

    init {
        val scale = zoom / 10 * Config.cursorArrowsScale * (1000 / containerSize.yd)
        this.distanceFromCenter = Config.cursorArrowsDispersion * scale
        this.minSizeOfSelectionBox = 0.0625 * scale
        this.maxSizeOfSelectionBox = 0.0625 * scale * 5
    }
}