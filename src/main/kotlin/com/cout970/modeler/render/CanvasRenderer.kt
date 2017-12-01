package com.cout970.modeler.render

import com.cout970.glutilities.tessellator.BufferPTNC
import com.cout970.modeler.api.model.selection.SelectionTarget
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
            Light(vec3Of(250, 500, 400), vec3Of(1, 1, 0.95)),
            Light(vec3Of(-250, -500, -400), vec3Of(0.9, 0.9, 1.0))
    )

    fun render(gui: Gui) {
        Profiler.startSection("canvasRender")

        // on each canvas
        gui.canvasContainer.canvas.forEachIndexed { index, canvas ->
            Profiler.startSection("canvas_$index")
            // rendering context
            val ctx = RenderContext(
                    camera = canvas.cameraHandler.camera,
                    lights = lights,
                    viewport = canvas.size.toIVector(),
                    shader = renderManager.shader,
                    gui = gui,
                    buffer = buffer
            )
            // viewportSize
            val viewportPos = vec2Of(
                    canvas.absolutePosition.x,
                    gui.windowHandler.window.size.yf - (canvas.absolutePositionV.yf + canvas.size.y)
            )
            // change view port
            gui.windowHandler.saveViewport(viewportPos, canvas.size.toIVector()) {
                // enable shaders
                renderManager.shader.useShader(ctx) {
                    // if this canvas is 3D
                    if (canvas.viewMode == SelectionTarget.MODEL || canvas.viewMode == SelectionTarget.ANIMATION) {
                        worldRenderer.renderWorld(ctx, gui.modelAccessor.model)
                        centerMarkRenderer.renderCursor(ctx)
                    } else {
                        // if this canvas is only 2D
                        val ref = gui.state.selectedMaterial
                        val material = gui.modelAccessor.model.getMaterial(ref)
                        materialRenderer.renderWorld(ctx, ref, material)
                    }
                }
            }
            Profiler.endSection()
        }
        Profiler.endSection()
    }
}