package com.cout970.modeler.view.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.border.SimpleLineBorder
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import javax.script.ScriptEngineManager

/**
 * Created by cout970 on 2017/06/21.
 */
class CTextInput(val id: String, text: String = "", x: Float = 0f, y: Float = 0f, width: Float = 60f,
                 height: Float = 18f)
    : TextInput(text, x, y, width, height) {

    init {
        textState.textColor = Config.colorPalette.textColor.toColor()
        backgroundColor = Config.colorPalette.greyColor.toColor()
        border = SimpleLineBorder(Config.colorPalette.greyColor.toColor(), 0.5f)
        cornerRadius = 0f
        textState.horizontalAlign = HorizontalAlign.RIGHT
    }

    companion object {
        val scriptEngine = ScriptEngineManager().getEngineByName("JavaScript")!!
    }
}