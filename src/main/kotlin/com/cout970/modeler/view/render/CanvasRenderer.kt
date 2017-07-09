package com.cout970.modeler.view.render

import com.cout970.modeler.controller.World
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.modeler.view.render.tool.shader.UniversalShader
import com.cout970.modeler.view.render.world.WorldRenderer
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.yf

/**
 * Created by cout970 on 2017/05/16.
 */
class CanvasRenderer(val renderManager: RenderManager, val input: IInput) {

    val worldRenderer = WorldRenderer()
    val buffer = UniversalShader.Buffer()

    fun render(gui: Gui) {

        gui.canvasContainer.canvas.forEach { canvas ->
            val ctx = RenderContext(
                    camera = canvas.cameraHandler.camera,
                    input = input,
                    lights = canvas.lights,
                    viewport = canvas.size.toIVector(),
                    windowHandler = gui.windowHandler,
                    timer = gui.timer,
                    shader = renderManager.shader,
                    gui = gui,
                    buffer = buffer,
                    resources = gui.resources
            )
            val viewportPos = vec2Of(
                    canvas.absolutePosition.x,
                    gui.windowHandler.window.size.yf - (canvas.absolutePosition.yf + canvas.size.y)
            )
            gui.windowHandler.saveViewport(viewportPos, canvas.size.toIVector()) {
                renderManager.shader.useShader(ctx) {
                    worldRenderer.renderWorld(ctx, World(listOf(gui.projectManager.model), gui.selector.cursor))
                }
            }
        }
    }
}