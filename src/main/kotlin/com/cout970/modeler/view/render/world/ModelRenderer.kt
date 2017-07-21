package com.cout970.modeler.view.render.world

import com.cout970.glutilities.tessellator.VAO
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.modeler.view.render.tool.createVao
import com.cout970.modeler.view.render.tool.forEachEdge
import com.cout970.modeler.view.render.tool.shader.UniversalShader
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/06/09.
 */

class ModelRenderer {

    var modelCache: List<VAO> = mutableListOf()
    var selectionVao: VAO? = null
    var lastModifiedSelection = -1L
    var modelHash = -1

    fun renderModels(ctx: RenderContext, model: IModel) {

        val modelToRender = ctx.gui.state.tmpModel ?: model

        if (modelCache.isEmpty() || modelToRender.hashCode() != modelHash) {
            modelHash = modelToRender.hashCode()
            modelCache.forEach { it.close() }
            modelCache = mutableListOf()
        }

        renderModel(ctx, modelToRender)

        if (ctx.gui.selectionHandler.lastModified != lastModifiedSelection || ctx.gui.state.tmpModel != null) {
            lastModifiedSelection = ctx.gui.selectionHandler.lastModified
            selectionVao?.close()
            selectionVao = null
        }
        if (selectionVao == null) {
            selectionVao = ctx.buffer.build(GL11.GL_QUADS) {
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

        selectionVao?.let {
            ctx.shader.apply {
                useTexture.setInt(0)
                useColor.setInt(1)
                useLight.setInt(0)
                matrixM.setMatrix4(Matrix4.IDENTITY)
                accept(it)
            }
        }
    }


    fun renderModel(ctx: RenderContext, model: IModel) {

        if (modelCache.size != model.objects.size) {
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
        model.materials.forEach { it.loadTexture(ResourceLoader()) }
        return updateCache(buffer, model)
    }

    private fun updateCache(buffer: UniversalShader.Buffer, model: IModel): List<VAO> {
        return model.objects
                .map { it.mesh }
                .map { it.createVao(buffer) }
    }
}