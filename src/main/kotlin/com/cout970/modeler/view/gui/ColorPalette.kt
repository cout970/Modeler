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
        val selectedButton: IVector3,
        val textColor: IVector3,
        val modelBackgroundColor: IVector3,
        val textureSelectionColor: IVector3,
        val modelSelectionColor: IVector3,
        val borderColor: IVector3,
        val grid1Color: IVector3,
        val grid2Color: IVector3
) {

    companion object {

        val defaultPalette = ColorPalette(
                darkColor = vec3Of(194, 209, 221) / 255f,
                primaryColor = vec3Of(213, 237, 255) / 255f,
                lightColor = vec3Of(229, 244, 255) / 255f,
                buttonColor = vec3Of(229, 244, 255) / 255f,
                selectedButton = vec3Of(229, 244, 255) / 255f,
                textColor = vec3Of(32, 32, 32) / 255f,
                modelBackgroundColor = vec3Of(126, 201, 255) / 255f,
                textureSelectionColor = vec3Of(255, 0, 0) / 255f,
                modelSelectionColor = vec3Of(0, 0, 255) / 255f,
                borderColor = vec3Of(188, 188, 188) / 255f,
                grid1Color = vec3Of(125, 125, 125) / 255f,
                grid2Color = vec3Of(255, 0, 0) / 255f
        )
    }
}