package com.cout970.modeler.render.renderer

import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.shader.UniformVariable
import com.cout970.glutilities.tessellator.*
import com.cout970.glutilities.texture.Texture
import com.cout970.glutilities.texture.TextureLoader
import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.mat4Of
import com.cout970.modeler.ResourceManager
import com.cout970.modeler.model.Model
import com.cout970.modeler.modelcontrol.SelectionComponent
import com.cout970.modeler.modelcontrol.SelectionManager
import com.cout970.modeler.modelcontrol.SelectionMode
import com.cout970.modeler.util.Cache
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import com.cout970.vector.extensions.xi
import com.cout970.vector.extensions.yi
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.util.function.Consumer

/**
 * Created by cout970 on 2016/12/03.
 */
class ModelRenderer(resourceManager: ResourceManager) {

    val tessellator = Tessellator()
    var consumer: Consumer<VAO>
    val cache = Cache<Int, VAO>(10).apply { onRemove = { k, v -> v.close() } }
    //Model, View, Projection matrices
    var matrixM: IMatrix4 = mat4Of(1)
    var matrixV: IMatrix4 = mat4Of(1)
    var matrixP: IMatrix4 = mat4Of(1)
    // valid values GL11.GL_FILL, GL11.GL_LINE, GL11.GL_POINT
    var mode = GL11.GL_FILL

    //shader
    var shader: ShaderProgram
    //vertex shader variables
    val projectionMatrix: UniformVariable
    val viewMatrix: UniformVariable
    val transformationMatrix: UniformVariable
    val lightPositionA: UniformVariable
    val lightPositionB: UniformVariable
    //fragment shader variables
    val lightColorA: UniformVariable
    val lightColorB: UniformVariable
    val shineDamper: UniformVariable
    val reflectivity: UniformVariable
    val enableLight: UniformVariable
    val textureSize: UniformVariable

    val debugTexture: Texture

    init {
        shader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER, resourceManager.readResource("assets/shaders/model_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER, resourceManager.readResource("assets/shaders/model_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_texture")
            bindAttribute(2, "in_normal")
            bindAttribute(3, "in_selected")
        }
        projectionMatrix = shader.createUniformVariable("projectionMatrix")
        viewMatrix = shader.createUniformVariable("viewMatrix")
        transformationMatrix = shader.createUniformVariable("transformationMatrix")
        lightPositionA = shader.createUniformVariable("lightPositionA")
        lightPositionB = shader.createUniformVariable("lightPositionB")
        lightColorA = shader.createUniformVariable("lightColorA")
        lightColorB = shader.createUniformVariable("lightColorB")
        shineDamper = shader.createUniformVariable("shineDamper")
        reflectivity = shader.createUniformVariable("reflectivity")
        enableLight = shader.createUniformVariable("enableLight")
        textureSize = shader.createUniformVariable("textureSize")

        val aux = TextureLoader.loadTexture(resourceManager.readResource("assets/textures/debug.png"))
        debugTexture = TextureLoader.uploadTexture2D(aux)

        consumer = Consumer<VAO> {
            it.bind()
            it.bindAttrib()
            it.draw()
            it.unbindAttrib()
            VAO.unbind()
        }
    }

    fun start(pos: IVector2, size: IVector2) {
        GL11.glViewport(pos.xi, pos.yi, size.xi, size.yi)
        shader.start()
        projectionMatrix.setMatrix4(matrixP)
        viewMatrix.setMatrix4(matrixV)
        transformationMatrix.setMatrix4(matrixM)
        lightPositionA.setVector3(vec3Of(100, 75, 100))
        lightPositionB.setVector3(vec3Of(-100, -75, -100))
        lightColorA.setVector3(vec3Of(1))
        lightColorB.setVector3(vec3Of(1))
        shineDamper.setFloat(1f)
        reflectivity.setFloat(0f)
        enableLight.setBoolean(true)
        textureSize.setVector2(vec2Of(1, 1))

        debugTexture.bind()

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, mode)
    }

    fun render(model: Model, selectionManager: SelectionManager) {
        consumer.accept(cache.getOrCompute(model.hashCode() % 10) {
            tessellator.compile(GL11.GL_QUADS, Format()) {
                if (selectionManager.selection.mode == SelectionMode.COMPONENT) {
                    model.getComponents().forEach { component ->
                        val s = if ((selectionManager.selection as SelectionComponent).isSelected(component)) 1 else 0
                        component.getQuads().forEach { quad ->
                            val norm = quad.normal
                            quad.vertex.forEach { (pos, tex) ->
                                set(0, pos.x, pos.y, pos.z).set(1, tex.x, tex.y).set(2, norm.x, norm.y, norm.z).set(3, s).endVertex()
                            }
                        }
                    }
                } else {
                    model.getComponents().forEach { component ->
                        component.getQuads().forEach { quad ->
                            val norm = quad.normal
                            quad.vertex.forEach { (pos, tex) ->
                                set(0, pos.x, pos.y, pos.z).set(1, tex.x, tex.y).set(2, norm.x, norm.y, norm.z).set(3, 0).endVertex()
                            }
                        }
                    }
                }
            }
        })
        tessellator.draw(GL11.GL_LINES, Format(), consumer) {
            set(0, -10, 0, 0).set(1, 0, 0).set(2, 0, 1, 0).set(3, 0).endVertex()
            set(0, 10, 0, 0).set(1, 0, 0).set(2, 0, 1, 0).set(3, 0).endVertex()

            set(0, 0, -10, 0).set(1, 0, 0).set(2, 0, 1, 0).set(3, 0).endVertex()
            set(0, 0, 10, 0).set(1, 0, 0).set(2, 0, 1, 0).set(3, 0).endVertex()

            set(0, 0, 0, -10).set(1, 0, 0).set(2, 0, 1, 0).set(3, 0).endVertex()
            set(0, 0, 0, 10).set(1, 0, 0).set(2, 0, 1, 0).set(3, 0).endVertex()
        }
    }

    fun stop() {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        shader.stop()
    }

    class Format : IFormat {

        var bufferPos = Buffer(IBuffer.BufferType.FLOAT, 65536, 3)
        var bufferTex = Buffer(IBuffer.BufferType.FLOAT, 65536, 2)
        var bufferNorm = Buffer(IBuffer.BufferType.FLOAT, 65536, 3)
        var bufferSel = Buffer(IBuffer.BufferType.FLOAT, 65536, 1)

        override fun getBuffers(): List<IBuffer> = listOf(bufferPos, bufferTex, bufferNorm, bufferSel)

        override fun injectData(builder: VaoBuilder) {
            builder.bindAttribf(0, bufferPos.getBase().apply { flip() }, 3)
            builder.bindAttribf(1, bufferTex.getBase().apply { flip() }, 2)
            builder.bindAttribf(2, bufferNorm.getBase().apply { flip() }, 3)
            builder.bindAttribf(3, bufferSel.getBase().apply { flip() }, 1)
        }

        override fun reset() {
            bufferPos.getBase().clear()
            bufferTex.getBase().clear()
            bufferNorm.getBase().clear()
            bufferSel.getBase().clear()
        }
    }
}