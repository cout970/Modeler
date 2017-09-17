package com.cout970.modeler.gui.react.leguicomp

import com.cout970.modeler.gui.GuiResources
import com.cout970.modeler.gui.IResourceReloadable
import com.cout970.modeler.gui.comp.CTooltip
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.ImageIcon

/**
 * Created by cout970 on 2017/09/15.
 */
class IconButton(
        val id: String = "",
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
}