package com.cout970.modeler.render.world

import com.cout970.glutilities.tessellator.VAO
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.render.tool.*
import com.cout970.modeler.render.tool.shader.UniversalShader
import com.cout970.modeler.util.getColor
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.Vector3
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/06/09.
 */

class ModelRenderer {

    var modelCache: List<VAO> = mutableListOf()
    var selectionCache = AutoCache(CacheFrags.MODEL, CacheFrags.SELECTION_MODEL)
    var lastModifiedSelection = -1L
    var modelHash = -1

    fun renderModels(ctx: RenderContext, model: IModel) {

        val modelToRender = ctx.gui.state.tmpModel ?: model

        renderModel(ctx, modelToRender)
        renderSelection(ctx, modelToRender)
    }

    fun renderSelection(ctx: RenderContext, modelToRender: IModel) {
        val vao = selectionCache.getOrCreate(ctx) {

            ctx.buffer.build(GL11.GL_LINES) {
                val selection = ctx.gui.selectionHandler.ref

                val objSel = modelToRender.objects.filterIndexed { index, _ -> selection.any { it.objectIndex == index } }
                objSel.forEach {
                    it.mesh.forEachEdge { (a, b) ->
                        add(a.pos, Vector2.ORIGIN, Vector3.ZERO, Config.colorPalette.modelSelectionColor)
                        add(b.pos, Vector2.ORIGIN, Vector3.ZERO, Config.colorPalette.modelSelectionColor)

//                        Old selection rendering (really inefficient)
//                        RenderUtil.appendBar(this, a.pos, b.pos,
//                                size = Config.selectionThickness.toDouble(),
//                                color = Config.colorPalette.modelSelectionColor)
                    }
                }
            }
        }
        ctx.shader.apply {
            useTexture.setInt(0)
            useColor.setInt(1)
            useLight.setInt(0)
            matrixM.setMatrix4(Matrix4.IDENTITY)
            GL11.glLineWidth(Config.selectionThickness * 20f)
            accept(vao)
            GL11.glLineWidth(1f)
        }
    }

    fun renderModel(ctx: RenderContext, model: IModel) {

        if (modelCache.isEmpty() || modelCache.size != model.objects.size || model.hashCode() != modelHash) {
            modelHash = model.hashCode()
            modelCache.forEach { it.close() }
            modelCache = buildCache(modelCache, ctx.buffer, model)
        }

        val map = model.objects
                .mapIndexed { ind, iObject -> ind to iObject }
                .filter { (first) -> model.visibilities[first] }
                .groupBy { it.second.material }

        map.forEach { material, list ->
            model.getMaterial(material).bind()
            list.forEach { (objIndex, _) ->
                ctx.shader.apply {
                    useTexture.setBoolean(ctx.gui.state.useTexture)
                    useColor.setBoolean(ctx.gui.state.useColor)
                    useLight.setBoolean(ctx.gui.state.useLight)
                    showHiddenFaces.setBoolean(ctx.gui.state.showHiddenFaces)
                    matrixM.setMatrix4(Matrix4.IDENTITY)
                    accept(modelCache[objIndex])
                    showHiddenFaces.setBoolean(false)
                }
            }
        }
    }

    private fun buildCache(list: List<VAO>, buffer: UniversalShader.Buffer, model: IModel): List<VAO> {
        list.forEach { it.close() }
        return model.objects
                .map { it.mesh }
                .map { it.createVao(buffer, getColor(it.hashCode())) }
    }
}