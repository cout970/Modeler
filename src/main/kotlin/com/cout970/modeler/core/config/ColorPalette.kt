package com.cout970.modeler.core.config

import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.div
import com.cout970.vector.extensions.vec3Of
import java.awt.Color

/**
 * Created by cout970 on 2017/01/24.
 */
data class ColorPalette(
        val darkestColor: IVector3,
        val darkColor: IVector3,
        val lightDarkColor: IVector3,
        val greyColor: IVector3,
        val lightBrightColor: IVector3,
        val brightColor: IVector3,
        val brightestColor: IVector3,

        val buttonColor: IVector3,
        val topPanelColor: IVector3,
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
                darkestColor = hexToColor(0x282828),
                darkColor = hexToColor(0x383838),
                lightDarkColor = hexToColor(0x464646),
                greyColor = hexToColor(0x545454),
                lightBrightColor = hexToColor(0x626262),
                brightColor = hexToColor(0x707070),
                brightestColor = hexToColor(0x878787),

                buttonColor = hexToColor(0x707070),
                topPanelColor = hexToColor(0x484848),
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