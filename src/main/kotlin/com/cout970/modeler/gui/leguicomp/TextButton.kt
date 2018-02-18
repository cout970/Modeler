package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.ImageIcon

/**
 * Created by cout970 on 2017/09/21.
 */
class TextButton(
        val command: String = "",
        text: String = "",
        posX: Number = 0f,
        posY: Number = 0f,
        sizeX: Number = 32f,
        sizeY: Number = 32f
) : Button(text, posX.toFloat(), posY.toFloat(), sizeX.toFloat(), sizeY.toFloat()) {

    init {
        textState.textColor = Config.colorPalette.textColor.toColor()
        backgroundColor = Config.colorPalette.buttonColor.toColor()
        cornerRadius = 0f
        setBorderless()
    }

    fun setTooltip(tooltip: String) {
        this.tooltip = InstantTooltip(tooltip)
    }

    fun setImage(img: ImageIcon) {
        backgroundIcon = img
        focusedBackgroundIcon = img
        hoveredBackgroundIcon = img
        pressedBackgroundIcon = img
    }

    fun setTextLeft() = this.apply {
        textState.horizontalAlign = HorizontalAlign.LEFT
        textState.padding.x += 5f
    }

    override fun toString(): String = "TextButton(command='$command')"
}