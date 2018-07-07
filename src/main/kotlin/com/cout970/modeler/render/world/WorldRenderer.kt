package com.cout970.modeler.render.world

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.BufferPTNC
import com.cout970.glutilities.tessellator.DrawMode
import com.cout970.matrix.extensions.Matrix4
import com.cout970.matrix.extensions.times
import com.cout970.matrix.extensions.toMutable
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.render.tool.AutoCache
import com.cout970.modeler.render.tool.CacheFlags
import com.cout970.modeler.render.tool.RenderContext
import com.cout970.modeler.render.tool.createVao
import com.cout970.modeler.render.tool.shader.ShaderFlag
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/06/14.
 */

class WorldRenderer {

    val modelRenderer = ModelRenderer()
    val cursorRenderer = ModelCursorRenderer()

    var baseCubeCache = AutoCache()
    var orientationCubeCache = AutoCache()
    var gridLinesPixel = AutoCache(CacheFlags.GRID_LINES)
    var gridLinesBlock = AutoCache(CacheFlags.GRID_LINES)
    var lights = AutoCache()
    var skybox = AutoCache()
    var pivot = AutoCache()

    fun renderWorld(ctx: RenderContext, model: IModel) {

        if (ctx.gui.state.renderSkybox) {
            renderSkybox(ctx)
        }

        if (ctx.gui.state.renderBaseBlock) {
            renderBaseBlock(ctx)
        }

        renderGridLines(ctx)


        if (ctx.gui.state.renderLights) {
            renderLights(ctx)
        }

        modelRenderer.renderModels(ctx, model)

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        renderOrientationCube(ctx)

        renderPivot(ctx)

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

    fun renderPivot(ctx: RenderContext) {
        val vao = pivot.getOrCreate(ctx) {
            ctx.gui.resources.lightMesh.createVao(ctx.buffer, vec3Of(0, 0, 1))
        }
        val acc = ctx.gui.animator
        val channel = acc.selectedChannel ?: return
        val keyframe = acc.selectedKeyframe ?: return

        val key = acc.animation.channels[channel]?.keyframes?.get(keyframe) ?: return
        val value = key.value as? TRTSTransformation ?: return

        val transform = TRSTransformation(translation = value.pivot, scale = Vector3.ONE * 0.25)
        ctx.shader.render(vao, transform.matrix, ShaderFlag.LIGHT, ShaderFlag.COLOR)
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
        if (!ctx.gui.gridLines.enableXPlane &&
                !ctx.gui.gridLines.enableYPlane &&
                !ctx.gui.gridLines.enableZPlane) return

        val pixelVao = gridLinesPixel.getOrCreate(ctx) {
            ctx.buffer.build(DrawMode.LINES) { renderGridLines(ctx, true) }
        }
        val blockVao = gridLinesBlock.getOrCreate(ctx) {
            ctx.buffer.build(DrawMode.LINES) { renderGridLines(ctx, false) }
        }
        val vao = if (ctx.camera.zoom < Config.zoomLevelToChangeGridDetail) pixelVao else blockVao

        ctx.shader.render(vao, Matrix4.IDENTITY, ShaderFlag.COLOR)
    }

    private fun BufferPTNC.renderGridLines(ctx: RenderContext, pixel: Boolean) {
        val size = ctx.gui.gridLines.gridSize
        val offset = ctx.gui.gridLines.gridOffset
        val xRange = (-size.xi / 2 + 8..size.xi / 2 + 8)
        val yRange = (-size.yi / 2 + 8..size.yi / 2 + 8)
        val zRange = (-size.zi / 2 + 8..size.zi / 2 + 8)

        if (ctx.gui.gridLines.enableXPlane) {
            for (z in zRange) {
                val color = if (z % 16 == 0) Config.colorPalette.grid2Color else Config.colorPalette.grid1Color
                if (!pixel && z % 16 != 0) continue
                add(offset + vec3Of(0, yRange.first, z), Vector2.ORIGIN, Vector3.ORIGIN, color)
                add(offset + vec3Of(0, yRange.last, z), Vector2.ORIGIN, Vector3.ORIGIN, color)
            }
            for (y in yRange) {
                val color = if (y % 16 == 0) Config.colorPalette.grid2Color else Config.colorPalette.grid1Color
                if (!pixel && y % 16 != 0) continue
                add(offset + vec3Of(0, y, zRange.first), Vector2.ORIGIN, Vector3.ORIGIN, color)
                add(offset + vec3Of(0, y, zRange.last), Vector2.ORIGIN, Vector3.ORIGIN, color)
            }
        }

        if (ctx.gui.gridLines.enableYPlane) {
            for (x in xRange) {
                val color = if (x % 16 == 0) Config.colorPalette.grid2Color else Config.colorPalette.grid1Color
                if (!pixel && x % 16 != 0) continue
                add(offset + vec3Of(x, 0, zRange.first), Vector2.ORIGIN, Vector3.ORIGIN, color)
                add(offset + vec3Of(x, 0, zRange.last), Vector2.ORIGIN, Vector3.ORIGIN, color)
            }
            for (z in zRange) {
                val color = if (z % 16 == 0) Config.colorPalette.grid2Color else Config.colorPalette.grid1Color
                if (!pixel && z % 16 != 0) continue
                add(offset + vec3Of(xRange.first, 0, z), Vector2.ORIGIN, Vector3.ORIGIN, color)
                add(offset + vec3Of(xRange.last, 0, z), Vector2.ORIGIN, Vector3.ORIGIN, color)
            }
        }

        if (ctx.gui.gridLines.enableZPlane) {
            for (x in xRange) {
                val color = if (x % 16 == 0) Config.colorPalette.grid2Color else Config.colorPalette.grid1Color
                if (!pixel && x % 16 != 0) continue
                add(offset + vec3Of(x, yRange.first, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
                add(offset + vec3Of(x, yRange.last, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
            }
            for (y in yRange) {
                val color = if (y % 16 == 0) Config.colorPalette.grid2Color else Config.colorPalette.grid1Color
                if (!pixel && y % 16 != 0) continue
                add(offset + vec3Of(xRange.first, y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
                add(offset + vec3Of(xRange.last, y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
            }
        }
    }

    fun renderSkybox(ctx: RenderContext) {
        if (!ctx.camera.perspective)
            return

        val vao = skybox.getOrCreate(ctx) {
            val transform = TRSTransformation(translation = vec3Of(-1), scale = vec3Of(4))
            ctx.gui.resources.skybox.transform(transform).createVao(ctx.buffer)
        }

        val projection = ctx.camera.getProjectionMatrix(ctx.viewport)
        val view = ctx.camera.getViewMatrix().toMutable()

        view.m30d = 0.0
        view.m31d = 0.0
        view.m32d = 0.0

        GLStateMachine.depthTest.disable()
        ctx.gui.resources.skyboxTexture.bind(1)
        ctx.shader.apply {

            showHiddenFaces.setBoolean(false)
            matrixVP.setMatrix4(projection * view)
            render(vao, Matrix4.IDENTITY, ShaderFlag.TEXTURE, ShaderFlag.CUBEMAP)

            showHiddenFaces.setBoolean(ctx.gui.state.showHiddenFaces)
            matrixVP.setMatrix4(ctx.camera.getMatrix(ctx.viewport))
        }
        GLStateMachine.depthTest.enable()
    }

    fun renderOrientationCube(ctx: RenderContext) {

        ctx.gui.windowHandler.saveViewport(ctx.viewportPos, vec2Of(150, 150)) {


            val vao = orientationCubeCache.getOrCreate(ctx) {
                ctx.gui.resources.orientationCubeMesh.createVao(ctx.buffer)
            }
            val transform = TRSTransformation(translation = vec3Of(-8, -8, -8), scale = vec3Of(2))
            ctx.gui.resources.orientationCube.bind()

            ctx.shader.matrixVP.setMatrix4(ctx.camera.getMatrixForOrientationCube())
            ctx.shader.render(vao, transform.matrix, ShaderFlag.TEXTURE)
            ctx.shader.matrixVP.setMatrix4(ctx.camera.getMatrix(ctx.viewport))
        }
    }
}