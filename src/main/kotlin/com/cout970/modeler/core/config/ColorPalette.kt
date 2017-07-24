package com.cout970.modeler.core.config

import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.div
import com.cout970.vector.extensions.vec3Of
import java.awt.Color

/**
 * Created by cout970 on 2017/01/24.
 */
data class ColorPalette(
        val darkColor: IVector3,
        val primaryColor: IVector3,
        val lightColor: IVector3,
        val buttonColor: IVector3,
        val selectedButton: IVector3,
        val selectedButtonBorder: IVector3,
        val textColor: IVector3,
        val modelBackgroundColor: IVector3,
        val textureSelectionColor: IVector3,
        val modelSelectionColor: IVector3,
        val borderColor: IVector3,
        val grid1Color: IVector3,
        val grid2Color: IVector3,
        val selectedOption: IVector3
) {

    companion object {

        val darkPalette = ColorPalette(
                darkColor = hexToColor(0x383838),
                primaryColor = hexToColor(0x545454),
                lightColor = hexToColor(0x474747),
                buttonColor = hexToColor(0x8b8b8b),
                selectedButton = hexToColor(0x707070),
                selectedButtonBorder = hexToColor(0x606060),
                textColor = hexToColor(0xffffff),
                modelBackgroundColor = hexToColor(0x252525),
                textureSelectionColor = hexToColor(0xffff00),
                modelSelectionColor = hexToColor(0xffff00),
                borderColor = hexToColor(0xbcbcbc),
                grid1Color = hexToColor(0x6f6f6f),
                grid2Color = hexToColor(0xf0f0f0),
                selectedOption = hexToColor(0x4b6eaf)
        )

        val defaultPalette = darkPalette

        fun hexToColor(hex: Int): IVector3 {
            val color = Color(hex)
            return vec3Of(color.red, color.green, color.blue) / 255f
        }
    }
}