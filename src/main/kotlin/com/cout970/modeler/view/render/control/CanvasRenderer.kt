package com.cout970.modeler.view.render.control

import com.cout970.modeler.controller.ProjectController
import com.cout970.modeler.controller.World
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.yf

/**
 * Created by cout970 on 2017/05/16.
 */
class CanvasRenderer(val renderManager: RenderManager, val input: IInput) {

    val modelRenderer = ModelRenderer()

    fun render(state: Gui, projectController: ProjectController) {

        state.canvasContainer.canvas.forEach { canvas ->
            val ctx = RenderContext(
                    camera = canvas.cameraHandler.camera,
                    input = input,
                    lights = canvas.lights,
                    viewport = canvas.size.toIVector(),
                    windowHandler = state.windowHandler,
                    timer = state.timer,
                    shader = renderManager.shader,
                    guiState = projectController.guiState
            )
            val viewportPos = vec2Of(
                    canvas.absolutePosition.x,
                    state.windowHandler.window.size.yf - (canvas.absolutePosition.yf + canvas.size.y)
            )
            state.windowHandler.saveViewport(viewportPos, canvas.size.toIVector()) {
                renderCanvas(ctx, projectController.world)
            }
        }
    }

    fun renderCanvas(ctx: RenderContext, world: World) {

        renderManager.shader.useShader(ctx) { buffer, shader ->
            modelRenderer.render(ctx, buffer, world)
        }
    }
}