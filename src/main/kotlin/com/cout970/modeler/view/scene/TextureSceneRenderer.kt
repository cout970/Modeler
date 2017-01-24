package com.cout970.modeler.view.scene

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.matrix.extensions.times
import com.cout970.modeler.config.Config
import com.cout970.modeler.model.MaterialNone
import com.cout970.modeler.model.Quad
import com.cout970.modeler.modeleditor.selection.ModelPath
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.util.ShaderHandler
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.times
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.yd
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/01/23.
 */
class TextureSceneRenderer(shaderHandler: ShaderHandler) : SceneRenderer(shaderHandler) {

    fun render(scene: TextureScene) {

        if (scene.size.x < 1 || scene.size.y < 1) return

        val model = scene.modelProvider.model
        val selection = scene.modelProvider.selectionManager.selection

        val texture = model.groups.find { it.material != MaterialNone }?.material ?: MaterialNone
        val scale = 64.0
        val divs = 128
        val offset = scale / 2

        val y = scene.parent.size.y - (scene.position.y + scene.size.y)
        scene.windowHandler.saveViewport(vec2Of(scene.absolutePosition.x, y), scene.size.toIVector()) {
            shaderHandler.useUVShader(scene.createOrthoMatrix() * scene.camera.matrixForUV) {
                GLStateMachine.depthTest.disable()
                MaterialNone.bind()
                enableColor = true
                draw(GL11.GL_LINES, formatPCT) {
                    for (x in 0..divs) {
                        set(0, -offset + x * (scale / divs), -offset, 0).set(1, 0.5, 0.5, 0.5).set(2, 0.0,
                                0.0).endVertex()
                        set(0, -offset + x * (scale / divs), scale - offset, 0).set(1, 0.5, 0.5, 0.5).set(2, 0.0,
                                0.0).endVertex()
                    }

                    for (z in 0..divs) {
                        set(0, -offset, z * (scale / divs) - offset, 0).set(1, 0.5, 0.5, 0.5).set(2, 0.0,
                                0.0).endVertex()
                        set(0, -offset + scale, z * (scale / divs) - offset, 0).set(1, 0.5, 0.5, 0.5).set(2, 0.0,
                                0.0).endVertex()
                    }
                }

                enableColor = false
                texture.bind()
                draw(GL11.GL_QUADS, formatPCT) {
                    set(0, -offset, -offset, 0).set(1, 1, 1, 1).set(2, 0.0, 1.0).endVertex()
                    set(0, -offset + scale, -offset, 0).set(1, 1, 1, 1).set(2, 1.0, 1.0).endVertex()
                    set(0, -offset + scale, scale - offset, 0).set(1, 1, 1, 1).set(2, 1.0, 0.0).endVertex()
                    set(0, -offset, scale - offset, 0).set(1, 1, 1, 1).set(2, 0.0, 0.0).endVertex()
                }

                enableColor = true
                MaterialNone.bind()
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
                GL11.glLineWidth(2f)

                val color = Config.colorPalette.textureSelectionColor

                draw(GL11.GL_QUADS, formatPCT) {
                    val renderQuad: (Quad) -> Unit = { quad ->
                        quad.vertex
                                .map { it.copy(tex = vec2Of(it.tex.x, 1 - it.tex.yd)) }
                                .map { (it.tex * scale) - offset }
                                .forEach { set(0, it.x, it.yd, 0).setVec(1, color).set(2, 0.0, 0.0).endVertex() }
                    }

                    selection.paths.forEach { path ->
                        when (path.level) {
                            ModelPath.Level.GROUPS -> {
                                path.getSubPaths(model).forEach { meshPath ->
                                    meshPath.getSubPaths(model).forEach { quadPath ->
                                        renderQuad(quadPath.getQuad(model)!!)
                                    }
                                }
                            }
                            ModelPath.Level.MESH -> {
                                path.getSubPaths(model).forEach { quadPath ->
                                    renderQuad(quadPath.getQuad(model)!!)
                                }
                            }
                            ModelPath.Level.QUADS -> {
                                renderQuad(path.getQuad(model)!!)
                            }
                            else -> {
                            }
                        }
                    }
                }

                GL11.glLineWidth(1f)
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
            }
            // 2D cursor
            if (Config.keyBindings.moveCamera.check(scene.sceneController.input) ||
                Config.keyBindings.rotateCamera.check(scene.sceneController.input)) {
                renderCursor(scene.size.toIVector())
            }
        }
    }
}