package com.cout970.modeler.view.render

import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.shader.UniformVariable
import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.*
import com.cout970.glutilities.tessellator.format.FormatPT
import com.cout970.glutilities.tessellator.format.FormatPTN
import com.cout970.glutilities.texture.Texture
import com.cout970.matrix.api.IMatrix4
import com.cout970.matrix.extensions.mat4Of
import com.cout970.matrix.extensions.times
import com.cout970.matrix.extensions.transpose
import com.cout970.modeler.ResourceManager
import com.cout970.modeler.config.Config
import com.cout970.modeler.model.MaterialNone
import com.cout970.modeler.model.Model
import com.cout970.modeler.modeleditor.selection.ModelPath
import com.cout970.modeler.modeleditor.selection.Selection
import com.cout970.modeler.modeleditor.selection.SelectionMode
import com.cout970.modeler.modeleditor.selection.SelectionNone
import com.cout970.modeler.util.Cache
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.view.controller.ModelSelector
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.modeler.view.scene.Camera
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
    //vao formats
    val formatPC = FormatPC()
    val formatPTN = FormatPTN()
    val formatPT = FormatPT()
    val formatPCT = FormatPCT()
    //Model, View, Projection matrices
    var matrixM: IMatrix4 = mat4Of(1)
    var matrixV: IMatrix4 = mat4Of(1)
    var matrixP: IMatrix4 = mat4Of(1)
    // valid values GL11.GL_FILL, GL11.GL_LINE, GL11.GL_POINT
    var mode = GL11.GL_FILL

    //selection shader
    val plainColorShader: ShaderProgram
    //vertex shader variables
    val selProjectionMatrix: UniformVariable
    val selViewMatrix: UniformVariable
    val selTransformationMatrix: UniformVariable

    //model shader
    val modelShader: ShaderProgram
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

    //plane shader
    val planeShader: ShaderProgram
    //vertex shader variables
    val viewport: UniformVariable

    val cursorTexture: Texture

    //uv shader
    val uvShader: ShaderProgram

    val uvUseColor: UniformVariable
    val uvMatrix: UniformVariable

    init {
        modelShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceManager.readResource("assets/shaders/scene_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceManager.readResource("assets/shaders/scene_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_texture")
            bindAttribute(2, "in_normal")
        }
        plainColorShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceManager.readResource("assets/shaders/plain_color_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceManager.readResource("assets/shaders/plain_color_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_color")
        }
        planeShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceManager.readResource("assets/shaders/plane_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceManager.readResource("assets/shaders/plane_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_texture")
        }
        uvShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceManager.readResource("assets/shaders/uv_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceManager.readResource("assets/shaders/uv_fragment.glsl").reader().readText())
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
        textureSize = modelShader.createUniformVariable("textureSize")

        cursorTexture = resourceManager.getTexture("assets/textures/cursor.png")

        consumer = Consumer<VAO> {
            it.bind()
            it.bindAttrib()
            it.draw()
            it.unbindAttrib()
            VAO.Companion.unbind()
        }

        MaterialNone.loadTexture(resourceManager)
    }

    fun setViewport(pos: IVector2, size: IVector2) {
        GL11.glViewport(pos.xi, pos.yi, size.xi, size.yi)
    }

    fun startModel() {
        modelShader.start()
        projectionMatrix.setMatrix4(matrixP)
        viewMatrix.setMatrix4(matrixV)
        transformationMatrix.setMatrix4(matrixM)
        lightPositionA.setVector3(vec3Of(500, 1000, 750))
        lightPositionB.setVector3(vec3Of(-500, -1000, -750))
        lightColorA.setVector3(vec3Of(1))
        lightColorB.setVector3(vec3Of(1))
        shineDamper.setFloat(1f)
        reflectivity.setFloat(0f)
        enableLight.setBoolean(true)
        textureSize.setVector2(vec2Of(1, 1))

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, mode)
    }

    fun startPlane(size: IVector2) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        planeShader.start()
        viewport.setVector2(size)
    }

    fun startSelection() {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        plainColorShader.start()
        selProjectionMatrix.setMatrix4(matrixP)
        selViewMatrix.setMatrix4(matrixV)
        selTransformationMatrix.setMatrix4(matrixM)
    }

    fun stop() {
        plainColorShader.stop()
    }

    fun renderModel(model: Model, modelCache: Cache<Int, VAO>) {
        for (group in model.groups) {
            group.material.bind()
            transformationMatrix.setMatrix4(group.transform.matrix.transpose())
            consumer.accept(modelCache.getOrCompute(model.hashCode()) {
                tessellator.compile(GL11.GL_QUADS, formatPTN) {
                    group.getQuads().forEach { quad ->
                        val norm = quad.normal
                        quad.vertex.forEach { (pos, tex) ->
                            set(0, pos.x, pos.y, pos.z).set(1, tex.xd, tex.yd).set(2, norm.x, norm.y,
                                    norm.z).endVertex()
                        }
                    }
                }
            })
        }
        transformationMatrix.setMatrix4(matrixM)
    }

    fun renderModelSelection(model: Model, selection: Selection, selectionCache: Cache<Int, VAO>) {
        if (selection == SelectionNone) {
            return
        }

        consumer.accept(selectionCache.getOrCompute(model.hashCode() xor selection.hashCode()) {
            val size = Config.selectionThickness.toDouble()
            tessellator.compile(GL11.GL_QUADS, formatPC) {
                if (selection.mode != SelectionMode.VERTEX) {
                    model.getQuadsOptimized(selection) { quad ->
                        RenderUtil.renderBar(tessellator, quad.a.pos, quad.b.pos, size)
                        RenderUtil.renderBar(tessellator, quad.b.pos, quad.c.pos, size)
                        RenderUtil.renderBar(tessellator, quad.c.pos, quad.d.pos, size)
                        RenderUtil.renderBar(tessellator, quad.d.pos, quad.a.pos, size)
                        if (selection.mode == SelectionMode.QUAD) {
                            quad.vertex.forEach { (pos, _) ->
                                set(0, pos.xd + 0.005, pos.yd + 0.005, pos.zd + 0.005).set(1, 0.5, 0.5, 0.4).endVertex()
                            }
                            quad.vertex.forEach { (pos, _) ->
                                set(0, pos.xd - 0.005, pos.yd - 0.005, pos.zd - 0.005).set(1, 0.5, 0.5, 0.4).endVertex()
                            }
                        }
                    }
                } else {
                    model.getPaths(ModelPath.Level.MESH).forEach { compPath ->
                        val paths = selection.paths.filter { it.compareLevel(compPath, ModelPath.Level.MESH) }
                        if (paths.isNotEmpty()) {
                            val matrix = compPath.getMeshMatrix(model)
                            paths.map { it.getVertex(model)!! }.map { matrix * it.toVector4(1.0) }.forEach {
                                RenderUtil.renderBar(tessellator, it, it, size)
                            }
                        }
                    }

                }
            }
        })
    }

    fun renderRotation(center: IVector3, selector: ModelSelector, selection: Selection, camera: Camera) {

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        val controller = selector.controller
        val selX = controller.selectedAxis == SelectionAxis.X || controller.hoveredAxis == SelectionAxis.X
        val selY = controller.selectedAxis == SelectionAxis.Y || controller.hoveredAxis == SelectionAxis.Y
        val selZ = controller.selectedAxis == SelectionAxis.Z || controller.hoveredAxis == SelectionAxis.Z

        tessellator.draw(GL11.GL_QUADS, formatPC, consumer) {
            val (scale, radius, size) = selector.getArrowProperties(camera.zoom)

            if (selection.mode != SelectionMode.VERTEX) {
                RenderUtil.renderBar(tessellator, center, center, size * 1.5, vec3Of(1, 1, 1))
            }

            //if one of the axis is selected
            if (controller.selectedAxis != SelectionAxis.NONE) {

                val axis = controller.selectedAxis
                RenderUtil.renderCircle(tessellator, center, axis,
                        radius, Config.cursorLinesSize * scale * 0.03125, axis.axis)

            } else {
                for (axis in SelectionAxis.selectedValues) {
                    RenderUtil.renderCircle(tessellator, center, axis,
                            radius, Config.cursorLinesSize * scale * 0.03125, axis.axis)
                }

                RenderUtil.renderBar(tessellator, center + vec3Of(radius, 0, -0.2 * scale),
                        center + vec3Of(radius, 0, 0.2 * scale), if (selX) size * 1.5 else size, color = vec3Of(1))

                RenderUtil.renderBar(tessellator, center + vec3Of(-0.2 * scale, radius, 0),
                        center + vec3Of(0.2 * scale, radius, 0), if (selY) size * 1.5 else size, color = vec3Of(1))

                RenderUtil.renderBar(tessellator, center + vec3Of(0, -0.2 * scale, radius),
                        center + vec3Of(0, 0.2 * scale, radius), if (selZ) size * 1.5 else size, color = vec3Of(1))
            }
        }
    }

    fun renderTranslation(center: IVector3, selector: ModelSelector, selection: Selection, camera: Camera) {

        val controller = selector.controller
        val selX = controller.selectedAxis == SelectionAxis.X || controller.hoveredAxis == SelectionAxis.X
        val selY = controller.selectedAxis == SelectionAxis.Y || controller.hoveredAxis == SelectionAxis.Y
        val selZ = controller.selectedAxis == SelectionAxis.Z || controller.hoveredAxis == SelectionAxis.Z

        if (Config.enableHelperGrid && selector.scene.perspective && controller.selectedAxis != SelectionAxis.NONE) {
            tessellator.draw(GL11.GL_LINES, formatPC, consumer) {
                val grey = vec3Of(0.5)
                val red = vec3Of(1, 0, 0)
                var col: IVector3
                if (selX || selZ) {
                    for (x in -160..160) {
                        col = if (x % 16 == 0) red else grey
                        set(0, x, center.y, -160).set(1, col.x, col.y, col.z).endVertex()
                        set(0, x, center.y, 160).set(1, col.x, col.y, col.z).endVertex()
                    }
                    for (z in -160..160) {
                        col = if (z % 16 == 0) red else grey
                        set(0, -160, center.y, z).set(1, col.x, col.y, col.z).endVertex()
                        set(0, 160, center.y, z).set(1, col.x, col.y, col.z).endVertex()
                    }
                } else if (selY) {
                    for (z in -160..160) {
                        col = if (z % 16 == 0) red else grey
                        set(0, -160, z, center.z).set(1, col.x, col.y, col.z).endVertex()
                        set(0, 160, z, center.z).set(1, col.x, col.y, col.z).endVertex()
                    }
                    for (x in -160..160) {
                        col = if (x % 16 == 0) red else grey
                        set(0, x, -160, center.z).set(1, col.x, col.y, col.z).endVertex()
                        set(0, x, 160, center.z).set(1, col.x, col.y, col.z).endVertex()
                    }
                }
            }
        }
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT)
        tessellator.draw(GL11.GL_QUADS, formatPC, consumer) {

            val (scale, radius, size) = selector.getArrowProperties(camera.zoom)
            val start = radius - 0.2 * scale
            val end = radius + 0.2 * scale

            if (selection.mode != SelectionMode.VERTEX) {
                RenderUtil.renderBar(tessellator, center, center, size * 1.5, vec3Of(1, 1, 1))
            }

            RenderUtil.renderBar(tessellator, center + vec3Of(start, 0, 0), center + vec3Of(end, 0, 0),
                    if (selX) size * 1.5 else size, color = vec3Of(1, 0, 0))
            RenderUtil.renderBar(tessellator, center + vec3Of(0, start, 0), center + vec3Of(0, end, 0),
                    if (selY) size * 1.5 else size, color = vec3Of(0, 1, 0))
            RenderUtil.renderBar(tessellator, center + vec3Of(0, 0, start), center + vec3Of(0, 0, end),
                    if (selZ) size * 1.5 else size, color = vec3Of(0, 0, 1))
        }
    }

    fun renderCursor() {
        val size = vec2Of(100)
        GLStateMachine.depthTest.disable()
        GLStateMachine.blend.enable()
        cursorTexture.bind()
        tessellator.draw(GL11.GL_QUADS, formatPT, consumer) {
            set(0, -size.xd / 2, -size.yd / 2, 0.0).set(1, 0, 0).endVertex()
            set(0, -size.xd / 2, +size.yd / 2, 0.0).set(1, 1, 0).endVertex()
            set(0, +size.xd / 2, +size.yd / 2, 0.0).set(1, 1, 1).endVertex()
            set(0, +size.xd / 2, -size.yd / 2, 0.0).set(1, 0, 1).endVertex()
        }
        GLStateMachine.blend.disable()
        GLStateMachine.depthTest.enable()
    }

    fun renderExtras() {
        tessellator.draw(GL11.GL_LINES, formatPC, consumer) {
            set(0, -10, 0, 0).set(1, 1, 0, 0).endVertex()
            set(0, 10, 0, 0).set(1, 1, 0, 0).endVertex()

            set(0, 0, -10, 0).set(1, 0, 1, 0).endVertex()
            set(0, 0, 10, 0).set(1, 0, 1, 0).endVertex()

            set(0, 0, 0, -10).set(1, 0, 0, 1).endVertex()
            set(0, 0, 0, 10).set(1, 0, 0, 1).endVertex()

            val dist = -1024 * 15

            //y
            for (x in -160..160) {
                set(0, x, dist, -160).set(1, 0.5, 0.5, 0.5).endVertex()
                set(0, x, dist, 160).set(1, 0.5, 0.5, 0.5).endVertex()
            }
            for (z in -160..160) {
                set(0, -160, dist, z).set(1, 0.5, 0.5, 0.5).endVertex()
                set(0, 160, dist, z).set(1, 0.5, 0.5, 0.5).endVertex()
            }

            //x
            for (x in -160..160) {
                set(0, dist, x, -160).set(1, 0.5, 0.5, 0.5).endVertex()
                set(0, dist, x, 160).set(1, 0.5, 0.5, 0.5).endVertex()
            }
            for (z in -160..160) {
                set(0, dist, -160, z).set(1, 0.5, 0.5, 0.5).endVertex()
                set(0, dist, 160, z).set(1, 0.5, 0.5, 0.5).endVertex()
            }

            //z
            for (z in -160..160) {
                set(0, -160, z, dist).set(1, 0.5, 0.5, 0.5).endVertex()
                set(0, 160, z, dist).set(1, 0.5, 0.5, 0.5).endVertex()
            }
            for (x in -160..160) {
                set(0, x, -160, dist).set(1, 0.5, 0.5, 0.5).endVertex()
                set(0, x, 160, dist).set(1, 0.5, 0.5, 0.5).endVertex()
            }

            for (x in -7..8) {
                set(0, x * 16, 0, -7 * 16).set(1, 1.0, 0.0, 0.0).endVertex()
                set(0, x * 16, 0, 8 * 16).set(1, 1.0, 0.0, 0.0).endVertex()
            }

            for (z in -7..8) {
                set(0, -7 * 16, 0, z * 16).set(1, 1.0, 0.0, 0.0).endVertex()
                set(0, 8 * 16, 0, z * 16).set(1, 1.0, 0.0, 0.0).endVertex()
            }
        }
    }

    fun renderUV(model: Model, selection: Selection) {
        val texture = model.groups.find { it.material != MaterialNone }?.material ?: MaterialNone
        val scale = 16.0
        val divs = 128

        GLStateMachine.depthTest.disable()
        uvShader.start()
        uvMatrix.setMatrix4(matrixP * matrixV)

        uvUseColor.setBoolean(true)
        MaterialNone.bind()

        tessellator.draw(GL11.GL_LINES, formatPCT, consumer) {
            for (x in 0..divs) {
                set(0, x * (scale / divs), 0, 0).set(1, 0.5, 0.5, 0.5).set(2, 0.0, 0.0).endVertex()
                set(0, x * (scale / divs), scale, 0).set(1, 0.5, 0.5, 0.5).set(2, 0.0, 0.0).endVertex()
            }

            for (z in 0..divs) {
                set(0, 0, z * (scale / divs), 0).set(1, 0.5, 0.5, 0.5).set(2, 0.0, 0.0).endVertex()
                set(0, scale, z * (scale / divs), 0).set(1, 0.5, 0.5, 0.5).set(2, 0.0, 0.0).endVertex()
            }
        }

        uvUseColor.setBoolean(false)

        texture.bind()
        tessellator.draw(GL11.GL_QUADS, formatPCT, consumer) {
            set(0, 0, 0, 0).set(1, 1, 1, 1).set(2, 0.0, 1.0).endVertex()
            set(0, scale, 0, 0).set(1, 1, 1, 1).set(2, 1.0, 1.0).endVertex()
            set(0, scale, scale, 0).set(1, 1, 1, 1).set(2, 1.0, 0.0).endVertex()
            set(0, 0, scale, 0).set(1, 1, 1, 1).set(2, 0.0, 0.0).endVertex()
        }

        uvUseColor.setBoolean(true)
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
        GL11.glLineWidth(2f)
        tessellator.draw(GL11.GL_QUADS, formatPCT, consumer) {
            selection.paths.forEach { path ->
                when (path.level) {
                    ModelPath.Level.GROUPS -> {
                        path.getSubPaths(model).forEach { meshPath ->
                            meshPath.getSubPaths(model).forEach { quadPath ->
                                val quad = quadPath.getQuad(model)!!
                                quad.vertex
                                        .map { it.copy(tex = vec2Of(it.tex.x, 1 - it.tex.yd)) }
                                        .map { it.tex * scale }
                                        .forEach { set(0, it.x, it.yd, 0).set(1, 1, 0, 0).set(2, 0.0, 0.0).endVertex() }
                            }
                        }
                    }
                    ModelPath.Level.MESH -> {
                        path.getSubPaths(model).forEach { quadPath ->
                            val quad = quadPath.getQuad(model)!!
                            quad.vertex
                                    .map { it.copy(tex = vec2Of(it.tex.x, 1 - it.tex.yd)) }
                                    .map { it.tex * scale }
                                    .forEach { set(0, it.x, it.yd, 0).set(1, 1, 0, 0).set(2, 0.0, 0.0).endVertex() }
                        }
                    }
                    ModelPath.Level.QUADS -> {
                        val quad = path.getQuad(model)!!
                        quad.vertex
                                .map { it.copy(tex = vec2Of(it.tex.x, 1 - it.tex.yd)) }
                                .map { it.tex * scale }
                                .forEach { set(0, it.x, it.yd, 0).set(1, 1, 0, 0).set(2, 0.0, 0.0).endVertex() }
                    }
                    else -> {
                    }
                }
            }
        }
        GL11.glLineWidth(1f)
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        GLStateMachine.depthTest.enable()
    }

    class FormatPC : IFormat {

        var bufferPos = Buffer(IBuffer.BufferType.FLOAT, 524288 * 16, 3)
        var bufferCol = Buffer(IBuffer.BufferType.FLOAT, 524288 * 16, 3)

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

        var bufferPos = Buffer(IBuffer.BufferType.FLOAT, 524288 * 16, 3)
        var bufferCol = Buffer(IBuffer.BufferType.FLOAT, 524288 * 16, 3)
        var bufferTex = Buffer(IBuffer.BufferType.FLOAT, 524288 * 16, 2)

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
}
