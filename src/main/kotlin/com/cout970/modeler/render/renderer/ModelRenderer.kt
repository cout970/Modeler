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
import com.cout970.modeler.modelcontrol.selection.ModelPath
import com.cout970.modeler.modelcontrol.selection.Selection
import com.cout970.modeler.modelcontrol.selection.SelectionMode
import com.cout970.modeler.modelcontrol.selection.SelectionNone
import com.cout970.modeler.render.controller.ModelSelector
import com.cout970.modeler.render.controller.SelectionAxis
import com.cout970.modeler.util.Cache
import com.cout970.modeler.util.RenderUtil
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
    //cache
    val modelCache = Cache<Int, VAO>(1).apply { onRemove = { k, v -> v.close() } }
    val selectionCache = Cache<Int, VAO>(1).apply { onRemove = { k, v -> v.close() } }
    //vao formats
    val formatPC = FormatPC()
    val formatPTN = FormatPTN()
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
            compile(GL20.GL_VERTEX_SHADER, resourceManager.readResource("assets/shaders/scene_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER, resourceManager.readResource("assets/shaders/scene_fragment.glsl").reader().readText())
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

    fun renderModel(model: Model) {
        consumer.accept(modelCache.getOrCompute(model.hashCode()) {
            tessellator.compile(GL11.GL_QUADS, formatPTN) {
                model.quads.forEach { quad ->
                    val norm = quad.normal
                    quad.vertex.forEach { (pos, tex) ->
                        set(0, pos.x, pos.y, pos.z).set(1, tex.x, tex.y).set(2, norm.x, norm.y, norm.z).endVertex()
                    }
                }
            }
        })
    }

    fun renderModelSelection(model: Model, selection: Selection) {
        if (selection == SelectionNone) {
            return
        }

        consumer.accept(selectionCache.getOrCompute(model.hashCode() xor selection.hashCode()) {
            tessellator.compile(GL11.GL_QUADS, formatPC) {
                if (selection.mode != SelectionMode.VERTEX) {
                    model.getQuadsOptimized(selection) { quad ->
                        RenderUtil.renderBar(tessellator, quad.a.pos, quad.b.pos)
                        RenderUtil.renderBar(tessellator, quad.b.pos, quad.c.pos)
                        RenderUtil.renderBar(tessellator, quad.c.pos, quad.d.pos)
                        RenderUtil.renderBar(tessellator, quad.d.pos, quad.a.pos)
                        if (selection.mode == SelectionMode.QUAD) {
                            quad.vertex.forEach { (pos, tex) ->
                                val pos_: IVector3 = pos
                                set(0, pos_.xd + 0.01, pos_.yd + 0.01, pos_.zd + 0.01).set(1, 0.5, 0.5, 0.4).endVertex()
                            }
                            quad.vertex.forEach { (pos, tex) ->
                                val pos_: IVector3 = pos
                                set(0, pos_.xd - 0.01, pos_.yd - 0.01, pos_.zd - 0.01).set(1, 0.5, 0.5, 0.4).endVertex()
                            }
                        }
                    }
                } else {
                    model.getPaths(ModelPath.Level.COMPONENTS).forEach { compPath ->
                        val paths = selection.paths.filter { it.compareLevel(compPath, ModelPath.Level.COMPONENTS) }
                        if (paths.isNotEmpty()) {
                            val matrix = compPath.getComponentMatrix(model)
                            paths.map { it.getVertex(model)!! }.map { it.transform(matrix) }.forEach {
                                RenderUtil.renderBar(tessellator, it.pos, it.pos, 0.0625)
                            }
                        }
                    }

                }
            }
        })
    }

    fun renderTranslation(center: IVector3, selector: ModelSelector, selection: Selection) {

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        val selX = selector.selectedAxis == SelectionAxis.X || selector.phantomSelectedAxis == SelectionAxis.X
        val selY = selector.selectedAxis == SelectionAxis.Y || selector.phantomSelectedAxis == SelectionAxis.Y
        val selZ = selector.selectedAxis == SelectionAxis.Z || selector.phantomSelectedAxis == SelectionAxis.Z
        val size = 0.0625 / 2

        tessellator.draw(GL11.GL_QUADS, formatPC, consumer) {
            if (selection.mode != SelectionMode.VERTEX) {
                RenderUtil.renderBar(tessellator, center, center, 0.0625, vec3Of(1, 1, 1))
            }

            RenderUtil.renderBar(tessellator, center + vec3Of(0.8, 0, 0), center + vec3Of(1, 0, 0), if (selX) size * 1.5 else size, col = vec3Of(1, 0, 0))

            RenderUtil.renderBar(tessellator, center + vec3Of(0, 0.8, 0), center + vec3Of(0, 1, 0), if (selY) size * 1.5 else size, col = vec3Of(0, 1, 0))

            RenderUtil.renderBar(tessellator, center + vec3Of(0, 0, 0.8), center + vec3Of(0, 0, 1), if (selZ) size * 1.5 else size, col = vec3Of(0, 0, 1))
        }
    }

    fun renderExtras() {
        tessellator.draw(GL11.GL_LINES, formatPC, consumer) {
            set(0, -10, 0, 0).set(1, 1, 0, 0).endVertex()
            set(0, 10, 0, 0).set(1, 1, 0, 0).endVertex()

            set(0, 0, -10, 0).set(1, 0, 1, 0).endVertex()
            set(0, 0, 10, 0).set(1, 0, 1, 0).endVertex()

            set(0, 0, 0, -10).set(1, 0, 0, 1).endVertex()
            set(0, 0, 0, 10).set(1, 0, 0, 1).endVertex()
        }
    }
}
