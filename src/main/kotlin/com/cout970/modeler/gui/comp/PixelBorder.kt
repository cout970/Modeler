package com.cout970.modeler.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.joml.Vector4f
import org.liquidengine.legui.border.Border

/**
 * Created by cout970 on 2017/08/29.
 */
class PixelBorder : Border() {

    var color: Vector4f = Config.colorPalette.blackColor.toColor()

    var enableTop = false
    var enableBottom = false
    var enableLeft = false
    var enableRight = false
}