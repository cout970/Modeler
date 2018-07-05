package com.cout970.modeler.render

import com.cout970.glutilities.tessellator.BufferPTNC
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.render.texture.MaterialRenderer
import com.cout970.modeler.render.tool.Light
import com.cout970.modeler.render.tool.RenderContext
import com.cout970.modeler.render.world.CenterMarkRenderer
import com.cout970.modeler.render.world.WorldRenderer
import com.cout970.modeler.util.absolutePositionV
import com.cout970.modeler.util.toIVector
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/05/16.
 */
class CanvasRenderer(val renderManager: RenderManager) {

    val worldRenderer = WorldRenderer()
    val materialRenderer = MaterialRenderer()
    val centerMarkRenderer = CenterMarkRenderer()

    val buffer = BufferPTNC()
    val lights: List<Light> = listOf(
            Light(vec3Of(0, -432, 0), vec3Of(1.0, 1.0, 1.0)),
            Light(vec3Of(500, 432, 0), vec3Of(1.0, 1.0, 1.0)),
            Light(vec3Of(-432, 432, -432), vec3Of(1.0, 1.0, 1.0)),
            Light(vec3Of(-432, 432, 432), vec3Of(1.0, 1.0, 1.0))
    )

    fun render(gui: Gui) {
        Profiler.startSection("canvasRender")

        // on each canvas
        gui.canvasContainer.canvas.forEachIndexed { index, canvas ->
            Profiler.startSection("canvas_$index")

            var viewport = canvas.size.toIVector()

            if (viewport.xi % 2 != 0) {
                viewport = vec2Of(viewport.xi - 1, viewport.yi)
            }
            if (viewport.yi % 2 != 0) {
                viewport = vec2Of(viewport.xi, viewport.yi - 1)
            }

            // viewportSize
            val viewportPos = vec2Of(
                    canvas.absolutePosition.x,
                    gui.windowHandler.window.size.yf - (canvas.absolutePositionV.yf + canvas.size.y)
            )

            // rendering context
            val ctx = RenderContext(
                    camera = canvas.cameraHandler.camera,
                    lights = lights,
                    viewport = viewport,
                    viewportPos = viewportPos,
                    shader = renderManager.shader,
                    gui = gui,
                    buffer = buffer,
                    canvas = canvas
            )

            // change viewport
            gui.windowHandler.saveViewport(viewportPos, viewport) {
                renderManager.shader.useShader(ctx) {
                    if (canvas.viewMode.is3D) render3D(ctx) else render2D(ctx)
                }
            }
            Profiler.endSection()
        }
        Profiler.endSection()
    }

    private fun render3D(ctx: RenderContext) {
        worldRenderer.renderWorld(ctx, ctx.gui.programState.model)
        centerMarkRenderer.renderCursor(ctx)
    }

    private fun render2D(ctx: RenderContext) {
        val ref = ctx.gui.state.selectedMaterial
        val material = ctx.gui.programState.model.getMaterial(ref)
        materialRenderer.renderWorld(ctx, ref, material)
    }
}