package com.cout970.modeler.view.render

import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.shader.UniformVariable
import com.cout970.glutilities.tessellator.*
import com.cout970.glutilities.tessellator.format.FormatPT
import com.cout970.glutilities.texture.Texture
import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.model.material.MaterialNone
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import org.lwjgl.opengl.GL20
import java.util.function.Consumer

/**
 * Created by cout970 on 2016/12/03.
 */
class ShaderHandler(resourceLoader: ResourceLoader) {

    val tessellator = Tessellator()
    val consumer: Consumer<VAO>
    // vao formats
    val formatPC = FormatPC()
    val formatPTN = FormatPTN()
    val formatPT = FormatPT()
    val formatPCT = FormatPCT()

    // selection shader
    private val plainColorShader: ShaderProgram
    // vertex shader variables
    private val selProjectionMatrix: UniformVariable
    private val selViewMatrix: UniformVariable
    private val selTransformationMatrix: UniformVariable

    // model shader
    private val modelShader: ShaderProgram
    // vertex shader variables
    private val projectionMatrix: UniformVariable
    private val viewMatrix: UniformVariable
    private val transformationMatrix: UniformVariable
    private val lightPositionA: UniformVariable
    private val lightPositionB: UniformVariable
    // fragment shader variables
    private val lightColorA: UniformVariable
    private val lightColorB: UniformVariable
    private val shineDamper: UniformVariable
    private val reflectivity: UniformVariable
    private val enableLight: UniformVariable

    // plane shader
    private val planeShader: ShaderProgram
    // vertex shader variables
    private val viewport: UniformVariable

    val cursorTexture: Texture

    // uv shader
    private val uvShader: ShaderProgram

    private val uvUseColor: UniformVariable
    private val uvMatrix: UniformVariable

    init {
        modelShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceLoader.readResource("assets/shaders/scene_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceLoader.readResource("assets/shaders/scene_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_texture")
            bindAttribute(2, "in_normal")
        }
        plainColorShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceLoader.readResource("assets/shaders/plain_color_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceLoader.readResource("assets/shaders/plain_color_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_color")
        }
        planeShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceLoader.readResource("assets/shaders/plane_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceLoader.readResource("assets/shaders/plane_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_texture")
        }
        uvShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceLoader.readResource("assets/shaders/uv_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceLoader.readResource("assets/shaders/uv_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_color")
            bindAttribute(2, "in_texture")
        }

        uvMatrix = uvShader.createUniformVariable("matrix")
        uvUseColor = uvShader.createUniformVariable("useColor")

        viewport = planeShader.createUniformVariable("viewport")

        selProjectionMatrix = plainColorShader.createUniformVariable("projectionMatrix")
        selViewMatrix = plainColorShader.createUniformVariable("viewMatrix")
        selTransformationMatrix = plainColorShader.createUniformVariable("transformationMatrix")

        projectionMatrix = modelShader.createUniformVariable("projectionMatrix")
        viewMatrix = modelShader.createUniformVariable("viewMatrix")
        transformationMatrix = modelShader.createUniformVariable("transformationMatrix")
        lightPositionA = modelShader.createUniformVariable("lightPositionA")
        lightPositionB = modelShader.createUniformVariable("lightPositionB")
        lightColorA = modelShader.createUniformVariable("lightColorA")
        lightColorB = modelShader.createUniformVariable("lightColorB")
        shineDamper = modelShader.createUniformVariable("shineDamper")
        reflectivity = modelShader.createUniformVariable("reflectivity")
        enableLight = modelShader.createUniformVariable("enableLight")

        cursorTexture = resourceLoader.getTexture("assets/textures/cursor.png")

        consumer = Consumer<VAO> {
            it.bind()
            it.bindAttrib()
            it.draw()
            it.unbindAttrib()
            VAO.Companion.unbind()
        }

        MaterialNone.loadTexture(resourceLoader)
    }

    private var currentShader = -1


    fun useModelShader(lights: List<IVector3>, lightColors: List<IVector3>, shineDamper: Float, reflectivity: Float,
                       func: ShaderHandler.() -> Unit) {
        currentShader = 0
        modelShader.start()
        projectionMatrix.setMatrix4(matrixP)
        viewMatrix.setMatrix4(matrixV)
        transformationMatrix.setMatrix4(matrixM)

        lightPositionA.setVector3(lights[0])
        lightPositionB.setVector3(lights[1])
        lightColorA.setVector3(lightColors[0])
        lightColorB.setVector3(lightColors[1])
        this.shineDamper.setFloat(shineDamper)
        this.reflectivity.setFloat(reflectivity)
        enableLight.setBoolean(true)
        func()
        modelShader.stop()
        currentShader = -1
    }

    fun useSingleColorShader(func: ShaderHandler.() -> Unit) {
        currentShader = 1
        plainColorShader.start()
        func()
        plainColorShader.stop()
        currentShader = -1
    }

    fun useFixedViewportShader(size_: IVector2, func: ShaderHandler.() -> Unit) {
        currentShader = 2
        planeShader.start()
        viewport.setVector2(size_)
        func()
        planeShader.stop()
        currentShader = -1
    }

    fun useUVShader(matrix: IMatrix4, func: ShaderHandler.() -> Unit) {
        currentShader = 3
        uvShader.start()
        uvMatrix.setMatrix4(matrix)
        func()
        currentShader = -1
    }

    var enableColor: Boolean
        get() = false
        set(value) = uvUseColor.setBoolean(value)


    var matrixM: IMatrix4
        get() = Matrix4.IDENTITY
        set(value) {
            when (currentShader) {
                0 -> transformationMatrix.setMatrix4(value)
                1 -> selTransformationMatrix.setMatrix4(value)
            }
        }

    var matrixV: IMatrix4
        get() = Matrix4.IDENTITY
        set(value) {
            when (currentShader) {
                0 -> viewMatrix.setMatrix4(value)
                1 -> selViewMatrix.setMatrix4(value)
            }
        }

    var matrixP: IMatrix4
        get() = Matrix4.IDENTITY
        set(value) {
            when (currentShader) {
                0 -> projectionMatrix.setMatrix4(value)
                1 -> selProjectionMatrix.setMatrix4(value)
            }
        }

    object BufferSize {
        val value = 67108864 / 32 * 32
    }

    class FormatPC : IFormat {

        var bufferPos = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 3)
        var bufferCol = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 3)

        override fun getBuffers(): List<IBuffer> = listOf(bufferPos, bufferCol)

        override fun injectData(builder: VaoBuilder) {
            builder.bindAttribf(0, bufferPos.getBase().apply { flip() }, 3)
            builder.bindAttribf(1, bufferCol.getBase().apply { flip() }, 3)
        }

        override fun reset() {
            bufferPos.getBase().clear()
            bufferCol.getBase().clear()
        }
    }

    class FormatPCT : IFormat {

        var bufferPos = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 3)
        var bufferCol = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 3)
        var bufferTex = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 2)

        override fun getBuffers(): List<IBuffer> = listOf(bufferPos, bufferCol, bufferTex)

        override fun injectData(builder: VaoBuilder) {
            builder.bindAttribf(0, bufferPos.getBase().apply { flip() }, 3)
            builder.bindAttribf(1, bufferCol.getBase().apply { flip() }, 3)
            builder.bindAttribf(2, bufferTex.getBase().apply { flip() }, 2)
        }

        override fun reset() {
            bufferPos.getBase().clear()
            bufferCol.getBase().clear()
            bufferTex.getBase().clear()
        }
    }

    class FormatPTN : IFormat {

        var bufferPos = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 3)
        var bufferTex = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 2)
        var bufferNorm = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 3)

        override fun getBuffers(): List<IBuffer> = listOf(bufferPos, bufferTex, bufferNorm)

        override fun injectData(builder: VaoBuilder) {
            builder.bindAttribf(0, bufferPos.getBase().apply { flip() }, 3)
            builder.bindAttribf(1, bufferTex.getBase().apply { flip() }, 2)
            builder.bindAttribf(2, bufferNorm.getBase().apply { flip() }, 3)
        }

        override fun reset() {
            bufferPos.getBase().clear()
            bufferTex.getBase().clear()
            bufferNorm.getBase().clear()
        }
    }
}
