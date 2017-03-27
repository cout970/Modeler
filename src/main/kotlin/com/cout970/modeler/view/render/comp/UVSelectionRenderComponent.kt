package com.cout970.modeler.view.render.comp

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.modeler.config.Config
import com.cout970.modeler.model.Edge
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.Vertex
import com.cout970.modeler.model.material.MaterialNone
import com.cout970.modeler.model.util.getElement
import com.cout970.modeler.selection.VertexPosSelection
import com.cout970.modeler.selection.VertexTexSelection
import com.cout970.modeler.selection.subselection.SubSelectionEdge
import com.cout970.modeler.selection.subselection.SubSelectionFace
import com.cout970.modeler.selection.subselection.SubSelectionVertex
import com.cout970.modeler.selection.vertexPosSelection
import com.cout970.modeler.selection.vertexTexSelection
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.view.render.RenderContext
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/21.
 */
class UVSelectionRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {
        ctx.apply {
            GLStateMachine.depthTest.disable()

            val modelSelection = scene.modelProvider.selectionManager.vertexPosSelection
            val textureSelection = scene.modelProvider.selectionManager.vertexTexSelection
            val texture = model.resources.materials.firstOrNull() ?: MaterialNone

            val size = texture.size
            val offset = MaterialNone.size / 2
            val color = Config.colorPalette.textureSelectionColor

            val renderer = Renderer(size, offset, color, ctx)

            MaterialNone.bind()
            drawModelSelection(this, renderer, modelSelection, scene.sceneController.showAllMeshUVs.get())
            drawTextureSelection(this, renderer, textureSelection)
            GLStateMachine.depthTest.enable()
        }
    }

    private fun drawTextureSelection(ctx: RenderContext, renderer: Renderer, textureSelection: VertexTexSelection) {
        ctx.apply {
            GLStateMachine.useBlend(0.25f) {
                draw(GL11.GL_QUADS, shaderHandler.formatPCT) {
                    val handler = textureSelection.subPathHandler
                    when (handler) {
                        is SubSelectionFace -> handler.paths.forEach { renderer.renderQuad(it.toQuad(model)) }
                        is SubSelectionEdge -> handler.paths.forEach { renderer.renderEdge(it.toEdge(model)) }
                        is SubSelectionVertex -> handler.paths.forEach { renderer.renderVertex(it.toVertex(model)) }
                    }
                }
            }
        }
    }

    private fun drawModelSelection(ctx: RenderContext, renderer: Renderer, modelSelection: VertexPosSelection,
                                   showAllMeshUVs: Boolean) {
        ctx.apply {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
            GL11.glLineWidth(2f)

            if (showAllMeshUVs) {
                draw(GL11.GL_QUADS, shaderHandler.formatPCT) {
                    modelSelection.pathList
                            .map { it.elementPath }
                            .distinct()
                            .map { model.getElement(it) }
                            .flatMap { it.getQuads() }
                            .forEach { renderer.renderQuad(it) }
                }
            } else {
                draw(GL11.GL_QUADS, shaderHandler.formatPCT) {
                    val handler = modelSelection.subPathHandler
                    when (handler) {
                        is SubSelectionFace -> handler.paths.forEach { renderer.renderQuad(it.toQuad(model)) }
                        is SubSelectionEdge -> handler.paths.forEach { renderer.renderEdge(it.toEdge(model)) }
                        is SubSelectionVertex -> handler.paths.forEach { renderer.renderVertex(it.toVertex(model)) }
                    }
                }
            }

            GL11.glLineWidth(1f)
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        }
    }

    private class Renderer(
            val size: IVector2,
            val offset: IVector2,
            val color: IVector3,
            val ctx: RenderContext
    ) {

        fun renderQuad(quad: Quad) {
            ctx.apply {
                quad.vertex
                        .map { it.copy(tex = vec2Of(it.tex.x, 1 - it.tex.yd)) }
                        .map { (it.tex * size) - offset }
                        .forEach { tessellator.set(0, it.x, it.yd, 0).setVec(1, color).set(2, 0.0, 0.0).endVertex() }
            }
        }

        fun renderEdge(edge: Edge) {
            ctx.apply {
                edge.vertex
                        .map { it.copy(tex = vec2Of(it.tex.x, 1 - it.tex.yd)) }
                        .map { (it.tex * size) - offset }
                        .also {
                            RenderUtil.renderBar(it[0].toVector3(0), it[1].toVector3(0), 0.5) {
                                tessellator.set(0, it.x, it.yd, it.zd).setVec(1, color).set(2, 0.0, 0.0).endVertex()
                            }
                        }
            }
        }

        fun renderVertex(vertex: Vertex) {
            ctx.apply {
                listOf(vertex)
                        .map { it.copy(tex = vec2Of(it.tex.x, 1 - it.tex.yd)) }
                        .map { (it.tex * size) - offset }
                        .forEach {
                            val pos = vec3Of(it.x, it.yd, 0)
                            RenderUtil.renderBar(pos, pos, 0.5) {
                                tessellator.set(0, it.x, it.yd, it.zd).setVec(1, color).set(2, 0.0, 0.0).endVertex()
                            }
                        }
            }
        }
    }


}