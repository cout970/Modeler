package com.cout970.modeler.gui.leguicomp

import org.liquidengine.legui.component.Label

/**
 * Created by cout970 on 2017/09/16.
 */

class FixedLabel(text: String, x: Float = 0f, y: Float = 0f, width: Float = 10f, height: Float = 24f)
    : Label(text, x, y, width, height) {

    constructor() : this("")

    init {
        classes("fixed_label")
    }
}