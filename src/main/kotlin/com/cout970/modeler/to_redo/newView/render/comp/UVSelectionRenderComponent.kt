package com.cout970.modeler.to_redo.newView.render.comp

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.to_redo.model.Model
import com.cout970.modeler.to_redo.model.Quad
import com.cout970.modeler.to_redo.model.Vertex
import com.cout970.modeler.to_redo.model.api.IElementLeaf
import com.cout970.modeler.to_redo.model.material.MaterialNone
import com.cout970.modeler.to_redo.model.util.getElement
import com.cout970.modeler.to_redo.selection.VertexPosSelection
import com.cout970.modeler.to_redo.selection.VertexTexSelection
import com.cout970.modeler.to_redo.selection.subselection.SubSelectionEdge
import com.cout970.modeler.to_redo.selection.subselection.SubSelectionFace
import com.cout970.modeler.to_redo.selection.subselection.SubSelectionVertex
import com.cout970.modeler.to_redo.selection.vertexTexSelection
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

            val modelSelection = selectionManager.getSelectedVertexPos(model)
            val textureSelection = selectionManager.vertexTexSelection
            val texture = model.resources.materials.firstOrNull() ?: MaterialNone

            val size = texture.size
            val offset = MaterialNone.size / 2
            val color = Config.colorPalette.textureSelectionColor

            val renderer = Renderer(size, offset, color, ctx)

            MaterialNone.bind()
            drawModelSelection(this, renderer, modelSelection, controllerState.showAllMeshUVs.get())
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
                        is SubSelectionEdge -> renderer.renderSelectedEdges(model, handler)
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
                        is SubSelectionEdge -> renderer.renderSelectedEdges(model, handler)
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

        fun renderSelectedEdges(model: Model, sel: SubSelectionEdge) {
            val aux = sel.paths
                    .groupBy { it.elementPath }
                    .map { model.getElement(it.key) as IElementLeaf to it.value }

            aux.forEach { (elem, list) ->
                val pos = list.flatMap {
                    listOf(elem.positions[it.firstIndex], elem.positions[it.secondIndex])
                }

                elem.getQuads().forEach {
                    it.toEdges().forEach {
                        if (it.a.pos in pos && it.b.pos in pos) {
                            renderEdge(it.a.tex to it.b.tex)
                        }
                    }
                }
            }
        }


        fun renderQuad(quad: Quad) {
            ctx.apply {
                quad.vertex
                        .map { it.copy(tex = vec2Of(it.tex.x, 1 - it.tex.yd)) }
                        .map { (it.tex * size) - offset }
                        .forEach { tessellator.set(0, it.x, it.yd, 0).setVec(1, color).set(2, 0.0, 0.0).endVertex() }
            }
        }

        fun renderEdge(edge: Pair<IVector2, IVector2>) {
            ctx.apply {
                edge.toList()
                        .map { vec2Of(it.x, 1 - it.yd) }
                        .map { (it * size) - offset }
                        .also {
                            RenderUtil.renderBar(it[0].toVector3(0), it[1].toVector3(0), 0.5) {
                                tessellator.set(0, it.xd, it.yd, it.zd).setVec(1, color).set(2, 0.0, 0.0).endVertex()
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
                            val pos = vec3Of(it.xd, it.yd, 0)
                            RenderUtil.renderBar(pos, pos, 0.5) {
                                tessellator.set(0, it.x, it.yd, it.zd).setVec(1, color).set(2, 0.0, 0.0).endVertex()
                            }
                        }
            }
        }
    }


}