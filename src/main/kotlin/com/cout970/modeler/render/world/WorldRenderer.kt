package com.cout970.modeler.render.world

import com.cout970.glutilities.tessellator.DrawMode
import com.cout970.matrix.extensions.Matrix4
import com.cout970.matrix.extensions.times
import com.cout970.matrix.extensions.toMutable
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.render.tool.AutoCache
import com.cout970.modeler.render.tool.RenderContext
import com.cout970.modeler.render.tool.createVao
import com.cout970.modeler.render.tool.shader.ShaderFlag
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
    val cursorRenderer = ModelCursorRenderer()

    var baseCubeCache = AutoCache()
    var gridLines = AutoCache()
    var lights = AutoCache()
    var skybox = AutoCache()

    fun renderWorld(ctx: RenderContext, model: IModel) {

        if (ctx.gui.state.renderBaseBlock) {
            renderBaseBlock(ctx)
        }
        if (ctx.gui.state.drawModelGridLines) {
            renderGridLines(ctx)
        }
        if (ctx.gui.state.renderLights) {
            renderLights(ctx)
        }
        if (ctx.gui.state.renderSkybox) {
            renderSkybox(ctx)
        }

        modelRenderer.renderModels(ctx, model)

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        cursorRenderer.renderCursor(ctx)
    }

    fun renderLights(ctx: RenderContext) {
        val vao = lights.getOrCreate(ctx) {
            ctx.gui.resources.lightMesh.createVao(ctx.buffer, vec3Of(1, 1, 0.1))
        }
        ctx.lights.forEach { light ->
            val transform = TRSTransformation(translation = light.pos, scale = Vector3.ONE * 8)
            ctx.shader.render(vao, transform.matrix, ShaderFlag.LIGHT, ShaderFlag.COLOR)
        }
    }

    fun renderBaseBlock(ctx: RenderContext) {
        val vao = baseCubeCache.getOrCreate(ctx) {
            ctx.gui.resources.baseCubeMesh.createVao(ctx.buffer)
        }
        val transform = TRSTransformation(translation = vec3Of(8, -8, 8), scale = Vector3.ONE)
        ctx.gui.resources.baseCubeTexture.bind()
        ctx.shader.render(vao, transform.matrix, ShaderFlag.LIGHT, ShaderFlag.TEXTURE)
    }

    fun renderGridLines(ctx: RenderContext) {
        val vao = gridLines.getOrCreate(ctx) {
            ctx.buffer.build(DrawMode.LINES) {
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
        ctx.shader.render(vao, Matrix4.IDENTITY, ShaderFlag.COLOR)
    }

    fun renderSkybox(ctx: RenderContext) {
        val vao = skybox.getOrCreate(ctx) {
            ctx.gui.resources.skybox.createVao(ctx.buffer)
        }
        val size = 1000
        val transform = TRSTransformation(translation = vec3Of(-4 * size), scale = Vector3.ONE * size)
        val projection = ctx.camera.getProjectionMatrix(ctx.viewport)
        val view = ctx.camera.getViewMatrix().toMutable()

        view.m30d = 0.0
        view.m31d = 0.0
        view.m32d = 0.0

        ctx.gui.resources.skyboxTexture.bind()
        ctx.shader.apply {

            showHiddenFaces.setBoolean(false)
            matrixVP.setMatrix4(projection * view)

            render(vao, transform.matrix, ShaderFlag.TEXTURE)

            showHiddenFaces.setBoolean(ctx.gui.state.showHiddenFaces)
            matrixVP.setMatrix4(ctx.camera.getMatrix(ctx.viewport))
        }
    }
}