package com.cout970.modeler.view.render.tool.shader

import com.cout970.glutilities.shader.ShaderBuilder
import com.cout970.glutilities.shader.ShaderProgram
import com.cout970.glutilities.shader.UniformVariable
import com.cout970.glutilities.tessellator.VAO
import com.cout970.glutilities.tessellator.VaoBuilder
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.util.FloatArrayList
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.xf
import com.cout970.vector.extensions.yf
import com.cout970.vector.extensions.zf
import org.lwjgl.opengl.GL20
import java.util.function.Consumer

/**
 * Created by cout970 on 2017/05/25.
 */

class UniversalShader(resourceLoader: ResourceLoader) : Consumer<VAO> {

    private val program: ShaderProgram

    val matrixVP: UniformVariable
    val matrixM: UniformVariable
    val cameraPos: UniformVariable
    val lightPos: UniformVariable.UniformVariableArray
    val lightCount: UniformVariable
    val useLight: UniformVariable
    val textureSampler: UniformVariable
    val useTexture: UniformVariable
    val useColor: UniformVariable
    val lightColor: UniformVariable.UniformVariableArray
    val shineDamper: UniformVariable
    val reflectivity: UniformVariable
    val ambient: UniformVariable

    val buffer = Buffer()

    init {
        program = ShaderBuilder.build {
            compile(GL20.GL_VERTEX_SHADER,
                    resourceLoader.readResource("assets/shaders/universal_vertex.glsl").reader().readText())
            compile(GL20.GL_FRAGMENT_SHADER,
                    resourceLoader.readResource("assets/shaders/universal_fragment.glsl").reader().readText())
            bindAttribute(0, "in_position")
            bindAttribute(1, "in_texture")
            bindAttribute(2, "in_normal")
            bindAttribute(3, "in_color")
        }
        matrixVP = program.createUniformVariable("matrixVP")
        matrixM = program.createUniformVariable("matrixM")
        cameraPos = program.createUniformVariable("cameraPos")
        lightPos = program.createUniformVariableArray("lightPos")
        lightCount = program.createUniformVariable("lightCount")
        useLight = program.createUniformVariable("useLight")
        textureSampler = program.createUniformVariable("textureSampler")
        useTexture = program.createUniformVariable("useTexture")
        useColor = program.createUniformVariable("useColor")
        lightColor = program.createUniformVariableArray("lightColor")
        shineDamper = program.createUniformVariable("shineDamper")
        reflectivity = program.createUniformVariable("reflectivity")
        ambient = program.createUniformVariable("ambient")
    }

    fun useShader(ctx: RenderContext, func: (buffer: Buffer, shader: UniversalShader) -> Unit) {
        program.start()
        matrixVP.setMatrix4(ctx.camera.getMatrix(ctx.viewport))
        matrixM.setMatrix4(Matrix4.IDENTITY)
        cameraPos.setVector3(ctx.camera.position)

        lightCount.setInt(ctx.lights.size)
        ctx.lights.forEachIndexed { index, (pos, color) ->
            lightPos.setVector3(index, pos)
            lightColor.setVector3(index, color)
        }
        useTexture.setBoolean(false)
        useColor.setBoolean(true)
        useLight.setInt(0)
        func(buffer, this)
        program.stop()
    }

    override fun accept(it: VAO) {
        it.bind()
        it.bindAttrib()
        it.draw()
        it.unbindAttrib()
        VAO.unbind()
    }

    class Buffer {

        private val regions = mutableListOf<Pair<Int, Int>>()
        private var vertex = 0
        private var mode = -1

        private val pos = FloatArrayList()
        private val tex = FloatArrayList()
        private val norm = FloatArrayList()
        private val col = FloatArrayList()

        fun newRegion(mode: Int) {
            require(mode > 0) { "Invalid mode: $mode" }
            if (this.mode != -1) {
                regions.add(Pair(this.mode, vertex))
                vertex = 0
            }
            this.mode = mode
        }

        fun add(vpos: IVector3, vtex: IVector2, vnorm: IVector3, vcol: IVector3) {
            vertex++
            pos.add(vpos.xf)
            pos.add(vpos.yf)
            pos.add(vpos.zf)
            tex.add(vtex.xf)
            tex.add(vtex.yf)
            norm.add(vnorm.xf)
            norm.add(vnorm.yf)
            norm.add(vnorm.zf)
            col.add(vcol.xf)
            col.add(vcol.yf)
            col.add(vcol.zf)
        }

        fun addPos(x: Float, y: Float, z: Float) {
            vertex++
            pos.add(x)
            pos.add(y)
            pos.add(z)
        }

        fun addPos(vpos: IVector3) {
            vertex++
            pos.add(vpos.xf)
            pos.add(vpos.yf)
            pos.add(vpos.zf)
        }

        fun addTexCoord(u: Float, v: Float) {
            tex.add(u)
            tex.add(v)
        }

        fun addTexCoord(vtex: IVector3) {
            tex.add(vtex.xf)
            tex.add(vtex.yf)
        }

        fun addColor(x: Float, y: Float, z: Float) {
            col.add(x)
            col.add(y)
            col.add(z)
        }

        fun addColor(vcol: IVector3) {
            col.add(vcol.xf)
            col.add(vcol.yf)
            col.add(vcol.zf)
        }

        fun addNormal(x: Float, y: Float, z: Float) {
            norm.add(x)
            norm.add(y)
            norm.add(z)
        }

        fun addNormal(vnorm: IVector3) {
            norm.add(vnorm.xf)
            norm.add(vnorm.yf)
            norm.add(vnorm.zf)
        }

        fun build(mode: Int, func: Buffer.() -> Unit): VAO {
            var vao: VAO? = null
            pos.clear()
            tex.clear()
            col.clear()
            vertex = 0
            this.mode = mode
            func(this)
            regions.add(Pair(this.mode, vertex))

            pos.useAsBuffer(tex, norm, col) { pos, tex, norm, col ->
                val builder = VaoBuilder(true)
                builder.bindAttrib(0, pos, 3)
                builder.bindAttrib(1, tex, 2)
                builder.bindAttrib(2, norm, 3)
                builder.bindAttrib(3, col, 3)
                regions.forEach { builder.addRegion(it.first, it.second) }
                regions.clear()
                vao = builder.build()
            }
            return vao!!
        }
    }
}