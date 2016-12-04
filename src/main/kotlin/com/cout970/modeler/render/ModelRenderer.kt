package com.cout970.modeler.render

import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.shader.UniformVariable
import com.cout970.glutilities.tessellator.Tessellator
import com.cout970.glutilities.tessellator.VAO
import com.cout970.glutilities.tessellator.format.FormatPT
import com.cout970.matrix.extensions.mutableMat4Of
import com.cout970.matrix.extensions.translate
import com.cout970.modeler.ModelController
import com.cout970.modeler.ResourceManager
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec3Of
import com.cout970.vector.extensions.xi
import com.cout970.vector.extensions.yi
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.util.function.Consumer

/**
 * Created by cout970 on 2016/12/03.
 */
class ModelRenderer(
        resourceManager: ResourceManager
) {
    var consumer: Consumer<VAO>
    var shader: ShaderProgram
    val tessellator = Tessellator()
    val color: UniformVariable
    val panelSize: UniformVariable
    val matrixMVP: UniformVariable

    init {
        shader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER, resourceManager.readResource("shaders/model_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER, resourceManager.readResource("shaders/model_fragment.glsl").reader().readText())
            bindAttribute(0, "pos")
            bindAttribute(1, "tex")
        }

        color = shader.createUniformVariable("color")
        panelSize = shader.createUniformVariable("panelSize")
        matrixMVP = shader.createUniformVariable("matrixMVP")

        consumer = Consumer<VAO> {
            it.bind()
            it.bindAttrib()
            it.draw()
            it.unbindAttrib()
            VAO.unbind()
        }
    }

    fun render(modelController: ModelController, pos: IVector2, size: IVector2) {
        GL11.glViewport(pos.xi, pos.yi, size.xi, size.yi)
        shader.start()
        color.setVector3(vec3Of(1, 0, 0))
        panelSize.setVector2(size)
        matrixMVP.setMatrix4(mutableMat4Of(1).apply {
            println(this)
            translate(vec3Of(0, 0, 0))
            println(this)
        })
//        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)

        tessellator.draw(GL11.GL_QUADS, FormatPT(), consumer) {
            modelController.model.getComponents().forEach {
                it.getQuads()
                        .flatMap { it.vertex }
                        .forEach { set(0, it.pos.x, it.pos.y, it.pos.z).set(1, it.tex.x, it.tex.y).endVertex() }
            }
        }

        tessellator.draw(GL11.GL_LINES, FormatPT(), consumer) {
            set(0, 0, -1, 0).set(1, 0, 1).endVertex()
            set(0, 0, 1, 0).set(1, 0, 1).endVertex()
        }
        tessellator.draw(GL11.GL_LINES, FormatPT(), consumer) {
            set(0, -1, 0, 0).set(1, 1, 0).endVertex()
            set(0, 1, 0, 0).set(1, 1, 0).endVertex()
        }
//        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
        shader.stop()
    }
}