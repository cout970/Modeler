package com.cout970.modeler.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.ScrollEvent
import org.liquidengine.legui.icon.ImageIcon

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

    constructor() : this("", 0, 0, 10, 10, "")

    init {
        textState.textColor = Config.colorPalette.textColor.toColor()
        backgroundColor = Config.colorPalette.buttonColor.toColor()
        cornerRadius = 0f
        setBorderless()
        listenerMap.addListener(ScrollEvent::class.java) {
            propagateScroll(it)
        }
    }

    fun propagateScroll(e: ScrollEvent<*>) {
        if (parent is CPanel) {
            parent.listenerMap.getListeners(ScrollEvent::class.java)?.forEach {
                it.process(e)
            }
        }
    }

    override fun setTooltip(tooltip: String) {
        this.setTooltipComponent(CTooltip(tooltip))
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

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }

    override fun toString(): String {
        return "CButton(command='$command')"
    }
}