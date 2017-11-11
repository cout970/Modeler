package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.gui.GuiResources
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.ImageIcon

/**
 * Created by cout970 on 2017/09/15.
 */
class IconButton(
        val command: String = "",
        val icon: String = "",
        posX: Float = 0f, posY: Float = 0f,
        sizeX: Float = 16f, sizeY: Float = 16f
) : Button("", posX, posY, sizeX, sizeY), IResourceReloadable {

    init {
        setBorderless()
        setTransparent()
        cornerRadius = 0f
    }

    override fun loadResources(resources: GuiResources) {
        resources.getIcon(icon)?.let {
            setImage(ImageIcon(it))
        }
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

    override fun toString(): String = "IconButton(id='$command', icon='$icon')"
}