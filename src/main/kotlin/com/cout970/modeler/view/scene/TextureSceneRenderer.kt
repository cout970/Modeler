package com.cout970.modeler.view.scene

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.ITessellator
import com.cout970.modeler.config.Config
import com.cout970.modeler.model.MaterialNone
import com.cout970.modeler.model.Model
import com.cout970.modeler.model.Quad
import com.cout970.modeler.modeleditor.selection.*
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.getArrowProperties
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.modeler.view.controller.TextureSelector
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
        val modelSelection = scene.modelProvider.selectionManager.modelSelection
        val textureSelection = scene.modelProvider.selectionManager.textureSelection
        val texture = model.groups.find { it.material != MaterialNone }?.material ?: MaterialNone

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

                if (textureSelection != SelectionNone) {
                    val center = scene.fromTextureToWorld(scene.textureSelector.selectionCenter)
                    renderTranslation(center, scene.textureSelector, scene.textureSelector.selection, scene.camera)
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

    private fun drawTextureSelection(sh: ShaderHandler, model: Model, textureSelection: ITextureSelection) {
        sh.apply {
            GLStateMachine.useBlend(0.25f) {
                draw(GL11.GL_QUADS, formatPCT) {
                    textureSelection.paths.forEach { path ->
                        if (path.level == ModelPath.Level.QUADS) {
                            renderQuad(this, path.getQuad(model)!!)
                        }
                    }
                }
            }
        }
    }

    private fun drawModelSelection(sh: ShaderHandler, model: Model, modelSelection: IModelSelection,
                                   showAllMeshUVs: Boolean) {
        sh.apply {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
            GL11.glLineWidth(2f)

            draw(GL11.GL_QUADS, formatPCT) {

                modelSelection.paths.forEach { path ->
                    when (path.level) {
                        ModelPath.Level.GROUPS -> {
                            path.getSubPaths(model).forEach { meshPath ->
                                meshPath.getSubPaths(model).forEach { quadPath ->
                                    renderQuad(this, quadPath.getQuad(model)!!)
                                }
                            }
                        }
                        ModelPath.Level.MESH -> {
                            path.getSubPaths(model).forEach { quadPath ->
                                renderQuad(this, quadPath.getQuad(model)!!)
                            }
                        }
                        ModelPath.Level.QUADS -> {
                            if (showAllMeshUVs) {
                                path.getParent().getSubPaths(model).forEach { quadPath ->
                                    renderQuad(this, quadPath.getQuad(model)!!)
                                }
                            } else {
                                renderQuad(this, path.getQuad(model)!!)
                            }
                        }
                        else -> {
                        }
                    }
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

    fun renderTranslation(center: IVector3, selector: TextureSelector, selection: ITextureSelection, camera: Camera) {
        val controller = selector.controller
        val selX = controller.selectedTextureAxis == SelectionAxis.X || controller.hoveredTextureAxis == SelectionAxis.X
        val selY = controller.selectedTextureAxis == SelectionAxis.Y || controller.hoveredTextureAxis == SelectionAxis.Y

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        draw(GL11.GL_QUADS, shaderHandler.formatPC) {

            val (scale, radius, size) = getArrowProperties(camera.zoom)
            val start = radius - 0.2 * scale
            val end = radius + 0.2 * scale

            if (selection.textureMode != TextureSelectionMode.VERTEX) {
                RenderUtil.renderBar(this, center, center, size * 1.5, vec3Of(1, 1, 1))
            }

            RenderUtil.renderBar(this, center + vec3Of(start, 0, 0), center + vec3Of(end, 0, 0),
                    if (selX) size * 2.5 else size * 1.5, color = vec3Of(1, 1, 1))
            RenderUtil.renderBar(this, center + vec3Of(0, start, 0), center + vec3Of(0, end, 0),
                    if (selY) size * 2.5 else size * 1.5, color = vec3Of(1, 1, 1))

            RenderUtil.renderBar(this, center + vec3Of(start, 0, 0), center + vec3Of(end, 0, 0),
                    if (selX) size * 1.5 else size, color = vec3Of(1, 0, 0))
            RenderUtil.renderBar(this, center + vec3Of(0, start, 0), center + vec3Of(0, end, 0),
                    if (selY) size * 1.5 else size, color = vec3Of(0, 1, 0))
        }
    }
}