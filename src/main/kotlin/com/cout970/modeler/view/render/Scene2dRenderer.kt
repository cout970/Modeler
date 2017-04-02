package com.cout970.modeler.view.render

import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.render.comp.*
import com.cout970.modeler.view.scene.Scene2d
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.yf

/**
 * Created by cout970 on 2017/01/23.
 */
class Scene2dRenderer(shaderHandler: ShaderHandler) : SceneRenderer(shaderHandler) {

    override val components: Map<ShaderType, List<IRenderableComponent>> = mapOf(
            ShaderType.UV_SHADER to listOf(UVOutlineRenderComponent(),
                    TextureRenderComponent(), UVSelectionRenderComponent()
            ),
            ShaderType.PLAIN_3D_SHADER to listOf(Cursor3dRenderComponent())
    )

    fun render(scene: Scene2d) {

        if (scene.size.x < 1 || scene.size.y < 1) return

        val context = RenderContext(
                shaderHandler = shaderHandler,
                modelProvider = scene.modelProvider,
                model = scene.sceneController.getModel(scene.modelProvider.model),
                selectionManager = scene.modelProvider.selectionManager,
                sceneController = scene.sceneController,
                scene = scene
        )

        val viewportPos = vec2Of(
                scene.absolutePosition.x,
                scene.windowHandler.window.size.yf - (scene.absolutePosition.yf + scene.size.y)
        )
        scene.windowHandler.saveViewport(viewportPos, scene.size.toIVector()) {

            val uvShaderComponents = components[ShaderType.UV_SHADER] ?: emptyList()
            if (uvShaderComponents.isNotEmpty()) {
                shaderHandler.useUVShader(scene.getMatrixMVP()) {
                    uvShaderComponents.forEach { it.render(context) }
                }
            }
        }
    }
//
//    fun renderTranslation(selection: VertexTexSelection, controller: SceneController, cursor: Cursor) {
//
//        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
//        draw(GL11.GL_QUADS, shaderHandler.formatPC) {
//
//            val center = cursor.center
//            val radius = cursor.parameters.distanceFromCenter
//            val start = radius - cursor.parameters.maxSizeOfSelectionBox / 2.0
//            val end = radius + cursor.parameters.maxSizeOfSelectionBox / 2.0
//
////            if (selection !is VertexSelection) {
////                RenderUtil.renderBar(this, center, center, params.minSizeOfSelectionBox, vec3Of(1, 1, 1))
////            }
//
//            for (axis in SelectionAxis.selectedValues) {
//                val selected = controller.selectedModelAxis == axis || controller.hoveredModelAxis == axis
//                RenderUtil.renderBar(tessellator = this,
//                        startPoint = center + axis.direction * start,
//                        endPoint = center + axis.direction * end,
//                        size = if (selected) cursor.parameters.minSizeOfSelectionBox * 3 else cursor.parameters.minSizeOfSelectionBox * 2,
//                        color = Vector3.ONE)
//
//                RenderUtil.renderBar(tessellator = this,
//                        startPoint = center + axis.direction * start,
//                        endPoint = center + axis.direction * end,
//                        size = if (selected) cursor.parameters.minSizeOfSelectionBox * 1.5 else cursor.parameters.minSizeOfSelectionBox,
//                        color = axis.direction)
//            }
//        }
//    }
//
//    fun renderRotation(selection: VertexTexSelection, controller: SceneController, cursor: Cursor) {
//
//        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
//        draw(GL11.GL_QUADS, shaderHandler.formatPC) {
//
//            val center = cursor.center
//            val radius = cursor.parameters.distanceFromCenter
//
////            if (selection !is VertexSelection) {
////                RenderUtil.renderBar(this, center, center, params.minSizeOfSelectionBox, vec3Of(1, 1, 1))
////            }
//
//            val selected = controller.hoveredTextureAxis != SelectionAxis.NONE ||
//                           controller.selectedTextureAxis != SelectionAxis.NONE
//            val direction = vec3Of(1, 0, 0)
//            val rotationDirection = vec3Of(0, 1, 0)
//
//            val edgePoint = center + direction * radius
//
//            RenderUtil.renderBar(
//                    tessellator = this,
//                    startPoint = edgePoint - rotationDirection * cursor.parameters.maxSizeOfSelectionBox / 2,
//                    endPoint = edgePoint + rotationDirection * cursor.parameters.maxSizeOfSelectionBox / 2,
//                    size = if (selected) cursor.parameters.minSizeOfSelectionBox * 3 else cursor.parameters.minSizeOfSelectionBox * 2,
//                    color = Vector3.ORIGIN
//            )
//            RenderUtil.renderBar(
//                    tessellator = this,
//                    startPoint = edgePoint - rotationDirection * cursor.parameters.maxSizeOfSelectionBox / 2,
//                    endPoint = edgePoint + rotationDirection * cursor.parameters.maxSizeOfSelectionBox / 2,
//                    size = if (selected) cursor.parameters.minSizeOfSelectionBox * 1.5 else cursor.parameters.minSizeOfSelectionBox,
//                    color = Vector3.ONE
//            )
//        }
//    }
}