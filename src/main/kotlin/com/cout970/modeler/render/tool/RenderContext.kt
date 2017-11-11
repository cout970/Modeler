package com.cout970.modeler.render.tool

import com.cout970.glutilities.tessellator.BufferPTNC
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.render.tool.camera.Camera
import com.cout970.modeler.render.tool.shader.UniversalShader
import com.cout970.vector.api.IVector2

/**
 * Created by cout970 on 2017/05/25.
 */
data class RenderContext(
        val gui: Gui,
        val camera: Camera,
        val viewport: IVector2,
        val lights: List<Light>,
        val shader: UniversalShader,
        val buffer: BufferPTNC
)