package com.cout970.modeler.to_redo.newView.render.shader

import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.shader.UniformVariable
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.view.render.RenderContext
import org.lwjgl.opengl.GL20

/**
 * Created by cout970 on 2017/04/10.
 */
class UVShader(resourceLoader: ResourceLoader) : IShader {

    // uv shader
    private val uvShader: ShaderProgram

    private val uvUseColor: UniformVariable
    private val matrixMVP: UniformVariable

    init {
        uvShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceLoader.readResource("assets/shaders/uv_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceLoader.readResource("assets/shaders/uv_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_color")
            bindAttribute(2, "in_texture")
        }

        matrixMVP = uvShader.createUniformVariable("matrix")
        uvUseColor = uvShader.createUniformVariable("useColor")
    }

    override fun useShader(ctx: RenderContext, func: () -> Unit) {
        uvShader.start()
        matrixMVP.setMatrix4(ctx.scene.getMatrixMVP())
        uvUseColor.setBoolean(true)
        func()
        uvShader.stop()
    }

    fun enableColor(value: Boolean) {
        uvUseColor.setBoolean(value)
    }
}