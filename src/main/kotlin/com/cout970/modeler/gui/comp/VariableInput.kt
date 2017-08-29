package com.cout970.modeler.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.GuiResources
import com.cout970.modeler.util.toColor
import org.joml.Vector2f
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.ImageIcon

/**
 * Created by cout970 on 2017/08/29.
 */
class VariableInput(cmd: String, posX: Float, posY: Float) : CPanel(0f, 0f, 75f, 70f) {

    val topButton = CButton("", 0f, 0f, 75f, 16f, cmd + ".up")
    val textField = CTextInput(cmd, "", 0f, 16f, 75f, 40f)
    val bottomButton = CButton("", 0f, 56f, 75f, 16f, cmd + ".down")

    init {
        position = Vector2f(posX, posY)
        add(topButton)
        add(textField)
        add(bottomButton)
        setTransparent()
        setBorderless()
        textField.textState.horizontalAlign = HorizontalAlign.CENTER
        textField.textState.fontSize = 24f
        topButton.backgroundColor = Config.colorPalette.lightDarkColor.toColor()
        bottomButton.backgroundColor = Config.colorPalette.lightDarkColor.toColor()
    }

    override fun loadResources(resources: GuiResources) {
        super.loadResources(resources)

        topButton.setImage(ImageIcon(resources.upIcon))
        bottomButton.setImage(ImageIcon(resources.downIcon))
    }
}