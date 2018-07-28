package com.cout970.modeler.render.texture

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.BufferPTNC
import com.cout970.glutilities.tessellator.DrawMode
import com.cout970.matrix.extensions.Matrix4
import com.cout970.matrix.extensions.times
import com.cout970.modeler.Debugger
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.api.model.selection.*
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.helpers.PickupHelper
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.material.ColoredMaterial
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.render.tool.AutoCache
import com.cout970.modeler.render.tool.CacheFlags.*
import com.cout970.modeler.render.tool.RenderContext
import com.cout970.modeler.render.tool.append
import com.cout970.modeler.render.tool.useBlend
import com.cout970.modeler.util.MatrixUtils
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * Created by cout970 on 2017/07/11.
 */
class MaterialRenderer {

    var areasCache = AutoCache(MODEL, MATERIAL, SELECTION_TEXTURE)
    val debugCache = AutoCache()

    val gridLinesPixel = AutoCache(MATERIAL)
    val gridLinesBlock = AutoCache(MATERIAL)
    val materialCache = AutoCache(MATERIAL)
    val textureSelectionCache = AutoCache(MODEL, SELECTION_TEXTURE, MATERIAL)
    val modelSelectionCache = AutoCache(MODEL, SELECTION_MODEL, MATERIAL)

    val cursorRenderer = TextureCursorRenderer()

    fun renderWorld(ctx: RenderContext, ref: IMaterialRef, material: IMaterial) {
        setCamera(ctx)
        GLStateMachine.depthTest.disable()

        renderMaterial(ctx, material)

        if (ctx.gui.state.drawTextureGridLines) {
            renderGridLines(ctx, material)
        }

        if (ctx.gui.state.drawTextureProjection) {
            GLStateMachine.useBlend(0.5f) {
                renderMappedAreas(ctx, ref, material)
            }
        }

        ctx.gui.programState.modelSelection.ifNotNull {
            renderModelSelection(ctx, it, material)
        }

        ctx.gui.programState.textureSelection.ifNotNull {
            renderTextureSelection(ctx, it, material)
        }

        if (Debugger.DYNAMIC_DEBUG) {
            renderDebugCursor(ctx, material)
        }

        GLStateMachine.depthTest.enable()

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        cursorRenderer.renderCursor(ctx)
    }

    fun renderMappedAreas(ctx: RenderContext, ref: IMaterialRef, material: IMaterial) {
        val vao = areasCache.getOrCreate(ctx) {
            val model = ctx.gui.state.tmpModel ?: ctx.gui.programState.model
            val objs = model.objectRefs
                    .map { model.getObject(it) }
                    .filter { it.visible && it.material == ref }

            ctx.buffer.build(DrawMode.TRIANGLES) {
                objs.forEach { obj ->
                    val mesh = obj.mesh

                    mesh.faces.forEachIndexed { index, face ->
                        val vec = Color.getHSBColor((index * 59 % 360) / 360f, 0.5f, 1.0f)
                        val color = vec3Of(vec.red, vec.green, vec.blue) / 255

                        val positions = face.tex
                                .map { mesh.tex[it] }
                                .map { vec2Of(it.xd, 1 - it.yd) }
                                .map { it * material.size }

                        assert(positions.size == 4)


                        positions[0].let { add(vec3Of(it.x, it.y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color) }
                        positions[1].let { add(vec3Of(it.x, it.y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color) }
                        positions[2].let { add(vec3Of(it.x, it.y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color) }

                        positions[0].let { add(vec3Of(it.x, it.y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color) }
                        positions[2].let { add(vec3Of(it.x, it.y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color) }
                        positions[3].let { add(vec3Of(it.x, it.y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color) }
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

    fun renderTextureSelection(ctx: RenderContext, selection: ISelection, material: IMaterial) {
        val vao = textureSelectionCache.getOrCreate(ctx) {
            val model = ctx.gui.state.tmpModel ?: ctx.gui.programState.model
            val color = Config.colorPalette.textureSelectionColor

            ctx.buffer.build(DrawMode.LINES) {

                when (selection.selectionType) {
                    SelectionType.OBJECT -> this.renderObjectSelection(model, selection, material, color)
                    SelectionType.FACE -> this.renderFaceSelection(model, selection, material, color)
                    SelectionType.EDGE -> this.renderEdgeSelection(model, selection, material, color)
                    SelectionType.VERTEX -> this.renderVertexSelection(model, selection, material, color)
                }
            }
        }

        GL11.glLineWidth(3f)
        ctx.shader.apply {
            useColor.setInt(0)
            useLight.setInt(0)
            useTexture.setInt(0)
            matrixM.setMatrix4(Matrix4.IDENTITY)
            accept(vao)
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

    fun renderModelSelection(ctx: RenderContext, selection: ISelection, material: IMaterial) {

        if (selection.selectionType !in setOf(SelectionType.OBJECT, SelectionType.FACE)) return

        val vao = modelSelectionCache.getOrCreate(ctx) {
            val model = ctx.gui.state.tmpModel ?: ctx.gui.programState.model
            val color = Config.colorPalette.modelSelectionColor

            ctx.buffer.build(DrawMode.LINES) {

                when (selection.selectionType) {
                    SelectionType.OBJECT -> this.renderObjectSelection(model, selection, material, color)
                    SelectionType.FACE -> this.renderFaceSelection(model, selection, material, color)
                    else -> {
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

    private fun BufferPTNC.renderVertexSelection(model: IModel, selection: ISelection, material: IMaterial,
                                                 color: IVector3) {

        val refs = selection.refs.filterIsInstance<IPosRef>()

        refs.forEach { ref ->
            val obj = model.getObject(ref.toObjectRef())

            val pos = ref.posIndex
                    .let { obj.mesh.tex[it] }
                    .let { vec2Of(it.xd, 1 - it.yd) }
                    .let { it * material.size }

            val scale = vec2Of(1 / 4.0)

            val pos0 = pos + scale * vec2Of(-1, -1)
            val pos1 = pos + scale * vec2Of(1, -1)
            val pos2 = pos + scale * vec2Of(1, 1)
            val pos3 = pos + scale * vec2Of(-1, 1)

            val positions = listOf(pos0, pos1, pos2, pos3)

            addPositions(positions, color)
        }
    }

    private fun BufferPTNC.renderEdgeSelection(model: IModel, selection: ISelection, material: IMaterial,
                                               color: IVector3) {
        val refs = selection.refs.filterIsInstance<IEdgeRef>()
        refs.forEach { ref ->
            val obj = model.getObject(ref.toObjectRef())

            val positions = listOf(ref.firstIndex, ref.secondIndex)
                    .map { obj.mesh.tex[it] }
                    .map { vec2Of(it.xd, 1 - it.yd) }
                    .map { it * material.size }

            addPositions(positions, color)
        }
    }

    private fun BufferPTNC.renderFaceSelection(model: IModel, selection: ISelection, material: IMaterial,
                                               color: IVector3) {
        val refs = selection.refs.filterIsInstance<IFaceRef>()
        refs.forEach { ref ->
            val obj = model.getObject(ref.toObjectRef())
            val face = obj.mesh.faces[ref.faceIndex]

            val positions: List<IVector2> = face.tex
                    .map { obj.mesh.tex[it] }
                    .map { vec2Of(it.xd, 1 - it.yd) }
                    .map { it * material.size }

            addPositions(positions, color)
        }
    }

    private fun BufferPTNC.renderObjectSelection(model: IModel, selection: ISelection, material: IMaterial,
                                                 color: IVector3) {
        val objs = model.objectRefs
                .filter { selection.isSelected(it) }
                .map { model.getObject(it) }

        objs.forEach { obj ->
            val mesh = obj.mesh

            mesh.faces.forEach { face ->

                val positions = face.tex
                        .map { mesh.tex[it] }
                        .map { vec2Of(it.xd, 1 - it.yd) }
                        .map { it * material.size }

                addPositions(positions, color)
            }
        }
    }

    private fun BufferPTNC.addPositions(positions: List<IVector2>, color: IVector3) {
        positions.indices.forEach {
            val next = (it + 1) % positions.size
            val pos0 = positions[it]
            val pos1 = positions[next]
            add(vec3Of(pos0.x, pos0.y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(pos1.x, pos1.y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
        }
    }

    fun renderMaterial(ctx: RenderContext, material: IMaterial) {
        val vao = materialCache.getOrCreate(ctx) {
            ctx.buffer.build(DrawMode.TRIANGLES) {
                val maxX = material.size.xi
                val maxY = material.size.yi
                add(vec3Of(0, 0, 0), vec2Of(0, 1), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(maxX, 0, 0), vec2Of(1, 1), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(maxX, maxY, 0), vec2Of(1, 0), Vector3.ORIGIN, Vector3.ORIGIN)

                add(vec3Of(0, 0, 0), vec2Of(0, 1), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(maxX, maxY, 0), vec2Of(1, 0), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(0, maxY, 0), vec2Of(0, 0), Vector3.ORIGIN, Vector3.ORIGIN)
            }
        }

        material.bind()
        ctx.shader.apply {
            useColor.setBoolean(false)
            useLight.setBoolean(false)
            useTexture.setBoolean(true)
            matrixM.setMatrix4(Matrix4.IDENTITY)

            if (material is ColoredMaterial) {
                useGlobalColor.setBoolean(true)
                globalColor.setVector3(material.color)
            }

            accept(vao)

            if (material is ColoredMaterial) {
                globalColor.setVector3(Vector3.ONE)
            }
        }
    }

    fun renderGridLines(ctx: RenderContext, material: IMaterial) {
        val pixelVao = gridLinesPixel.getOrCreate(ctx) {
            ctx.buffer.build(DrawMode.LINES) { renderGridLines(ctx, material, true) }
        }
        val blockVao = gridLinesBlock.getOrCreate(ctx) {
            ctx.buffer.build(DrawMode.LINES) { renderGridLines(ctx, material, false) }
        }
        val vao = if (ctx.camera.zoom < Config.zoomLevelToChangeGridDetail) pixelVao else blockVao

        ctx.shader.apply {
            useColor.setInt(1)
            useLight.setInt(0)
            useTexture.setInt(0)
            matrixM.setMatrix4(Matrix4.IDENTITY)
            accept(vao)
        }
    }

    private fun BufferPTNC.renderGridLines(ctx: RenderContext, material: IMaterial, pixel: Boolean) {
        val min = 0
        val maxX = material.size.xi
        val maxY = material.size.yi

        for (x in min..maxX) {
            val color = if (x % 16 == 0) Config.colorPalette.grid2Color else Config.colorPalette.grid1Color
            if (!pixel && x % 16 != 0) continue
            add(vec3Of(x, min, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(x, maxY, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
        }
        for (y in min..maxY) {
            val color = if (y % 16 == 0) Config.colorPalette.grid2Color else Config.colorPalette.grid1Color
            if (!pixel && y % 16 != 0) continue
            add(vec3Of(min, y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
            add(vec3Of(maxX, y, 0), Vector2.ORIGIN, Vector3.ORIGIN, color)
        }
    }

    fun setCamera(ctx: RenderContext) {
        val projection = MatrixUtils.createOrthoMatrix(ctx.viewport)
        val view = ctx.camera.matrixForUV
        ctx.shader.matrixVP.setMatrix4(projection * view)
    }

    fun renderDebugCursor(ctx: RenderContext, material: IMaterial) {
        val vao = debugCache.getOrCreate(ctx) {
            ctx.buffer.build(DrawMode.TRIANGLES) {
                MeshFactory.createCube(vec3Of(0.5), Vector3.ORIGIN).append(this, vec3Of(1, 0, 1))
            }
        }

        val position = PickupHelper.getMousePosAbsolute(ctx.canvas, ctx.gui.input.mouse.getMousePos())

        ctx.shader.apply {
            useColor.setInt(1)
            useLight.setInt(0)
            useTexture.setInt(0)
            matrixM.setMatrix4(TRSTransformation(translation = position.toVector3(0.0)).matrix)
            accept(vao)
        }
    }
}