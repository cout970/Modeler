package com.cout970.modeler.view.gui

import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.div
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/01/24.
 */
data class ColorPalette(
        val darkColor: IVector3,
        val primaryColor: IVector3,
        val lightColor: IVector3,
        val buttonColor: IVector3,
        val textColor: IVector3,
        val modelBackgroundColor: IVector3,
        val textureSelectionColor: IVector3,
        val modelSelectionColor: IVector3,
        val borderColor: IVector3
) {

    companion object {

        val materializePalette = ColorPalette(
                darkColor = vec3Of(1, 135, 208) / 255f,
                primaryColor = vec3Of(2, 168, 243) / 255f,
                lightColor = vec3Of(178, 228, 251) / 255f,
                buttonColor = vec3Of(254, 254, 254) / 255f,
                textColor = vec3Of(32, 32, 32) / 255f,
                modelBackgroundColor = vec3Of(1, 1, 1) / 255f,
                textureSelectionColor = vec3Of(255, 0, 0) / 255f,
                modelSelectionColor = vec3Of(255, 255, 0) / 255f,
                borderColor = vec3Of(188, 188, 188) / 255f
        )
        val defaultPalette = ColorPalette(
                darkColor = vec3Of(194, 209, 221) / 255f,
                primaryColor = vec3Of(213, 237, 255) / 255f,
                lightColor = vec3Of(229, 244, 255) / 255f,
                buttonColor = vec3Of(229, 244, 255) / 255f,
                textColor = vec3Of(32, 32, 32) / 255f,
                modelBackgroundColor = vec3Of(126, 201, 255) / 255f,
                textureSelectionColor = vec3Of(255, 0, 0) / 255f,
                modelSelectionColor = vec3Of(0, 0, 255) / 255f,
                borderColor = vec3Of(188, 188, 188) / 255f
        )
    }
}