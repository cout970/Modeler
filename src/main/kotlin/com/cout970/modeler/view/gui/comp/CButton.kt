package com.cout970.modeler.view.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.ImageView
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/01/24.
 */
class CButton(
        text: String,
        posX: Number,
        posY: Number,
        sizeX: Number,
        sizeY: Number,
        val command: String
) : Button(text, posX.toFloat(), posY.toFloat(), sizeX.toFloat(), sizeY.toFloat()) {

    init {
        textState.textColor = Config.colorPalette.textColor.toColor()
        backgroundColor = Config.colorPalette.buttonColor.toColor()
    }

    fun setImage(img: ImageView) {
        backgroundImage = img
        focusedBbackgroundImage = img
        hoveredBackgroundImage = img
        pressedBackgroundImage = img
    }

    fun setTextLeft() = this.apply {
        textState.horizontalAlign = HorizontalAlign.LEFT
        textState.padding.x += 5f
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }
}