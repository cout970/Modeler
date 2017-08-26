package com.cout970.modeler.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.util.IPropertyBind
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.border.SimpleLineBorder
import org.liquidengine.legui.component.ToggleButton
import org.liquidengine.legui.icon.Icon

/**
 * Created by cout970 on 2017/01/24.
 */
class CToggleButton(posX: Number, posY: Number, sizeX: Number, sizeY: Number,
                    val default: Boolean = false, val bindCallback: (GuiState) -> IPropertyBind<Boolean>)
    : ToggleButton(posX.toFloat(), posY.toFloat(), sizeX.toFloat(), sizeY.toFloat()) {

    lateinit var bind: IPropertyBind<Boolean>

    init {
        setTransparent()
        toggledBackgroundColor = Config.colorPalette.selectedButton.toColor()
        border.isEnabled = false
        border = SimpleLineBorder(Config.colorPalette.selectedButton.toColor(), 1f)
    }

    override fun isToggled(): Boolean {
        return bind.get()
    }

    override fun setToggled(toggled: Boolean) {
        bind.set(toggled)
        super.setToggled(toggled)
        border.isEnabled = toggled
    }

    fun bindState(state: GuiState) {
        bind = bindCallback(state)
        isToggled = default
    }

    fun setImage(active: Icon, notActive: Icon = active) {
        backgroundIcon = active
        togglededBackgroundIcon = active
        focusedBackgroundIcon = active
        hoveredBackgroundIcon = active
        pressedBackgroundIcon = active
    }
}