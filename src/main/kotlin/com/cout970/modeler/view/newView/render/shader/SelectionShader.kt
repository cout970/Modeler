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
class SelectionShader(resourceLoader: ResourceLoader) : IShader {

    // selection shader
    private val selectionShader: ShaderProgram
    // vertex shader variables
    private val matrixMVP: UniformVariable

    init {
        selectionShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceLoader.readResource("assets/shaders/selection_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceLoader.readResource("assets/shaders/selection_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_color")
        }

        matrixMVP = selectionShader.createUniformVariable("matrixMVP")
    }

    override fun useShader(ctx: RenderContext, func: () -> Unit) {
        selectionShader.start()
        matrixMVP.setMatrix4(ctx.scene.getMatrixMVP())
        func()
        selectionShader.stop()
    }
}