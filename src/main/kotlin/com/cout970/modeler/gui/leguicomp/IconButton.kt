package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.gui.GuiResources
import com.cout970.reactive.dsl.borderless
import com.cout970.reactive.dsl.rectCorners
import com.cout970.reactive.dsl.transparent
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.optional.align.HorizontalAlign
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
        hoveredStyle.background.color = color { brightColor }
        borderless()
        transparent()
        rectCorners()
    }

    override fun loadResources(resources: GuiResources) {
        resources.getIcon(icon)?.let {
            setImage(ImageIcon(it))
        }
    }

    fun setTooltip(tooltip: String) {
        this.tooltip = InstantTooltip(tooltip)
    }

    fun setImage(img: Icon) {
        style.background.icon = img
    }

    fun setTextLeft() = this.apply {
        textState.horizontalAlign = HorizontalAlign.LEFT
        textState.padding.x += 5f
    }

    override fun toString(): String = "IconButton(id='$command', icon='$icon')"
}