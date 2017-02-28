package com.cout970.modeler.view.scene

import com.cout970.glutilities.tessellator.VAO
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.config.Config
import com.cout970.modeler.model.api.IElementLeaf
import com.cout970.modeler.model.material.MaterialNone
import com.cout970.modeler.model.util.getLeafElements
import com.cout970.modeler.model.util.toAABB
import com.cout970.modeler.selection.VertexPosSelection
import com.cout970.modeler.util.*
import com.cout970.modeler.view.controller.SceneController
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

    fun render(scene: SceneModel) {
        if (scene.size.x < 1 || scene.size.y < 1) return

        val model = scene.sceneController.getModel(scene.modelProvider.model)
        val selection = scene.modelProvider.selectionManager.vertexPosSelection
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
                matrixM = Matrix4.IDENTITY

                MaterialNone.bind()
                renderCache(modelCache, model.hashCode()) {
                    tessellator.compile(GL11.GL_QUADS, formatPTN) {
                        model.getQuads().forEach { quad ->
                            val norm = quad.normal
                            quad.vertex.forEach { (pos, tex) ->
                                set(0, pos.x, pos.y, pos.z)
                                        .set(1, tex.xd, tex.yd)
                                        .set(2, norm.x, norm.y, norm.z).endVertex()
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
                drawGrids(sceneController, scene.perspective)

                // bounding boxes
                if (sceneController.showBoundingBoxes.get()) {
                    draw(GL11.GL_LINES, formatPC) {
                        model.getLeafElements().map(IElementLeaf::toAABB).forEach {
                            RenderUtil.renderBox(this, it)
                        }
                    }
                }

                // selection outline
                if (selection != VertexPosSelection.EMPTY) {

                    // render selection
                    renderCache(selectionCache, model.hashCode() xor selection.hashCode()) {
                        val size = Config.selectionThickness.toDouble()
                        val color = Config.colorPalette.modelSelectionColor
                        tessellator.compile(GL11.GL_QUADS, formatPC) {
                            if (selection !is VertexSelection) {
                                selection.paths.flatMap { model.getQuads(it) }.forEach { (a, b, c, d) ->
                                    RenderUtil.renderBar(tessellator, a.pos, b.pos, size, color)
                                    RenderUtil.renderBar(tessellator, b.pos, c.pos, size, color)
                                    RenderUtil.renderBar(tessellator, c.pos, d.pos, size, color)
                                    RenderUtil.renderBar(tessellator, d.pos, a.pos, size, color)
                                }
                            } else {

                                val structure = RenderUtil.zipVertexPaths(model, selection).toStructure(model)

                                structure.quads.forEach { (a, b, c, d) ->
                                    RenderUtil.renderBar(tessellator, a.pos, b.pos, size, color)
                                    RenderUtil.renderBar(tessellator, b.pos, c.pos, size, color)
                                    RenderUtil.renderBar(tessellator, c.pos, d.pos, size, color)
                                    RenderUtil.renderBar(tessellator, d.pos, a.pos, size, color)
                                }

                                structure.edges.forEach { (a, b) ->
                                    RenderUtil.renderBar(tessellator, a.pos, b.pos, size, color)
                                }

                                structure.vertex.forEach { (pos) ->
                                    RenderUtil.renderBar(tessellator, pos, pos, size * 4, color)
                                }
                            }
                        }
                    }

                    // render selected quads
                    //TODO selection mode for quads
//                    if (selection.modelMode == ModelSelectionMode.QUAD) {
//                        GLStateMachine.useBlend(0.25f) {
//                            val color = Config.colorPalette.modelSelectionColor
//
//                            renderCache(selectionCache, model.hashCode() xor (selection.hashCode() + 1)) {
//                                tessellator.compile(GL11.GL_QUADS, formatPC) {
//                                    model.getQuadsOptimized(selection) { quad ->
//                                        quad.vertex.forEach { (pos, _) ->
//                                            set(0, pos.xd + 0.1, pos.yd + 0.1, pos.zd + 0.1)
//                                                    .setVec(1, color).endVertex()
//                                        }
//                                        quad.vertex.forEach { (pos, _) ->
//                                            set(0, pos.xd - 0.1, pos.yd - 0.1, pos.zd - 0.1)
//                                                    .setVec(1, color).endVertex()
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
                }

                // 3D cursor
                val selector = scene.modelSelector
                if (selection != SelectionNone) {

                    when (selector.transformationMode) {
                        TransformationMode.TRANSLATION -> {
                            val cursorParams = CursorParameters(
                                    sceneController.cursorCenter,
                                    scene.camera.zoom,
                                    scene.size.toIVector())

                            renderTranslation(selection, sceneController, cursorParams, scene.perspective)
                        }
                        TransformationMode.ROTATION -> {
                            val cursorParams = CursorParameters(
                                    selection.center3D(model),
                                    scene.camera.zoom,
                                    scene.size.toIVector())

                            renderRotation(selection, scene.sceneController, cursorParams)
                        }
                        TransformationMode.SCALE -> {
                            val cursorParams = CursorParameters(
                                    sceneController.cursorCenter,
                                    scene.camera.zoom,
                                    scene.size.toIVector())

                            renderTranslation(selection, sceneController, cursorParams, scene.perspective)
                        }
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

    fun renderRotation(selection: Selection, controller: SceneController, cursorParams: CursorParameters) {

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        val center = cursorParams.center

        draw(GL11.GL_QUADS, shaderHandler.formatPC) {

            if (selection !is VertexSelection) {
                RenderUtil.renderBar(this, center, center, cursorParams.minSizeOfSelectionBox, vec3Of(1, 1, 1))
            }

            //if one of the axis is selected
            if (controller.selectedModelAxis != SelectionAxis.NONE) {

                val axis = controller.selectedModelAxis
                RenderUtil.renderCircle(this, center, axis,
                        cursorParams.distanceFromCenter,
                        Config.cursorLinesSize * cursorParams.minSizeOfSelectionBox,
                        axis.direction)

            } else {
                for (axis in SelectionAxis.selectedValues) {
                    RenderUtil.renderCircle(this, center, axis,
                            cursorParams.distanceFromCenter,
                            Config.cursorLinesSize * cursorParams.minSizeOfSelectionBox,
                            axis.direction)
                }

                val radius = cursorParams.distanceFromCenter

                for (axis in SelectionAxis.selectedValues) {
                    val edgePoint = center + axis.direction * radius
                    val selected = controller.selectedModelAxis == axis || controller.hoveredModelAxis == axis
                    RenderUtil.renderBar(this,
                            edgePoint - axis.rotationDirection * cursorParams.maxSizeOfSelectionBox / 2,
                            edgePoint + axis.rotationDirection * cursorParams.maxSizeOfSelectionBox / 2,
                            if (selected) cursorParams.minSizeOfSelectionBox * 1.5 else cursorParams.minSizeOfSelectionBox,
                            color = vec3Of(1))
                }
            }
        }
    }

    fun renderTranslation(selection: Selection, controller: SceneController, params: CursorParameters,
                          perspective: Boolean) {

        if (Config.enableHelperGrid && perspective && controller.selectedModelAxis != SelectionAxis.NONE) {
            drawHelperGrids(params.center, controller.selectedModelAxis)
        }
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        draw(GL11.GL_QUADS, shaderHandler.formatPC) {

            val center = params.center
            val radius = params.distanceFromCenter
            val start = radius - params.maxSizeOfSelectionBox / 2.0
            val end = radius + params.maxSizeOfSelectionBox / 2.0

            if (selection !is VertexSelection) {
                RenderUtil.renderBar(this, center, center, params.minSizeOfSelectionBox, vec3Of(1, 1, 1))
            }

            for (axis in SelectionAxis.selectedValues) {
                val selected = controller.selectedModelAxis == axis || controller.hoveredModelAxis == axis
                RenderUtil.renderBar(this,
                        center + axis.direction * start,
                        center + axis.direction * end,
                        if (selected) params.minSizeOfSelectionBox * 1.5 else params.minSizeOfSelectionBox,
                        color = axis.direction)
            }
        }
    }

    private fun drawHelperGrids(center: IVector3, axis: SelectionAxis) {
        draw(GL11.GL_LINES, shaderHandler.formatPC) {
            val grid1 = Config.colorPalette.grid1Color
            val grid2 = Config.colorPalette.grid2Color
            var col: IVector3
            if (axis != SelectionAxis.Y) {
                for (x in -160..160) {
                    col = if (x % 16 == 0) grid2 else grid1
                    set(0, x, center.y, -160).set(1, col.x, col.y, col.z).endVertex()
                    set(0, x, center.y, 160).set(1, col.x, col.y, col.z).endVertex()
                }
                for (z in -160..160) {
                    col = if (z % 16 == 0) grid2 else grid1
                    set(0, -160, center.y, z).set(1, col.x, col.y, col.z).endVertex()
                    set(0, 160, center.y, z).set(1, col.x, col.y, col.z).endVertex()
                }
            } else {
                for (z in -160..160) {
                    col = if (z % 16 == 0) grid2 else grid1
                    set(0, -160, z, center.z).set(1, col.x, col.y, col.z).endVertex()
                    set(0, 160, z, center.z).set(1, col.x, col.y, col.z).endVertex()
                }
                for (x in -160..160) {
                    col = if (x % 16 == 0) grid2 else grid1
                    set(0, x, -160, center.z).set(1, col.x, col.y, col.z).endVertex()
                    set(0, x, 160, center.z).set(1, col.x, col.y, col.z).endVertex()
                }
            }
        }
    }

    fun drawGrids(controller: SceneController, perspective: Boolean) {
        draw(GL11.GL_LINES, shaderHandler.formatPC) {
            set(0, -10, 0, 0).set(1, 1, 0, 0).endVertex()
            set(0, 10, 0, 0).set(1, 1, 0, 0).endVertex()

            set(0, 0, -10, 0).set(1, 0, 1, 0).endVertex()
            set(0, 0, 10, 0).set(1, 0, 1, 0).endVertex()

            set(0, 0, 0, -10).set(1, 0, 0, 1).endVertex()
            set(0, 0, 0, 10).set(1, 0, 0, 1).endVertex()

            val dist = -1024 * 15
            val grid1 = Config.colorPalette.grid1Color
            val grid2 = Config.colorPalette.grid2Color
            var color: IVector3

            //y
            for (x in -160..160) {
                color = if (x % 16 == 0) grid2 else grid1
                set(0, x, dist, -160).setVec(1, color).endVertex()
                set(0, x, dist, 160).setVec(1, color).endVertex()
            }
            for (z in -160..160) {
                color = if (z % 16 == 0) grid2 else grid1
                set(0, -160, dist, z).setVec(1, color).endVertex()
                set(0, 160, dist, z).setVec(1, color).endVertex()
            }

            //x
            for (x in -160..160) {
                color = if (x % 16 == 0) grid2 else grid1
                set(0, dist, x, -160).setVec(1, color).endVertex()
                set(0, dist, x, 160).setVec(1, color).endVertex()
            }
            for (z in -160..160) {
                color = if (z % 16 == 0) grid2 else grid1
                set(0, dist, -160, z).setVec(1, color).endVertex()
                set(0, dist, 160, z).setVec(1, color).endVertex()
            }

            //z
            for (z in -160..160) {
                color = if (z % 16 == 0) grid2 else grid1
                set(0, -160, z, dist).setVec(1, color).endVertex()
                set(0, 160, z, dist).setVec(1, color).endVertex()
            }
            for (x in -160..160) {
                color = if (x % 16 == 0) grid2 else grid1
                set(0, x, -160, dist).setVec(1, color).endVertex()
                set(0, x, 160, dist).setVec(1, color).endVertex()
            }

            val selX = controller.selectedModelAxis == SelectionAxis.X
            val selY = controller.selectedModelAxis == SelectionAxis.Y
            val selZ = controller.selectedModelAxis == SelectionAxis.Z
            if (!selX && !selY && !selZ || !perspective || controller.transformationMode != TransformationMode.TRANSLATION) {
                color = Config.colorPalette.grid2Color

                for (x in -7..8) {
                    set(0, x * 16, 0, -7 * 16).setVec(1, color).endVertex()
                    set(0, x * 16, 0, 8 * 16).setVec(1, color).endVertex()
                }

                for (z in -7..8) {
                    set(0, -7 * 16, 0, z * 16).setVec(1, color).endVertex()
                    set(0, 8 * 16, 0, z * 16).setVec(1, color).endVertex()
                }
            }

        }
    }
}