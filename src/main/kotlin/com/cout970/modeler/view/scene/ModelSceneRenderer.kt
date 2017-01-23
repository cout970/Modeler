package com.cout970.modeler.view.scene

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.VAO
import com.cout970.matrix.extensions.Matrix4
import com.cout970.matrix.extensions.times
import com.cout970.matrix.extensions.transpose
import com.cout970.modeler.config.Config
import com.cout970.modeler.modeleditor.selection.ModelPath
import com.cout970.modeler.modeleditor.selection.Selection
import com.cout970.modeler.modeleditor.selection.SelectionMode
import com.cout970.modeler.modeleditor.selection.SelectionNone
import com.cout970.modeler.util.Cache
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.controller.ModelSelector
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.modeler.view.controller.TransformationMode
import com.cout970.modeler.view.util.ShaderHandler
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/01/23.
 */
class ModelSceneRenderer(shaderHandler: ShaderHandler) : SceneRenderer(shaderHandler) {

    fun render(scene: ModelScene) {
        if (scene.size.x < 1 || scene.size.y < 1) return

        val model = scene.sceneController.getModel(scene.modelProvider.model)
        val selection = scene.modelProvider.selectionManager.selection
        val sceneController = scene.sceneController

        val modelCache: Cache<Int, VAO> = sceneController.modelCache
        val selectionCache: Cache<Int, VAO> = sceneController.selectionCache

        if (scene.modelProvider.modelNeedRedraw) {
            scene.modelProvider.modelNeedRedraw = false
            sceneController.modelCache.clear()
            sceneController.selectionCache.clear()
        }

        val y = scene.parent.size.y - (scene.position.y + scene.size.y)
        scene.windowHandler.saveViewport(vec2Of(scene.absolutePosition.x, y), scene.size.toIVector()) {

            // normal shader with light and texture
            shaderHandler.useModelShader(
                    lights = listOf(vec3Of(500, 1000, 750), vec3Of(-500, -1000, -750)),
                    lightColors = listOf(Vector3.ONE, Vector3.ONE),
                    shineDamper = 1f,
                    reflectivity = 0f
            ) {
                matrixP = scene.getProjectionMatrix()
                matrixV = scene.getViewMatrix()

                // render base model
                for (group in model.groups) {
                    group.material.bind()
                    matrixM = group.transform.matrix.transpose()

                    renderCache(modelCache, model.hashCode()) {
                        tessellator.compile(GL11.GL_QUADS, formatPTN) {
                            group.getQuads().forEach { quad ->
                                val norm = quad.normal
                                quad.vertex.forEach { (pos, tex) ->
                                    set(0, pos.x, pos.y, pos.z).set(1, tex.xd, tex.yd).set(2, norm.x, norm.y,
                                            norm.z).endVertex()
                                }
                            }
                        }
                    }
                }
            }

            // extra shader only with plain color
            shaderHandler.useSingleColorShader {

                matrixP = scene.getProjectionMatrix()
                matrixV = scene.getViewMatrix()
                matrixM = Matrix4.IDENTITY

                // axis grids
                drawGrids()

                // selection outline
                if (selection != SelectionNone) {

                    // render selection
                    renderCache(selectionCache, model.hashCode() xor selection.hashCode()) {
                        val size = Config.selectionThickness.toDouble()
                        tessellator.compile(GL11.GL_QUADS, formatPC) {
                            if (selection.mode != SelectionMode.VERTEX) {
                                model.getQuadsOptimized(selection) { (a, b, c, d) ->
                                    RenderUtil.renderBar(tessellator, a.pos, b.pos, size)
                                    RenderUtil.renderBar(tessellator, b.pos, c.pos, size)
                                    RenderUtil.renderBar(tessellator, c.pos, d.pos, size)
                                    RenderUtil.renderBar(tessellator, d.pos, a.pos, size)
                                }
                            } else {
                                model.getPaths(ModelPath.Level.MESH).forEach { compPath ->
                                    val paths = selection.paths.filter {
                                        it.compareLevel(compPath, ModelPath.Level.MESH)
                                    }
                                    if (paths.isNotEmpty()) {
                                        val matrix = compPath.getMeshMatrix(model)
                                        paths.map { it.getVertex(model)!! }.map { matrix * it.toVector4(1.0) }.forEach {
                                            RenderUtil.renderBar(tessellator, it, it, size * 4)
                                        }
                                    }
                                }

                            }
                        }
                    }

                    // render selected quads
                    if (selection.mode == SelectionMode.QUAD) {
                        GLStateMachine.useBlend(0.5f) {

                            renderCache(selectionCache, model.hashCode() xor (selection.hashCode() + 1)) {
                                tessellator.compile(GL11.GL_QUADS, formatPC) {
                                    model.getQuadsOptimized(selection) { quad ->
                                        quad.vertex.forEach { (pos, _) ->
                                            set(0, pos.xd + 0.1, pos.yd + 0.1, pos.zd + 0.1).set(1, 0.5, 0.5,
                                                    0.4).endVertex()
                                        }
                                        quad.vertex.forEach { (pos, _) ->
                                            set(0, pos.xd - 0.1, pos.yd - 0.1, pos.zd - 0.1).set(1, 0.5, 0.5,
                                                    0.4).endVertex()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3D cursor
                val selector = scene.modelSelector
                if (selection != SelectionNone && selector.transformationMode != TransformationMode.NONE) {
                    when (selector.transformationMode) {
                        TransformationMode.TRANSLATION -> {
                            renderTranslation(sceneController.cursorCenter, selector, selection, scene.camera)
                        }
                        TransformationMode.ROTATION -> {
                            renderRotation(selection.getCenter(model), selector, selection, scene.camera)
                        }
                        TransformationMode.NONE -> Unit
                        TransformationMode.SCALE -> Unit
                    }
                }
            }
            // 2D cursor
            if (Config.keyBindings.moveCamera.check(sceneController.input) ||
                Config.keyBindings.rotateCamera.check(sceneController.input)) {
                renderCursor(scene.size.toIVector())
            }
        }
    }

    fun renderRotation(center: IVector3, selector: ModelSelector, selection: Selection, camera: Camera) {
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        val controller = selector.controller
        val selX = controller.selectedAxis == SelectionAxis.X || controller.hoveredAxis == SelectionAxis.X
        val selY = controller.selectedAxis == SelectionAxis.Y || controller.hoveredAxis == SelectionAxis.Y
        val selZ = controller.selectedAxis == SelectionAxis.Z || controller.hoveredAxis == SelectionAxis.Z

        draw(GL11.GL_QUADS, shaderHandler.formatPC) {
            val (scale, radius, size) = selector.getArrowProperties(camera.zoom)

            if (selection.mode != SelectionMode.VERTEX) {
                RenderUtil.renderBar(this, center, center, size * 1.5, vec3Of(1, 1, 1))
            }

            //if one of the axis is selected
            if (controller.selectedAxis != SelectionAxis.NONE) {

                val axis = controller.selectedAxis
                RenderUtil.renderCircle(this, center, axis,
                        radius, Config.cursorLinesSize * scale * 0.03125, axis.axis)

            } else {
                for (axis in SelectionAxis.selectedValues) {
                    RenderUtil.renderCircle(this, center, axis,
                            radius, Config.cursorLinesSize * scale * 0.03125, axis.axis)
                }

                RenderUtil.renderBar(this, center + vec3Of(radius, 0, -0.2 * scale),
                        center + vec3Of(radius, 0, 0.2 * scale), if (selX) size * 1.5 else size, color = vec3Of(1))

                RenderUtil.renderBar(this, center + vec3Of(-0.2 * scale, radius, 0),
                        center + vec3Of(0.2 * scale, radius, 0), if (selY) size * 1.5 else size, color = vec3Of(1))

                RenderUtil.renderBar(this, center + vec3Of(0, -0.2 * scale, radius),
                        center + vec3Of(0, 0.2 * scale, radius), if (selZ) size * 1.5 else size, color = vec3Of(1))
            }
        }
    }

    fun renderTranslation(center: IVector3, selector: ModelSelector, selection: Selection, camera: Camera) {
        val controller = selector.controller
        val selX = controller.selectedAxis == SelectionAxis.X || controller.hoveredAxis == SelectionAxis.X
        val selY = controller.selectedAxis == SelectionAxis.Y || controller.hoveredAxis == SelectionAxis.Y
        val selZ = controller.selectedAxis == SelectionAxis.Z || controller.hoveredAxis == SelectionAxis.Z

        if (Config.enableHelperGrid && selector.scene.perspective && controller.selectedAxis != SelectionAxis.NONE) {
            draw(GL11.GL_LINES, shaderHandler.formatPC) {
                val grey = vec3Of(0.5)
                val red = vec3Of(1, 0, 0)
                var col: IVector3
                if (selX || selZ) {
                    for (x in -160..160) {
                        col = if (x % 16 == 0) red else grey
                        set(0, x, center.y, -160).set(1, col.x, col.y, col.z).endVertex()
                        set(0, x, center.y, 160).set(1, col.x, col.y, col.z).endVertex()
                    }
                    for (z in -160..160) {
                        col = if (z % 16 == 0) red else grey
                        set(0, -160, center.y, z).set(1, col.x, col.y, col.z).endVertex()
                        set(0, 160, center.y, z).set(1, col.x, col.y, col.z).endVertex()
                    }
                } else if (selY) {
                    for (z in -160..160) {
                        col = if (z % 16 == 0) red else grey
                        set(0, -160, z, center.z).set(1, col.x, col.y, col.z).endVertex()
                        set(0, 160, z, center.z).set(1, col.x, col.y, col.z).endVertex()
                    }
                    for (x in -160..160) {
                        col = if (x % 16 == 0) red else grey
                        set(0, x, -160, center.z).set(1, col.x, col.y, col.z).endVertex()
                        set(0, x, 160, center.z).set(1, col.x, col.y, col.z).endVertex()
                    }
                }
            }
        }
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        draw(GL11.GL_QUADS, shaderHandler.formatPC) {

            val (scale, radius, size) = selector.getArrowProperties(camera.zoom)
            val start = radius - 0.2 * scale
            val end = radius + 0.2 * scale

            if (selection.mode != SelectionMode.VERTEX) {
                RenderUtil.renderBar(this, center, center, size * 1.5, vec3Of(1, 1, 1))
            }

            RenderUtil.renderBar(this, center + vec3Of(start, 0, 0), center + vec3Of(end, 0, 0),
                    if (selX) size * 1.5 else size, color = vec3Of(1, 0, 0))
            RenderUtil.renderBar(this, center + vec3Of(0, start, 0), center + vec3Of(0, end, 0),
                    if (selY) size * 1.5 else size, color = vec3Of(0, 1, 0))
            RenderUtil.renderBar(this, center + vec3Of(0, 0, start), center + vec3Of(0, 0, end),
                    if (selZ) size * 1.5 else size, color = vec3Of(0, 0, 1))
        }
    }

    fun drawGrids() {
        draw(GL11.GL_LINES, shaderHandler.formatPC) {
            set(0, -10, 0, 0).set(1, 1, 0, 0).endVertex()
            set(0, 10, 0, 0).set(1, 1, 0, 0).endVertex()

            set(0, 0, -10, 0).set(1, 0, 1, 0).endVertex()
            set(0, 0, 10, 0).set(1, 0, 1, 0).endVertex()

            set(0, 0, 0, -10).set(1, 0, 0, 1).endVertex()
            set(0, 0, 0, 10).set(1, 0, 0, 1).endVertex()

            val dist = -1024 * 15

            //y
            for (x in -160..160) {
                set(0, x, dist, -160).set(1, 0.5, 0.5, 0.5).endVertex()
                set(0, x, dist, 160).set(1, 0.5, 0.5, 0.5).endVertex()
            }
            for (z in -160..160) {
                set(0, -160, dist, z).set(1, 0.5, 0.5, 0.5).endVertex()
                set(0, 160, dist, z).set(1, 0.5, 0.5, 0.5).endVertex()
            }

            //x
            for (x in -160..160) {
                set(0, dist, x, -160).set(1, 0.5, 0.5, 0.5).endVertex()
                set(0, dist, x, 160).set(1, 0.5, 0.5, 0.5).endVertex()
            }
            for (z in -160..160) {
                set(0, dist, -160, z).set(1, 0.5, 0.5, 0.5).endVertex()
                set(0, dist, 160, z).set(1, 0.5, 0.5, 0.5).endVertex()
            }

            //z
            for (z in -160..160) {
                set(0, -160, z, dist).set(1, 0.5, 0.5, 0.5).endVertex()
                set(0, 160, z, dist).set(1, 0.5, 0.5, 0.5).endVertex()
            }
            for (x in -160..160) {
                set(0, x, -160, dist).set(1, 0.5, 0.5, 0.5).endVertex()
                set(0, x, 160, dist).set(1, 0.5, 0.5, 0.5).endVertex()
            }

            for (x in -7..8) {
                set(0, x * 16, 0, -7 * 16).set(1, 1.0, 0.0, 0.0).endVertex()
                set(0, x * 16, 0, 8 * 16).set(1, 1.0, 0.0, 0.0).endVertex()
            }

            for (z in -7..8) {
                set(0, -7 * 16, 0, z * 16).set(1, 1.0, 0.0, 0.0).endVertex()
                set(0, 8 * 16, 0, z * 16).set(1, 1.0, 0.0, 0.0).endVertex()
            }
        }
    }
}