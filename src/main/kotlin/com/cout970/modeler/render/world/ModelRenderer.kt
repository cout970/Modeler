package com.cout970.modeler.render.world

import com.cout970.glutilities.tessellator.VAO
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.render.tool.*
import com.cout970.modeler.render.tool.shader.UniversalShader
import com.cout970.modeler.util.RenderUtil
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.vec3Of
import org.lwjgl.opengl.GL11
import java.awt.Color

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
            list.forEach { (objIndex, _) ->
                ctx.shader.apply {
                    useTexture.setBoolean(ctx.gui.state.useTexture)
                    useColor.setBoolean(ctx.gui.state.useColor)
                    useLight.setBoolean(ctx.gui.state.useLight)
                    matrixM.setMatrix4(Matrix4.IDENTITY)
                    accept(modelCache[objIndex])
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

    fun getColor(hash: Int): IVector3 {
        val c = Color.getHSBColor((hash.toDouble() / Int.MAX_VALUE.toDouble()).toFloat() * 360f, 0.5f, 1f)
        return vec3Of(c.blue / 255f, c.green / 255f, c.red / 255f)
    }
}