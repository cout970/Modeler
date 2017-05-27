package com.cout970.modeler.view.render

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.gui.camera.Camera
import com.cout970.modeler.view.render.shader.UniversalShader
import com.cout970.modeler.view.window.WindowHandler
import com.cout970.vector.api.IVector2

/**
 * Created by cout970 on 2017/05/25.
 */
data class RenderContext(
        val windowHandler: WindowHandler,
        val timer: Timer,
        val camera: Camera,
        val viewport: IVector2,
        val input: IInput,
        val lights: List<Light>,
        val shader: UniversalShader
)