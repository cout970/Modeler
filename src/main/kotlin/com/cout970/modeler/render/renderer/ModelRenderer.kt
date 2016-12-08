package com.cout970.modeler.render.renderer

import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.shader.UniformVariable
import com.cout970.glutilities.tessellator.Tessellator
import com.cout970.glutilities.tessellator.VAO
import com.cout970.glutilities.tessellator.format.FormatPC
import com.cout970.glutilities.tessellator.format.FormatPTN
import com.cout970.glutilities.texture.Texture
import com.cout970.glutilities.texture.TextureLoader
import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.mat4Of
import com.cout970.modeler.ResourceManager
import com.cout970.modeler.model.Model
import com.cout970.modeler.modelcontrol.selection.*
import com.cout970.modeler.util.Cache
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
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

    //selection shader
    var selectionShader: ShaderProgram
    //vertex shader variables
    val selProjectionMatrix: UniformVariable
    val selViewMatrix: UniformVariable
    val selTransformationMatrix: UniformVariable

    //model shader
    var modelShader: ShaderProgram
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
        modelShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER, resourceManager.readResource("assets/shaders/model_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER, resourceManager.readResource("assets/shaders/model_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_texture")
            bindAttribute(2, "in_normal")
        }
        selectionShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER, resourceManager.readResource("assets/shaders/selection_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER, resourceManager.readResource("assets/shaders/selection_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_color")
        }

        selProjectionMatrix = selectionShader.createUniformVariable("projectionMatrix")
        selViewMatrix = selectionShader.createUniformVariable("viewMatrix")
        selTransformationMatrix = selectionShader.createUniformVariable("transformationMatrix")

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
        textureSize = modelShader.createUniformVariable("textureSize")

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
        modelShader.start()
        projectionMatrix.setMatrix4(matrixP)
        viewMatrix.setMatrix4(matrixV)
        transformationMatrix.setMatrix4(matrixM)
        lightPositionA.setVector3(vec3Of(150, 75, 100))
        lightPositionB.setVector3(vec3Of(-150, -75, -100))
        lightColorA.setVector3(vec3Of(1))
        lightColorB.setVector3(vec3Of(1))
        shineDamper.setFloat(1f)
        reflectivity.setFloat(0f)
        enableLight.setBoolean(true)
        textureSize.setVector2(vec2Of(1, 1))

        debugTexture.bind()

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, mode)
    }

    fun startSelection() {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        modelShader.stop()
        selectionShader.start()
        selProjectionMatrix.setMatrix4(matrixP)
        selViewMatrix.setMatrix4(matrixV)
        selTransformationMatrix.setMatrix4(matrixM)
    }

    fun stop() {
        selectionShader.stop()
    }

    fun render(model: Model) {
        consumer.accept(cache.getOrCompute(model.hashCode() % 17) {
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

        tessellator.draw(GL11.GL_LINES, FormatPTN(), consumer) {
            set(0, -10, 0, 0).set(1, 0, 0).set(2, 0, 1, 0).endVertex()
            set(0, 10, 0, 0).set(1, 0, 0).set(2, 0, 1, 0).endVertex()

            set(0, 0, -10, 0).set(1, 0, 0).set(2, 0, 1, 0).endVertex()
            set(0, 0, 10, 0).set(1, 0, 0).set(2, 0, 1, 0).endVertex()

            set(0, 0, 0, -10).set(1, 0, 0).set(2, 0, 1, 0).endVertex()
            set(0, 0, 0, 10).set(1, 0, 0).set(2, 0, 1, 0).endVertex()
        }
    }

    fun renderSelection(model: Model, selectionManager: SelectionManager) {
        if (selectionManager.selection == SelectionNone) {
            return
        } else if (selectionManager.selection.mode == SelectionMode.GROUP) {
            consumer.accept(cache.getOrCompute(model.hashCode() % 13) {
                tessellator.compile(GL11.GL_QUADS, FormatPC()) {
                    model.getGroups().forEach { group ->
                        if ((selectionManager.selection as SelectionGroup).isSelected(group)) {
                            group.components.forEach { component ->
                                component.getQuads().forEach { (a, b, c, d) ->
                                    renderBar(a.pos, b.pos)
                                    renderBar(b.pos, c.pos)
                                    renderBar(c.pos, d.pos)
                                    renderBar(d.pos, a.pos)
                                }
                            }
                        }
                    }
                }
            })
        } else if (selectionManager.selection.mode == SelectionMode.COMPONENT) {
            consumer.accept(cache.getOrCompute(model.hashCode() % 13) {
                tessellator.compile(GL11.GL_QUADS, FormatPC()) {
                    model.getComponents().forEach { component ->
                        if ((selectionManager.selection as SelectionComponent).isSelected(component)) {
                            component.getQuads().forEach { (a, b, c, d) ->
                                renderBar(a.pos, b.pos)
                                renderBar(b.pos, c.pos)
                                renderBar(c.pos, d.pos)
                                renderBar(d.pos, a.pos)
                            }
                        }
                    }
                }
            })
        } else if (selectionManager.selection.mode == SelectionMode.QUAD) {
            consumer.accept(cache.getOrCompute(model.hashCode() % 13) {
                tessellator.compile(GL11.GL_QUADS, FormatPC()) {
                    model.getComponents().forEach { component ->
                        component.getQuads().forEach({ quad ->
                            if ((selectionManager.selection as SelectionQuad).isSelected(quad)) {
                                renderBar(quad.a.pos, quad.b.pos)
                                renderBar(quad.b.pos, quad.c.pos)
                                renderBar(quad.c.pos, quad.d.pos)
                                renderBar(quad.d.pos, quad.a.pos)
                            }
                        })
                    }
                }
            })
        } else if (selectionManager.selection.mode == SelectionMode.VERTEX) {
            consumer.accept(cache.getOrCompute(model.hashCode() % 13) {
                tessellator.compile(GL11.GL_QUADS, FormatPC()) {
                    model.getComponents().forEach { component ->
                        component.getQuads().forEach({ quad ->
                            for (v in quad.vertex) {
                                if ((selectionManager.selection as SelectionVertex).isSelected(v)) {
                                    renderBar(v.pos, v.pos, 0.0625)
                                }
                            }
                        })
                    }
                }
            })
        }
    }

    fun renderBar(a: IVector3, b: IVector3, d: Double = 0.03125 / 2.0) {
        val col = vec3Of(1, 1, 0)
        tessellator.apply {
            //-x
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            //+x
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            //-y
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            //+y
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            //-z
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.min(Math.min(a.zd + d, a.zd - d), Math.min(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            //+z
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.min(Math.min(a.xd + d, a.xd - d), Math.min(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.min(Math.min(a.yd + d, a.yd - d), Math.min(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
            set(0, Math.max(Math.max(a.xd + d, a.xd - d), Math.max(b.xd - d, b.xd + d)), Math.max(Math.max(a.yd + d, a.yd - d), Math.max(b.yd - d, b.yd + d)), Math.max(Math.max(a.zd + d, a.zd - d), Math.max(b.zd - d, b.zd + d))).set(1, col.x, col.y, col.z).endVertex()
        }
    }
}