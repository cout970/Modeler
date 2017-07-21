package com.cout970.modeler.view.render

import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.core.model.material.MaterialRef
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.render.texture.MaterialRenderer
import com.cout970.modeler.view.render.tool.Light
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.modeler.view.render.tool.shader.UniversalShader
import com.cout970.modeler.view.render.world.CenterMarkRenderer
import com.cout970.modeler.view.render.world.WorldRenderer
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import com.cout970.vector.extensions.yf

/**
 * Created by cout970 on 2017/05/16.
 */
class CanvasRenderer(val renderManager: RenderManager) {

    val worldRenderer = WorldRenderer()
    val materialRenderer = MaterialRenderer()
    val centerMarkRenderer = CenterMarkRenderer()

    val buffer = UniversalShader.Buffer()
    val lights: List<Light> = listOf(
            Light(vec3Of(250, 500, 400), Vector3.ONE),
            Light(vec3Of(-250, -500, -400), Vector3.ONE)
    )

    fun render(gui: Gui) {

        gui.canvasContainer.canvas.forEach { canvas ->
            val ctx = RenderContext(
                    camera = canvas.cameraHandler.camera,
                    lights = lights,
                    viewport = canvas.size.toIVector(),
                    shader = renderManager.shader,
                    gui = gui,
                    buffer = buffer
            )
            val viewportPos = vec2Of(
                    canvas.absolutePosition.x,
                    gui.windowHandler.window.size.yf - (canvas.absolutePosition.yf + canvas.size.y)
            )
            gui.windowHandler.saveViewport(viewportPos, canvas.size.toIVector()) {
                renderManager.shader.useShader(ctx) {
                    if (canvas.viewMode == SelectionTarget.MODEL) {
                        worldRenderer.renderWorld(ctx, gui.projectManager.model)
                        centerMarkRenderer.renderCursor(ctx)
                    } else {
                        materialRenderer.renderWorld(ctx, gui.projectManager.model.getMaterial(MaterialRef(0)))
                    }
                }
            }
        }
    }
}