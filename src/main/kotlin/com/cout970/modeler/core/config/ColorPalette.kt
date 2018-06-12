package com.cout970.modeler.core.config

import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.div
import com.cout970.vector.extensions.vec3Of
import org.joml.Vector4f
import java.awt.Color

/**
 * Created by cout970 on 2017/01/24.
 */
data class ColorPalette(
        val dark3: IVector3,
        val dark2: IVector3,
        val dark1: IVector3,
        val grey: IVector3,
        val bright1: IVector3,
        val bright2: IVector3,
        val bright3: IVector3,
        val bright4: IVector3,

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
                dark3 = hexToColor(0x282828),
                dark2 = hexToColor(0x313131),
                dark1 = hexToColor(0x424242),
                grey = hexToColor(0x545454),
                bright1 = hexToColor(0x626262),
                bright2 = hexToColor(0x707070),
                bright3 = hexToColor(0x878787),
                bright4 = hexToColor(0xFFFFFF),

                buttonColor = hexToColor(0x707070),
                topPanelColor = hexToColor(0x313131),
                selectedButton = hexToColor(0x707070),

                selectedButtonBorder = hexToColor(0x606060),
                textColor = hexToColor(0xffffff),
                modelBackgroundColor = hexToColor(0x141414),
                textureSelectionColor = hexToColor(0x0000ff),
                modelSelectionColor = hexToColor(0xffff00),
                borderColor = hexToColor(0xbcbcbc),
                grid1Color = hexToColor(0x6f6f6f),
                grid2Color = hexToColor(0xf0f0f0),
                selectedOption = hexToColor(0x4b6eaf)
        )

        val clearPalette = ColorPalette(
                dark3 = hexToColor(0x666666),
                dark2 = hexToColor(0x888888),
                dark1 = hexToColor(0x777777),
                grey = hexToColor(0x888888),
                bright1 = hexToColor(0x999999),
                bright2 = hexToColor(0xAAAAAA),
                bright3 = hexToColor(0xBBBBBB),
                bright4 = hexToColor(0xCCCCCC),

                buttonColor = hexToColor(0x707070),
                topPanelColor = hexToColor(0x313131),
                selectedButton = hexToColor(0x707070),

                selectedButtonBorder = hexToColor(0x606060),
                textColor = hexToColor(0xffffff),
                modelBackgroundColor = hexToColor(0x141414),
                textureSelectionColor = hexToColor(0x0000ff),
                modelSelectionColor = hexToColor(0xffff00),
                borderColor = hexToColor(0xbcbcbc),
                grid1Color = hexToColor(0x6f6f6f),
                grid2Color = hexToColor(0xf0f0f0),
                selectedOption = hexToColor(0x4b6eaf)
        )

        val defaultPalette = clearPalette//darkPalette

        fun hexToColor(hex: Int): IVector3 {
            val color = Color(hex)
            return vec3Of(color.red, color.green, color.blue) / 255f
        }

        private val RGBA_PATTERN = """rgba\((\d{1,3}), ?(\d{1,3}), ?(\d{1,3}), ?(\d?\.(\d{1,3})?)\)""".toRegex()

        fun colorOf(argb: String): Vector4f {
            var c = if (argb.startsWith("#")) argb.substring(1) else argb
            if (RGBA_PATTERN.matches(argb)) {
                val match = RGBA_PATTERN.matchEntire(argb)!!
                return Vector4f(match.groupValues[1].toInt() / 255f, match.groupValues[2].toInt() / 255f, match.groupValues[3].toInt() / 255f,
                        match.groupValues[4].toFloat())
            }
            c = if (c.length == 3) "${c[0]}${c[0]}${c[1]}${c[1]}${c[2]}${c[2]}" else c

            val color = Color(c.toInt(16))
            return Vector4f(color.red / 255f, color.green / 255f, color.blue / 255f, 1f)
        }
    }
}