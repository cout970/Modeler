package com.cout970.modeler.render

import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.render.texture.MaterialRenderer
import com.cout970.modeler.render.tool.Light
import com.cout970.modeler.render.tool.RenderContext
import com.cout970.modeler.render.tool.shader.UniversalShader
import com.cout970.modeler.render.world.CenterMarkRenderer
import com.cout970.modeler.render.world.WorldRenderer
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
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
                        val ref = gui.state.selectedMaterial
                        val material = gui.projectManager.model.getMaterial(ref)
                        materialRenderer.renderWorld(ctx, ref, material)
                    }
                }
            }
        }
    }
}