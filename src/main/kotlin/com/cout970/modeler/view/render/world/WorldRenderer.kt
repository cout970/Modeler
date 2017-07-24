package com.cout970.modeler.view.render.world

import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.view.render.tool.AutoCache
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.modeler.view.render.tool.createVao
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.times
import com.cout970.vector.extensions.vec3Of
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/06/14.
 */

class WorldRenderer {

    val modelRenderer = ModelRenderer()
    val cursorRenderer = CursorRenderer()
    var baseCubeCache = AutoCache()
    var gridLines = AutoCache()
    var lights = AutoCache()

    fun renderWorld(ctx: RenderContext, model: IModel) {
        renderBaseBlock(ctx)
        if (ctx.gui.state.drawModelGridLines) {
            renderGridLines(ctx)
        }
        if (ctx.gui.state.renderLights) {
            renderLights(ctx)
        }
        modelRenderer.renderModels(ctx, model)

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        cursorRenderer.renderCursor(ctx)
    }

    fun renderLights(ctx: RenderContext) {
        val vao = lights.getOrCreate(ctx) {
            ctx.gui.resources.lightMesh.createVao(ctx.buffer, vec3Of(1, 1, 0))
        }
        ctx.shader.apply {
            useColor.setInt(1)
            useLight.setInt(0)
            useTexture.setInt(0)
            ctx.lights.forEach { light ->
                matrixM.setMatrix4(TRSTransformation(
                        translation = light.pos,
                        scale = Vector3.ONE * 8
                ).matrix)
                accept(vao)
            }
        }
    }

    fun renderBaseBlock(ctx: RenderContext) {
        val vao = baseCubeCache.getOrCreate(ctx) {
            ctx.gui.resources.baseCubeMesh.createVao(ctx.buffer)
        }
        ctx.shader.apply {
            useColor.setInt(0)
            useLight.setInt(1)
            useTexture.setInt(1)
            matrixM.setMatrix4(TRSTransformation(
                    translation = vec3Of(8, -8, 8),
                    scale = Vector3.ONE).matrix
            )
            ctx.gui.resources.baseCubeTexture.bind()
            accept(vao)
        }
    }

    fun renderGridLines(ctx: RenderContext) {
        val vao = gridLines.getOrCreate(ctx) {
            ctx.buffer.build(GL11.GL_LINES) {
                val size = 16 * 4
                val min = -size
                val max = size + 16

                for (x in min..max) {
                    val color = if (x % 16 == 0) Config.colorPalette.grid2Color else Config.colorPalette.grid1Color
                    add(vec3Of(x, 0, min), Vector2.ORIGIN, Vector3.ORIGIN, color)
                    add(vec3Of(x, 0, max), Vector2.ORIGIN, Vector3.ORIGIN, color)
                }
                for (z in min..max) {
                    val color = if (z % 16 == 0) Config.colorPalette.grid2Color else Config.colorPalette.grid1Color
                    add(vec3Of(min, 0, z), Vector2.ORIGIN, Vector3.ORIGIN, color)
                    add(vec3Of(max, 0, z), Vector2.ORIGIN, Vector3.ORIGIN, color)
                }
            }
        }
        ctx.shader.apply {
            useColor.setInt(1)
            useLight.setInt(0)
            useTexture.setInt(0)
            matrixM.setMatrix4(Matrix4.IDENTITY)
            accept(vao)
        }
    }
}