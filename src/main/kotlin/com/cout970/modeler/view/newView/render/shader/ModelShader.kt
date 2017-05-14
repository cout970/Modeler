package com.cout970.modeler.view.newView.render.shader

import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.shader.UniformVariable
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.view.newView.render.RenderContext
import org.lwjgl.opengl.GL20

/**
 * Created by cout970 on 2017/04/10.
 */
class ModelShader(resourceLoader: ResourceLoader) : IShader {

    // model shader
    private val modelShader: ShaderProgram
    // vertex shader variables
    private val matrixMVP: UniformVariable
    private val lightPositionA: UniformVariable
    private val lightPositionB: UniformVariable
    private val cameraPos: UniformVariable
    // fragment shader variables
    private val lightColorA: UniformVariable
    private val lightColorB: UniformVariable
    private val shineDamper: UniformVariable
    private val reflectivity: UniformVariable
    private val enableLight: UniformVariable


    init {
        modelShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceLoader.readResource("assets/shaders/model_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceLoader.readResource("assets/shaders/model_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_texture")
            bindAttribute(2, "in_normal")
        }

        matrixMVP = modelShader.createUniformVariable("matrixMVP")
        cameraPos = modelShader.createUniformVariable("cameraPos")
        lightPositionA = modelShader.createUniformVariable("lightPositionA")
        lightPositionB = modelShader.createUniformVariable("lightPositionB")
        lightColorA = modelShader.createUniformVariable("lightColorA")
        lightColorB = modelShader.createUniformVariable("lightColorB")
        shineDamper = modelShader.createUniformVariable("shineDamper")
        reflectivity = modelShader.createUniformVariable("reflectivity")
        enableLight = modelShader.createUniformVariable("enableLight")
    }

    override fun useShader(ctx: RenderContext, func: () -> Unit) {
        modelShader.start()

        matrixMVP.setMatrix4(ctx.scene.getMatrixMVP())
        cameraPos.setVector3(ctx.scene.cameraHandler.camera.position)

        val lights = ctx.scene.lights
        lightPositionA.setVector3(lights[0].pos)
        lightColorA.setVector3(lights[0].color)
        lightPositionB.setVector3(lights[1].pos)
        lightColorB.setVector3(lights[1].color)

        shineDamper.setFloat(1f)
        reflectivity.setFloat(0f)
        enableLight.setBoolean(true)
        func()
        modelShader.stop()
    }
}