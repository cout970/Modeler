package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.gui.GuiResources
import com.cout970.modeler.util.IPropertyBind
import org.liquidengine.legui.icon.Icon
import org.liquidengine.legui.icon.ImageIcon
import org.liquidengine.legui.component.ToggleButton as LeguiToggleButton


/**
 * Created by cout970 on 2017/09/08.
 */

class ToggleButton(
        var icon: String = "",
        val default: Boolean = false,
        posX: Float = 0f, posY: Float = 0f,
        sizeX: Float = 16f, sizeY: Float = 16f
) : LeguiToggleButton("", posX, posY, sizeX, sizeY), IResourceReloadable {

    var properties = mapOf<String, IPropertyBind<Boolean>>()

    init {
        classes("toggle_button")
    }

    override fun isToggled(): Boolean = default

    override fun loadResources(resources: GuiResources) {
        resources.getIconOrNull("active_$icon")?.let {
            setImage(ImageIcon(size, it))
        }
        resources.getIconOrNull("disable_$icon")?.let {
            style.background.icon = ImageIcon(it)
        }
    }

    fun setImage(active: Icon) {
        style.background.icon = active
        togglededBackgroundIcon = active
    }
}