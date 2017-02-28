package com.cout970.modeler.view.scene

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.ITessellator
import com.cout970.modeler.config.Config
import com.cout970.modeler.model.Edge
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.model.material.MaterialNone
import com.cout970.modeler.model.structure.zipVertexPaths
import com.cout970.modeler.selection.VertexPosSelection
import com.cout970.modeler.selection.VertexTexSelection
import com.cout970.modeler.util.CursorParameters
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.controller.SceneController
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.modeler.view.controller.TransformationMode
import com.cout970.modeler.view.util.ShaderHandler
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/01/23.
 */
class TextureSceneRenderer(shaderHandler: ShaderHandler) : SceneRenderer(shaderHandler) {

    private var size: IVector2 = vec2Of(1)
    private var offset: IVector2 = vec2Of(0)
    private var color: IVector3 = vec3Of(1)

    fun render(scene: SceneTexture) {

        if (scene.size.x < 1 || scene.size.y < 1) return

        val model = scene.sceneController.getModel(scene.modelProvider.model)
        val modelSelection = scene.modelProvider.selectionManager.vertexPosSelection
        val textureSelection = scene.modelProvider.selectionManager.vertexTexSelection
        val texture = model.resources.materials.firstOrNull() ?: MaterialNone

        size = texture.size
        offset = size / 2
        color = Config.colorPalette.textureSelectionColor

        val y = scene.parent.size.y - (scene.position.y + scene.size.y)
        scene.windowHandler.saveViewport(vec2Of(scene.absolutePosition.x, y), scene.size.toIVector()) {

            shaderHandler.useUVShader(scene.getMatrixMVP()) {
                GLStateMachine.depthTest.disable()
                MaterialNone.bind()
                enableColor = true
                draw(GL11.GL_LINES, formatPCT) {
                    for (x in 0..size.xi) {
                        set(0, -offset.xi + x * (size.xi / size.xi), -offset.yi, 0)
                                .set(1, 0.5, 0.5, 0.5)
                                .set(2, 0.0, 0.0).endVertex()
                        set(0, -offset.xi + x * (size.xi / size.xi), size.yi - offset.yi, 0)
                                .set(1, 0.5, 0.5, 0.5)
                                .set(2, 0.0, 0.0).endVertex()
                    }

                    for (z in 0..size.yi) {
                        set(0, -offset.xi, z * (size.yi / size.yi) - offset.yi, 0)
                                .set(1, 0.5, 0.5, 0.5)
                                .set(2, 0.0, 0.0).endVertex()
                        set(0, -offset.xi + size.xi, z * (size.yi / size.yi) - offset.yi, 0)
                                .set(1, 0.5, 0.5, 0.5)
                                .set(2, 0.0, 0.0).endVertex()
                    }
                }

                enableColor = false
                texture.bind()
                draw(GL11.GL_QUADS, formatPCT) {
                    set(0, -offset.xi, -offset.yi, 0)
                            .set(1, 1, 1, 1)
                            .set(2, 0.0, 1.0).endVertex()
                    set(0, -offset.xi + size.xi, -offset.yi, 0)
                            .set(1, 1, 1, 1)
                            .set(2, 1.0, 1.0).endVertex()
                    set(0, -offset.xi + size.xi, size.yi - offset.yi, 0)
                            .set(1, 1, 1, 1)
                            .set(2, 1.0, 0.0).endVertex()
                    set(0, -offset.xi, size.yi - offset.yi, 0)
                            .set(1, 1, 1, 1)
                            .set(2, 0.0, 0.0).endVertex()
                }

                enableColor = true
                MaterialNone.bind()
                drawModelSelection(this, model, modelSelection, scene.sceneController.showAllMeshUVs.get())
                drawTextureSelection(this, model, textureSelection)

                if (textureSelection != VertexTexSelection.EMPTY) {
                    val center = scene.fromTextureToWorld(scene.textureSelector.selectionCenter)
                    val cursorParams = CursorParameters(center, scene.camera.zoom, scene.size.toIVector())

                    when (scene.textureSelector.transformationMode) {

                        TransformationMode.TRANSLATION -> {
                            renderTranslation(scene.textureSelector.selection, scene.sceneController, cursorParams)
                        }
                        TransformationMode.ROTATION -> {
                            renderRotation(scene.textureSelector.selection, scene.sceneController, cursorParams)
                        }
                        TransformationMode.SCALE -> {
                            renderTranslation(scene.textureSelector.selection, scene.sceneController, cursorParams)
                        }
                    }

                }
                GLStateMachine.depthTest.enable()
            }
            // camera mark
            if (Config.keyBindings.moveCamera.check(scene.sceneController.input) ||
                Config.keyBindings.rotateCamera.check(scene.sceneController.input)) {
                renderCursor(scene.size.toIVector())
            }
        }
    }

    private fun drawTextureSelection(sh: ShaderHandler, model: Model, textureSelection: VertexTexSelection) {
        sh.apply {
            GLStateMachine.useBlend(0.25f) {
                //TODO add zipVertexPaths for VertexTexSelection
//                draw(GL11.GL_QUADS, formatPCT) {
//                    val structure = model.zipVertexPaths(textureSelection).toStructure(model)
//                    structure.quads.forEach { quad ->
//                        renderQuad(this, quad)
//                    }
//                    structure.edges.forEach { edge ->
//                        renderEdge(this, edge)
//                    }
//                }
            }
        }
    }

    private fun drawModelSelection(sh: ShaderHandler, model: Model, modelSelection: VertexPosSelection,
                                   showAllMeshUVs: Boolean) {
        sh.apply {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
            GL11.glLineWidth(2f)

            draw(GL11.GL_QUADS, formatPCT) {
                val structure = model.zipVertexPaths(modelSelection).toStructure(model)
                structure.quads.forEach { quad ->
                    renderQuad(this, quad)
                }
                structure.edges.forEach { edge ->
                    renderEdge(this, edge)
                }
            }

            GL11.glLineWidth(1f)
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        }
    }

    private fun renderQuad(tes: ITessellator, quad: Quad) {
        quad.vertex
                .map { it.copy(tex = vec2Of(it.tex.x, 1 - it.tex.yd)) }
                .map { (it.tex * size) - offset }
                .forEach { tes.set(0, it.x, it.yd, 0).setVec(1, color).set(2, 0.0, 0.0).endVertex() }
    }

    private fun renderEdge(tes: ITessellator, edge: Edge) {
        (edge.vertex + edge.vertex)
                .map { it.copy(tex = vec2Of(it.tex.x, 1 - it.tex.yd)) }
                .map { (it.tex * size) - offset }
                .forEach { tes.set(0, it.x, it.yd, 0).setVec(1, color).set(2, 0.0, 0.0).endVertex() }
    }

    fun renderTranslation(selection: VertexTexSelection, controller: SceneController, params: CursorParameters) {

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        draw(GL11.GL_QUADS, shaderHandler.formatPC) {

            val center = params.center
            val radius = params.distanceFromCenter
            val start = radius - params.maxSizeOfSelectionBox / 2.0
            val end = radius + params.maxSizeOfSelectionBox / 2.0

//            if (selection !is VertexSelection) {
//                RenderUtil.renderBar(this, center, center, params.minSizeOfSelectionBox, vec3Of(1, 1, 1))
//            }

            for (axis in SelectionAxis.selectedValues) {
                val selected = controller.selectedModelAxis == axis || controller.hoveredModelAxis == axis
                RenderUtil.renderBar(this,
                        center + axis.direction * start,
                        center + axis.direction * end,
                        if (selected) params.minSizeOfSelectionBox * 3 else params.minSizeOfSelectionBox * 2,
                        color = Vector3.ONE)
                RenderUtil.renderBar(this,
                        center + axis.direction * start,
                        center + axis.direction * end,
                        if (selected) params.minSizeOfSelectionBox * 1.5 else params.minSizeOfSelectionBox,
                        color = axis.direction)
            }
        }
    }

    fun renderRotation(selection: VertexTexSelection, controller: SceneController, params: CursorParameters) {

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        draw(GL11.GL_QUADS, shaderHandler.formatPC) {

            val center = params.center
            val radius = params.distanceFromCenter

//            if (selection !is VertexSelection) {
//                RenderUtil.renderBar(this, center, center, params.minSizeOfSelectionBox, vec3Of(1, 1, 1))
//            }

            val selected = controller.hoveredTextureAxis != SelectionAxis.NONE ||
                           controller.selectedTextureAxis != SelectionAxis.NONE
            val direction = vec3Of(1, 0, 0)
            val rotationDirection = vec3Of(0, 1, 0)

            val edgePoint = center + direction * radius

            RenderUtil.renderBar(
                    tessellator = this,
                    startPoint = edgePoint - rotationDirection * params.maxSizeOfSelectionBox / 2,
                    endPoint = edgePoint + rotationDirection * params.maxSizeOfSelectionBox / 2,
                    size = if (selected) params.minSizeOfSelectionBox * 3 else params.minSizeOfSelectionBox * 2,
                    color = Vector3.ORIGIN
            )
            RenderUtil.renderBar(
                    tessellator = this,
                    startPoint = edgePoint - rotationDirection * params.maxSizeOfSelectionBox / 2,
                    endPoint = edgePoint + rotationDirection * params.maxSizeOfSelectionBox / 2,
                    size = if (selected) params.minSizeOfSelectionBox * 1.5 else params.minSizeOfSelectionBox,
                    color = Vector3.ONE
            )
        }
    }
}