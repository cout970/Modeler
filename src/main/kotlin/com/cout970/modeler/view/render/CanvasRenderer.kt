package com.cout970.modeler.view.render

import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.gui.GuiUpdater
import com.cout970.modeler.view.gui.canvas.Canvas
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import com.cout970.vector.extensions.yf
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/05/16.
 */
class CanvasRenderer(val renderManager: RenderManager, val input: IInput) {

    fun render(updater: GuiUpdater) {

        val windowHandler = renderManager.windowHandler

        updater.canvasContainer.canvas.forEach { canvas ->
            val ctx = RenderContext(
                    camera = canvas.state.cameraHandler.camera,
                    input = input,
                    lights = canvas.state.lights,
                    viewport = canvas.size.toIVector(),
                    windowHandler = windowHandler,
                    timer = windowHandler.timer,
                    shader = renderManager.shader
            )
            val viewportPos = vec2Of(
                    canvas.absolutePosition.x,
                    windowHandler.window.size.yf - (canvas.absolutePosition.yf + canvas.size.y)
            )
            windowHandler.saveViewport(viewportPos, canvas.size.toIVector()) {
                renderCanvas(ctx, canvas)
            }
        }
    }

    fun renderCanvas(ctx: RenderContext, canvas: Canvas) {

        renderManager.shader.useShader(ctx) { buffer, consumer ->
            val tmp = buffer.build(GL11.GL_LINES) {
                add(vec3Of(0, 0, 0), vec2Of(0, 0), vec3Of(1, 0, 0), vec3Of(1, 0, 0))
                add(vec3Of(10, 0, 0), vec2Of(0, 0), vec3Of(1, 0, 0), vec3Of(0, 1, 0))
                add(vec3Of(0, 0, 0), vec2Of(0, 0), vec3Of(1, 0, 0), vec3Of(1, 0, 0))
                add(vec3Of(0, 10, 0), vec2Of(0, 0), vec3Of(1, 0, 0), vec3Of(0, 1, 0))
                newRegion(GL11.GL_QUADS)
                add(vec3Of(0, 0, 0), vec2Of(0, 0), vec3Of(1, 0, 0), vec3Of(1, 0, 0))
                add(vec3Of(-10, 0, 0), vec2Of(0, 0), vec3Of(1, 0, 0), vec3Of(0, 1, 0))
                add(vec3Of(-10, -10, 0), vec2Of(0, 0), vec3Of(1, 0, 0), vec3Of(1, 0, 0))
                add(vec3Of(0, -10, 0), vec2Of(0, 0), vec3Of(1, 0, 0), vec3Of(0, 1, 0))
            }
            consumer.accept(tmp)
            tmp.close()
        }
    }

}
