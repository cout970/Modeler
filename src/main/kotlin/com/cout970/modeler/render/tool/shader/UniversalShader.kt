package com.cout970.modeler.render.tool.shader

import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.shader.UniformVariable
import com.cout970.glutilities.tessellator.VAO
import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.Debugger
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.render.tool.RenderContext
import com.cout970.vector.extensions.Vector3
import org.lwjgl.opengl.GL20
import java.util.function.Consumer

/**
 * Created by cout970 on 2017/05/25.
 */

class UniversalShader(resourceLoader: ResourceLoader) : Consumer<VAO> {

    private val program: ShaderProgram

    val matrixVP: UniformVariable
    val matrixM: UniformVariable
    val cameraPos: UniformVariable
    val lightPos: UniformVariable.UniformVariableArray
    val lightCount: UniformVariable
    val useLight: UniformVariable
    val showHiddenFaces: UniformVariable
    val textureSampler: UniformVariable
    val useTexture: UniformVariable
    val useColor: UniformVariable
    val lightColor: UniformVariable.UniformVariableArray
    val shineDamper: UniformVariable
    val reflectivity: UniformVariable
    val ambient: UniformVariable
    val globalColor: UniformVariable

    init {
        program = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceLoader.readResource("assets/shaders/universal_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceLoader.readResource("assets/shaders/universal_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_texture")
            bindAttribute(2, "in_normal")
            bindAttribute(3, "in_color")
        }
        matrixVP = program.createUniformVariable("matrixVP")
        matrixM = program.createUniformVariable("matrixM")
        cameraPos = program.createUniformVariable("cameraPos")
        lightPos = program.createUniformVariableArray("lightPos")
        lightCount = program.createUniformVariable("lightCount")
        useLight = program.createUniformVariable("useLight")
        showHiddenFaces = program.createUniformVariable("showHiddenFaces")
        textureSampler = program.createUniformVariable("textureSampler")
        useTexture = program.createUniformVariable("useTexture")
        useColor = program.createUniformVariable("useColor")
        lightColor = program.createUniformVariableArray("lightColor")
        shineDamper = program.createUniformVariable("shineDamper")
        reflectivity = program.createUniformVariable("reflectivity")
        ambient = program.createUniformVariable("ambient")
        globalColor = program.createUniformVariable("globalColor")
    }

    fun useShader(ctx: RenderContext, func: () -> Unit) {
        program.start()
        matrixVP.setMatrix4(ctx.camera.getMatrix(ctx.viewport))
        matrixM.setMatrix4(Matrix4.IDENTITY)
        cameraPos.setVector3(ctx.camera.position)

        lightCount.setInt(ctx.lights.size)
        ctx.lights.forEachIndexed { index, (pos, color) ->
            lightPos.setVector3(index, pos)
            lightColor.setVector3(index, color)
        }
        useTexture.setInt(0)
        useColor.setInt(1)
        useLight.setInt(0)
        globalColor.setVector3(Vector3.ONE)
        func()
        program.stop()
    }

    fun render(vao: VAO, transform: IMatrix4, vararg flags: ShaderFlag) {
        useColor.setBoolean(ShaderFlag.COLOR in flags)
        useLight.setBoolean(ShaderFlag.LIGHT in flags)
        useTexture.setBoolean(ShaderFlag.TEXTURE in flags)
        matrixM.setMatrix4(transform)
        accept(vao)
    }

    override fun accept(it: VAO) {
        it.bind()
        it.bindAttrib()
        Debugger.drawVboCount += it.vboCount
        Debugger.drawRegionsCount += it.regions.size
        Debugger.drawVaoCount += 1
        it.draw()
        it.unbindAttrib()
        VAO.unbind()
    }
}

enum class ShaderFlag { COLOR, LIGHT, TEXTURE }