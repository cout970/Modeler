package com.cout970.modeler.view.render.world

import com.cout970.glutilities.tessellator.VAO
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.controller.World
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.modeler.view.render.tool.createVao
import com.cout970.modeler.view.render.tool.forEachEdge
import com.cout970.modeler.view.render.tool.shader.UniversalShader
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/06/09.
 */

class ModelRenderer {

    val modelCache: MutableList<List<VAO>> = mutableListOf()
    var selectionVao: VAO? = null
    var axis: VAO? = null
    var lastModified = -1L

    fun renderModels(ctx: RenderContext, world: World) {

        if (world.models.isEmpty()) return

        if (modelCache.size != world.models.size) {
            modelCache.clear()
            world.models.forEach { modelCache.add(listOf()) }
        }

        world.models.forEachIndexed { modelIndex, model ->
            if (modelIndex == 0 && ctx.guiState.tmpModel != null) {
                if (modelIndex in modelCache.indices) {
                    modelCache[modelIndex] = emptyList()
                }
                renderModel(ctx, ctx.guiState.tmpModel!!, modelIndex)
            } else {
                renderModel(ctx, model, modelIndex)
            }
        }

        if (ctx.guiState.selectionHandler.lastModified != lastModified) {
            selectionVao?.close()
            selectionVao = null
        }
        if (selectionVao == null) {
            selectionVao = ctx.buffer.build(GL11.GL_QUADS) {
                val model = world.models.first()
                val selection = ctx.guiState.selectionHandler.selection

                val objSel = model.objects.filterIndexed { index, _ -> selection.any { it.objectIndex == index } }
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

        if (axis == null) {
            axis = ctx.buffer.build(GL11.GL_LINES) {
                add(Vector3.ORIGIN, Vector2.ORIGIN, Vector3.ORIGIN, vec3Of(1, 0, 0))
                add(vec3Of(100, 0, 0), Vector2.ORIGIN, Vector3.ORIGIN, vec3Of(1, 0, 0))

                add(Vector3.ORIGIN, Vector2.ORIGIN, Vector3.ORIGIN, vec3Of(0, 1, 0))
                add(vec3Of(0, 100, 0), Vector2.ORIGIN, Vector3.ORIGIN, vec3Of(0, 1, 0))

                add(Vector3.ORIGIN, Vector2.ORIGIN, Vector3.ORIGIN, vec3Of(0, 0, 1))
                add(vec3Of(0, 0, 100), Vector2.ORIGIN, Vector3.ORIGIN, vec3Of(0, 0, 1))
            }
        }

        axis?.let {
            ctx.shader.apply {
                useTexture.setInt(0)
                useColor.setInt(1)
                useLight.setInt(0)
                matrixM.setMatrix4(Matrix4.IDENTITY)
                accept(it)
            }
        }
    }


    fun renderModel(ctx: RenderContext, model: IModel, index: Int) {

        if (modelCache[index].size != model.objects.size) {
            modelCache[index] = buildCache(modelCache[index], ctx.buffer, model)
        }
        val map = model.objects
                .mapIndexed { index, iObject -> index to iObject }
                .groupBy { it.second.material }

        map.forEach { material, list ->
            material.bind()
            list.forEach { (objIndex, obj) ->
                ctx.shader.apply {
                    useTexture.setInt(1)
                    useColor.setInt(0)
                    useLight.setInt(1)
                    matrixM.setMatrix4(obj.transformation.matrix)
                    accept(modelCache[index][objIndex])
                }
            }
        }
    }

    private fun buildCache(list: List<VAO>, buffer: UniversalShader.Buffer, model: IModel): List<VAO> {
        list.forEach { it.close() }
        model.objects.forEach { it.material.loadTexture(ResourceLoader()) }
        return updateCache(buffer, model)
    }

    private fun updateCache(buffer: UniversalShader.Buffer, model: IModel): List<VAO> {
        return model.objects
                .map { it.mesh }
                .map { it.createVao(buffer) }
    }
}