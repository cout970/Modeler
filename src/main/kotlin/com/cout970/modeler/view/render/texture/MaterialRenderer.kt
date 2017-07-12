package com.cout970.modeler.view.render.texture

import com.cout970.glutilities.tessellator.VAO
import com.cout970.matrix.extensions.Matrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.MatrixUtils
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/07/11.
 */
class MaterialRenderer {

    var gridLines: VAO? = null
    var materialVao: VAO? = null
    var outlineVao: VAO? = null
    var materialHash = -1
    var modelHash = -1
    var visibleHash = -1

    fun renderWorld(ctx: RenderContext, material: IMaterial) {
        setCamera(ctx)
        resetIfNeeded(ctx, material)
        if (ctx.gui.state.drawTextureGridLines) {
            renderGridLines(ctx, material)
        }
        renderMaterial(ctx, material)
        renderModelOutlines(ctx, material)
    }

    fun renderModelOutlines(ctx: RenderContext, material: IMaterial) {
        if (outlineVao == null) {
            val model = ctx.gui.projectManager.model
            val objs = model.objectRefs
                    .filter { model.isVisible(it) }
                    .map { model.getObject(it) }
            val color = Config.colorPalette.textureSelectionColor

            outlineVao = ctx.buffer.build(GL11.GL_LINES) {
                objs.forEach { obj ->
                    val mesh = obj.transformedMesh
                    mesh.faces.forEach { face ->
                        val positions = face.tex
                                .map { mesh.tex[it] }
                                .map { vec2Of(it.xd, 1 - it.yd) }
                                .map { it * material.size }
                        positions.indices.forEach {
                            val next = (it + 1) % positions.size
                            val pos0 = positions[it]
                            val pos1 = positions[next]
                            add(vec3Of(pos0.x, pos0.y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
                            add(vec3Of(pos1.x, pos1.y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
                        }
                    }
                }
            }
        }
        outlineVao?.let {
            GL11.glLineWidth(2f)
            ctx.shader.apply {
                useColor.setInt(1)
                useLight.setInt(0)
                useTexture.setInt(0)
                matrixM.setMatrix4(Matrix4.IDENTITY)
                accept(it)
            }
            GL11.glLineWidth(1f)
        }
    }

    fun resetIfNeeded(ctx: RenderContext, material: IMaterial) {
        if (materialHash != material.hashCode()) {
            materialHash = material.hashCode()
            materialVao?.close()
            materialVao = null
            gridLines?.close()
            gridLines = null
        }

        val model = ctx.gui.projectManager.model
        val objs = model.objectRefs
                .filter { model.isVisible(it) }
                .map { model.getObject(it) }

        if (modelHash != model.hashCode() || visibleHash != objs.hashCode() || ctx.gui.input.mouse.isButtonPressed(0)) {
            modelHash = model.hashCode()
            visibleHash = objs.hashCode()
            outlineVao?.close()
            outlineVao = null
        }
    }

    fun renderMaterial(ctx: RenderContext, material: IMaterial) {
        if (materialVao == null) {
            materialVao = ctx.buffer.build(GL11.GL_QUADS) {
                val maxX = material.size.xi
                val maxY = material.size.yi
                add(vec3Of(0, 0, 0), vec2Of(0, 1), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(maxX, 0, 0), vec2Of(1, 1), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(maxX, maxY, 0), vec2Of(1, 0), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(0, maxY, 0), vec2Of(0, 0), Vector3.ORIGIN, Vector3.ORIGIN)
            }
        }
        materialVao?.let {
            material.bind()
            ctx.shader.apply {
                useColor.setInt(0)
                useLight.setInt(0)
                useTexture.setInt(1)
                matrixM.setMatrix4(Matrix4.IDENTITY)
                accept(it)
            }
        }
    }

    fun renderGridLines(ctx: RenderContext, material: IMaterial) {
        if (gridLines == null) {
            gridLines = ctx.buffer.build(GL11.GL_LINES) {
                val min = 0
                val maxX = material.size.xi
                val maxY = material.size.yi

                for (x in min..maxX) {
                    val color = if (x % 16 == 0) Config.colorPalette.grid2Color else Config.colorPalette.grid1Color
                    add(vec3Of(x, min, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
                    add(vec3Of(x, maxY, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
                }
                for (y in min..maxY) {
                    val color = if (y % 16 == 0) Config.colorPalette.grid2Color else Config.colorPalette.grid1Color
                    add(vec3Of(min, y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
                    add(vec3Of(maxX, y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
                }
            }
        }
        gridLines?.let {
            ctx.shader.apply {
                useColor.setInt(1)
                useLight.setInt(0)
                useTexture.setInt(0)
                matrixM.setMatrix4(Matrix4.IDENTITY)
                accept(it)
            }
        }
    }

    fun setCamera(ctx: RenderContext) {
        val projection = MatrixUtils.createOrthoMatrix(ctx.viewport)
        val view = ctx.camera.matrixForUV
        ctx.shader.matrixVP.setMatrix4(projection * view)
    }
}