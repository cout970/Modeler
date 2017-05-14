package com.cout970.modeler.view.newView.render.shader

import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.shader.UniformVariable
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.newView.render.RenderContext
import org.lwjgl.opengl.GL20

/**
 * Created by cout970 on 2017/04/10.
 */
class GuiShader(resourceLoader: ResourceLoader) : IShader {

    // plane shader
    private val guiShader: ShaderProgram
    // vertex shader variables
    private val viewport: UniformVariable

    init {
        guiShader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceLoader.readResource("assets/shaders/gui_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceLoader.readResource("assets/shaders/gui_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_texture")
        }

        viewport = guiShader.createUniformVariable("viewport")
    }

    override fun useShader(ctx: RenderContext, func: () -> Unit) {
        guiShader.start()
        viewport.setVector2(ctx.scene.size.toIVector())
        func()
        guiShader.stop()
    }
}