package com.cout970.modeler.view.render

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.render.comp.*
import com.cout970.modeler.view.scene.Scene3d
import com.cout970.modeler.view.scene.SceneRenderer
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import com.cout970.vector.extensions.yf

/**
 * Created by cout970 on 2017/01/23.
 */
class Scene3dRenderer(shaderHandler: ShaderHandler) : SceneRenderer(shaderHandler) {

    override val components = mapOf(
            ShaderType.FULL_SHADER to listOf(ModelRenderComponent()),
            ShaderType.PLAIN_3D_SHADER to listOf(
                    GridsRenderComponent(), SelectionRenderComponent(),
                    AABBRenderComponent(), Cursor3dRenderComponent()
            ),
            ShaderType.UV_SHADER to listOf(Cursor2dRenderComponent())
    )

    fun render(scene: Scene3d) {
        if (scene.size.x < 1 || scene.size.y < 1) return

        val modelProvider = scene.modelProvider
        val sceneController = scene.sceneController

        if (modelProvider.modelNeedRedraw) {
            modelProvider.modelNeedRedraw = false
            sceneController.modelCache.clear()
            sceneController.selectionCache.clear()
        }

        val context = RenderContext(
                shaderHandler = shaderHandler,
                modelProvider = modelProvider,
                model = scene.sceneController.getModel(modelProvider.model),
                selectionManager = modelProvider.selectionManager,
                sceneController = sceneController,
                scene = scene
        )

        val viewportPos = vec2Of(
                scene.absolutePosition.x,
                scene.windowHandler.window.size.yf - (scene.absolutePosition.yf + scene.size.y)
        )
        scene.windowHandler.saveViewport(viewportPos, scene.size.toIVector()) {

            val fullShaderComponents = components[ShaderType.FULL_SHADER] ?: emptyList()
            if (fullShaderComponents.isNotEmpty()) {
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

                    fullShaderComponents.forEach {
                        it.render(context)
                        GLStateMachine.depthTest.enable()
                    }
                }
            }


            val plain3dShaderComponents = components[ShaderType.PLAIN_3D_SHADER] ?: emptyList()
            if (plain3dShaderComponents.isNotEmpty()) {
                shaderHandler.useSingleColorShader {

                    matrixP = scene.getProjectionMatrix()
                    matrixV = scene.getViewMatrix()
                    matrixM = Matrix4.IDENTITY

                    plain3dShaderComponents.forEach {
                        it.render(context)
                    }
                }
            }

            val uvShaderComponents = components[ShaderType.UV_SHADER] ?: emptyList()
            if (uvShaderComponents.isNotEmpty()) {
                shaderHandler.useFixedViewportShader(scene.size.toIVector()) {
                    uvShaderComponents.forEach { it.render(context) }
                }
            }
        }
    }
}