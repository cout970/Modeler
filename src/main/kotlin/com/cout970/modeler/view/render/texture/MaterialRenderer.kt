package com.cout970.modeler.view.render.texture

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.matrix.extensions.Matrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.MatrixUtils
import com.cout970.modeler.view.render.tool.AutoCache
import com.cout970.modeler.view.render.tool.CacheFrags
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.modeler.view.render.tool.useBlend
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by cout970 on 2017/07/11.
 */
class MaterialRenderer {

    var areasCache = AutoCache(CacheFrags.MODEL, CacheFrags.MATERIAL, CacheFrags.VISIBILITY)
    val gridLines = AutoCache()
    val materialCache = AutoCache(CacheFrags.MATERIAL)
    val selectionCache = AutoCache(CacheFrags.MODEL, CacheFrags.SELECTION_TEXTURE, CacheFrags.MATERIAL)

    fun renderWorld(ctx: RenderContext, ref: IMaterialRef, material: IMaterial) {
        setCamera(ctx)
        GLStateMachine.depthTest.disable()

        if (ctx.gui.state.drawTextureGridLines) {
            renderGridLines(ctx, material)
        }
        renderMaterial(ctx, material)
        GLStateMachine.useBlend(0.5f) {
            renderMappedAreas(ctx, ref, material)
        }

        val selection = ctx.gui.selectionHandler.getModelSelection()
        if (selection.isDefined()) {
            renderSelection(ctx, selection.get(), material)
        }
        GLStateMachine.depthTest.enable()
    }

    fun renderMappedAreas(ctx: RenderContext, ref: IMaterialRef, material: IMaterial) {
        val vao = areasCache.getOrCreate(ctx) {
            val model = ctx.gui.projectManager.model
            val objs = model.objectRefs
                    .filter { model.isVisible(it) }
                    .map { model.getObject(it) }
                    .filter { it.material == ref }

            ctx.buffer.build(GL11.GL_QUADS) {
                objs.forEach { obj ->
                    val mesh = obj.transformedMesh
                    mesh.faces.forEachIndexed { index, face ->
                        val vec = Color.getHSBColor((index * 59 % 360) / 360f, 0.5f, 1.0f)
                        val color = vec3Of(vec.red, vec.green, vec.blue) / 255

                        val positions = face.tex
                                .map { mesh.tex[it] }
                                .map { vec2Of(it.xd, 1 - it.yd) }
                                .map { it * material.size }
                        positions.indices.forEach {
                            val pos0 = positions[it]
                            add(vec3Of(pos0.x, pos0.y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
                        }
                    }
                }
            }
        }
        GL11.glLineWidth(2f)
        ctx.shader.apply {
            useColor.setInt(1)
            useLight.setInt(0)
            useTexture.setInt(0)
            matrixM.setMatrix4(Matrix4.IDENTITY)
            accept(vao)
        }
        GL11.glLineWidth(1f)
    }

    fun renderSelection(ctx: RenderContext, selection: ISelection, material: IMaterial) {
        val vao = selectionCache.getOrCreate(ctx) {
            val model = ctx.gui.state.tmpModel ?: ctx.gui.projectManager.model
            val objs = model.objectRefs
                    .filter { selection.isSelected(it) }
                    .map { model.getObject(it) }

            val color = Config.colorPalette.textureSelectionColor

            ctx.buffer.build(GL11.GL_LINES) {
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

        GL11.glLineWidth(2f)
        ctx.shader.apply {
            useColor.setInt(1)
            useLight.setInt(0)
            useTexture.setInt(0)
            matrixM.setMatrix4(Matrix4.IDENTITY)
            accept(vao)
        }
        GL11.glLineWidth(1f)
    }

    fun renderMaterial(ctx: RenderContext, material: IMaterial) {
        val vao = materialCache.getOrCreate(ctx) {
            ctx.buffer.build(GL11.GL_QUADS) {
                val maxX = material.size.xi
                val maxY = material.size.yi
                add(vec3Of(0, 0, 0), vec2Of(0, 1), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(maxX, 0, 0), vec2Of(1, 1), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(maxX, maxY, 0), vec2Of(1, 0), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(0, maxY, 0), vec2Of(0, 0), Vector3.ORIGIN, Vector3.ORIGIN)
            }
        }

        material.bind()
        ctx.shader.apply {
            useColor.setInt(0)
            useLight.setInt(0)
            useTexture.setInt(1)
            matrixM.setMatrix4(Matrix4.IDENTITY)
            accept(vao)
        }
    }

    fun renderGridLines(ctx: RenderContext, material: IMaterial) {
        val vao = gridLines.getOrCreate(ctx) {
            ctx.buffer.build(GL11.GL_LINES) {
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
        ctx.shader.apply {
            useColor.setInt(1)
            useLight.setInt(0)
            useTexture.setInt(0)
            matrixM.setMatrix4(Matrix4.IDENTITY)
            accept(vao)
        }
    }

    fun setCamera(ctx: RenderContext) {
        val projection = MatrixUtils.createOrthoMatrix(ctx.viewport)
        val view = ctx.camera.matrixForUV
        ctx.shader.matrixVP.setMatrix4(projection * view)
    }
}