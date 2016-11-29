package com.cout970.modeler

import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.Tessellator
import com.cout970.glutilities.tessellator.VAO
import com.cout970.glutilities.tessellator.format.FormatPT
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.awt.Color
import java.util.function.Consumer

/**
 * Created by cout970 on 2016/11/29.
 */
class RenderManager : ITickeable {

    lateinit var consumer: Consumer<VAO>
    lateinit var shader: ShaderProgram
    val tessellator = Tessellator()

    override fun tick() {
        GLStateMachine.clear()

        //temp
        shader.start()

        tessellator.draw(GL11.GL_QUADS, FormatPT(), consumer) {
            set(0, -0.9, -0.9, 0).set(1, 1, 0).endVertex()
            set(0, 0.9, -0.9, 0).set(1, 1, 1).endVertex()
            set(0, 0.9, 0.9, 0).set(1, 0, 1).endVertex()
            set(0, -0.9, 0.9, 0).set(1, 0, 0).endVertex()
        }

        tessellator.draw(GL11.GL_QUADS, FormatPT(), consumer) {
            set(0, -0.5, -0.5, 0).set(1, 1, 0).endVertex()
            set(0, 0.5, -0.5, 0).set(1, 1, 1).endVertex()
            set(0, 0.5, 0.5, 0).set(1, 0, 1).endVertex()
            set(0, -0.5, 0.5, 0).set(1, 0, 0).endVertex()
        }

        tessellator.draw(GL11.GL_QUADS, FormatPT(), consumer) {
            set(0, 0, 0, 0).set(1, 1, 0).endVertex()
            set(0, 0.8, 0, 0).set(1, 1, 1).endVertex()
            set(0, 0.8, 0.8, 0).set(1, 0, 1).endVertex()
            set(0, 0, 0.8, 0).set(1, 0, 0).endVertex()
        }

        shader.stop()
    }

    fun initOpenGl() {
        GLStateMachine.clearColor = Color.CYAN

        shader = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER, shader_vertex)
            compile(GL20.GL_FRAGMENT_SHADER, shader_fragment)
            bindAttribute(0, "pos")
            bindAttribute(1, "tex")
        }

        consumer = java.util.function.Consumer<VAO> {
            it.bind()
            it.bindAttrib()
            it.draw()
            it.unbindAttrib()
            VAO.unbind()
        }
    }

    internal val shader_vertex = """
#version 400 core

in vec3 pos;
in vec2 tex;

out vec2 pass_tex;

void main(){
    gl_Position = vec4(pos, 1.0);
    pass_tex = tex;
}
        """

    internal val shader_fragment = """
#version 400 core

in vec2 pass_tex;

out vec4 pixel;

uniform sampler2D sampler;

void main(){
    vec4 te = texture(sampler, pass_tex);
    //if(te.w < 0.1) discard;
    pixel = te;
}
        """
}