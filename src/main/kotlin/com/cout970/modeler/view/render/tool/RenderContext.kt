package com.cout970.modeler.view.render.tool

import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.render.tool.camera.Camera
import com.cout970.modeler.view.render.tool.shader.UniversalShader
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
        val buffer: UniversalShader.Buffer
)