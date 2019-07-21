package com.cout970.modeler.core.config

import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.div
import com.cout970.vector.extensions.vec3Of
import org.joml.Vector4f
import java.awt.Color

/**
 * Created by cout970 on 2017/01/24.
 */

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

fun colorToHex(color: IVector3): String {
    val r = (color.xf * 255).toInt().coerceIn(0, 255).toString(16).let { if (it.length == 1) "$it$it" else it }
    val g = (color.yf * 255).toInt().coerceIn(0, 255).toString(16).let { if (it.length == 1) "$it$it" else it }
    val b = (color.zf * 255).toInt().coerceIn(0, 255).toString(16).let { if (it.length == 1) "$it$it" else it }
    return "$r$g$b".toUpperCase()
}