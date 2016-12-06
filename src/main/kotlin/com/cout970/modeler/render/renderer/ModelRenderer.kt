package com.cout970.modeler.render.renderer

import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.shader.UniformVariable
import com.cout970.glutilities.tessellator.Tessellator
import com.cout970.glutilities.tessellator.VAO
import com.cout970.glutilities.tessellator.format.FormatPTN
import com.cout970.glutilities.texture.Texture
import com.cout970.glutilities.texture.TextureLoader
import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.mat4Of
import com.cout970.modeler.ResourceManager
import com.cout970.modeler.model.Model
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
            bindAttribute(0, "pos")
            bindAttribute(1, "tex")
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

    fun render(model: Model) {
        consumer.accept(cache.getOrCompute(model.hashCode() % 10) {
            tessellator.compile(GL11.GL_QUADS, FormatPTN()) {
                model.getComponents().forEach { component ->
                    component.getQuads().forEach { quad ->
                        val norm = quad.normal
                        quad.vertex.forEach { (pos, tex) ->
                            set(0, pos.x, pos.y, pos.z).set(1, tex.x, tex.y).set(2, norm.x, norm.y, norm.z).endVertex()
                        }
                    }
                }
            }
        })
    }

    fun stop() {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        shader.stop()
    }
}