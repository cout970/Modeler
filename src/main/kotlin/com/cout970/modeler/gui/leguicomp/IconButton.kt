package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.gui.GuiResources
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.icon.Icon
import org.liquidengine.legui.icon.ImageIcon

/**
 * Created by cout970 on 2017/09/15.
 */
class IconButton(
        var command: String = "",
        var icon: String = "",
        posX: Float = 0f, posY: Float = 0f,
        sizeX: Float = 16f, sizeY: Float = 16f
) : Button("", posX, posY, sizeX, sizeY), IResourceReloadable {

    init {
        classes("icon_button")
    }

    override fun loadResources(resources: GuiResources) {
        if (icon.isNotBlank()) {
            setImage(ImageIcon(resources.getIcon(icon)))
        }
    }

    fun setTooltip(tooltip: String) {
        this.tooltip = InstantTooltip(tooltip)
    }

    fun setImage(img: Icon) {
        style.background.icon = img
    }

    override fun toString(): String = "IconButton(id='$command', icon='$icon')"
}