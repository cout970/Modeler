package com.cout970.modeler.view.render.world

import com.cout970.glutilities.tessellator.VAO
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.view.render.tool.*
import com.cout970.modeler.view.render.tool.shader.UniversalShader
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
            ctx.buffer.build(GL11.GL_QUADS) {
                val selection = ctx.gui.selectionHandler.ref

                val objSel = modelToRender.objects.filterIndexed { index, _ -> selection.any { it.objectIndex == index } }
                objSel.forEach {
                    it.mesh.forEachEdge { (a, b) ->
                        RenderUtil.appendBar(this, a.pos, b.pos,
                                size = Config.selectionThickness.toDouble(),
                                color = Config.colorPalette.modelSelectionColor)
                    }
                }
            }
        }
        ctx.shader.apply {
            useTexture.setInt(0)
            useColor.setInt(1)
            useLight.setInt(0)
            matrixM.setMatrix4(Matrix4.IDENTITY)
            accept(vao)
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
            list.forEach { (objIndex, obj) ->
                ctx.shader.apply {
                    useTexture.setInt(1)
                    useColor.setInt(0)
                    useLight.setInt(1)
                    matrixM.setMatrix4(obj.transformation.matrix)
                    accept(modelCache[objIndex])
                }
            }
        }
    }

    private fun buildCache(list: List<VAO>, buffer: UniversalShader.Buffer, model: IModel): List<VAO> {
        list.forEach { it.close() }
        return model.objects
                .map { it.mesh }
                .map { it.createVao(buffer) }
    }
}