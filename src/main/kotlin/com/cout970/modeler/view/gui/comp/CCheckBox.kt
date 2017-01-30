package com.cout970.modeler.view.gui.comp

import com.cout970.modeler.config.Config
import com.cout970.modeler.util.IPropertyBind
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.CheckBox
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/01/27.
 */
class CCheckBox(name: String, x: Float, y: Float, width: Float, height: Float, val bind: IPropertyBind<Boolean>)
    : CheckBox(name, x, y, width, height) {

    init {
        textState.horizontalAlign = HorizontalAlign.LEFT
        textState.textColor = Config.colorPalette.textColor.toColor()
        backgroundColor = Config.colorPalette.buttonColor.toColor()
    }

    override fun isChecked(): Boolean {
        isChecked = bind.get()
        return super.isChecked()
    }

    override fun setChecked(checked: Boolean) {
        bind.set(checked)
        super.setChecked(bind.get())
    }
}