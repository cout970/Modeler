package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import com.cout970.reactive.dsl.borderless
import com.cout970.reactive.dsl.rectCorners
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
        hoveredStyle.background.color = color { brightestColor }
        background { buttonColor }
        rectCorners()
        borderless()
    }

    fun setTooltip(tooltip: String) {
        this.tooltip = InstantTooltip(tooltip)
    }

    fun setImage(img: ImageIcon) {
        style.background.icon = img
    }

    fun setTextLeft() = this.apply {
        textState.horizontalAlign = HorizontalAlign.LEFT
        textState.padding.x += 5f
    }

    override fun toString(): String = "TextButton(command='$command')"
}