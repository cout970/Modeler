package com.cout970.modeler.newView.render.comp

import com.cout970.modeler.config.Config
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.api.IElement
import com.cout970.modeler.model.api.IElementLeaf
import com.cout970.modeler.model.util.getElement
import com.cout970.modeler.model.util.getVertexPos
import com.cout970.modeler.newView.render.RenderContext
import com.cout970.modeler.selection.*
import com.cout970.modeler.selection.subselection.SubSelectionEdge
import com.cout970.modeler.selection.subselection.SubSelectionFace
import com.cout970.modeler.selection.subselection.SubSelectionVertex
import com.cout970.modeler.util.RenderUtil
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/20.
 */
class SelectionRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {
        ctx.apply {
            // selection outline
            if (selectionManager.selectionMode == SelectionMode.ELEMENT) {
                val selection = selectionManager.elementSelection

                if (selection != ElementSelection.EMPTY) {
                    renderCache(contentPanel.selectionCache, model.hashCode() xor selection.hashCode()) {
                        val size = Config.selectionThickness.toDouble()
                        val color = Config.colorPalette.modelSelectionColor

                        tessellator.compile(GL11.GL_QUADS, shaderHandler.formatPC) {
                            selection.paths
                                    .map { model.getElement(it) }
                                    .filter { it is IElementLeaf }
                                    .flatMap(IElement::getQuads)
                                    .flatMap(Quad::toEdges)
                                    .distinct()
                                    .forEach {
                                        RenderUtil.renderBar(tessellator, it.a.pos, it.b.pos, size, color)
                                    }
                        }
                    }
                }
            } else {
                val selection = selectionManager.vertexPosSelection
                if (selection != VertexPosSelection.EMPTY) {

                    // render selection
                    renderCache(contentPanel.selectionCache, model.hashCode() xor selection.hashCode()) {
                        val size = Config.selectionThickness.toDouble()
                        val color = Config.colorPalette.modelSelectionColor
                        tessellator.compile(GL11.GL_QUADS, shaderHandler.formatPC) {
                            val handler = selection.subPathHandler
                            when (handler) {
                                is SubSelectionVertex -> {
                                    handler.paths.map { model.getVertexPos(it) }
                                            .forEach { pos ->
                                                RenderUtil.renderBar(tessellator, pos, pos, size * 4, color)
                                            }
                                }
                                is SubSelectionEdge -> {
                                    handler.paths.map {
                                        Pair(model.getVertexPos(VertexPath(it.elementPath, it.firstIndex)),
                                                model.getVertexPos(VertexPath(it.elementPath, it.secondIndex)))
                                    }.forEach { (a, b) ->
                                        RenderUtil.renderBar(tessellator, a, b, size, color)
                                    }
                                }
                                is SubSelectionFace -> {
                                    handler.paths.map { (elementPath, _, vertex) ->
                                        val (a, b, c, d) = vertex.map {
                                            model.getVertexPos(VertexPath(elementPath, it))
                                        }
                                        RenderUtil.renderBar(tessellator, a, b, size, color)
                                        RenderUtil.renderBar(tessellator, b, c, size, color)
                                        RenderUtil.renderBar(tessellator, c, d, size, color)
                                        RenderUtil.renderBar(tessellator, d, a, size, color)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}