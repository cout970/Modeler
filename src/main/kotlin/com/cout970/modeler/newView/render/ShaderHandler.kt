package com.cout970.modeler.newView.render

import com.cout970.glutilities.tessellator.*
import com.cout970.glutilities.tessellator.format.FormatPT
import com.cout970.glutilities.texture.Texture
import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.model.material.MaterialNone
import com.cout970.modeler.newView.render.shader.*
import com.cout970.modeler.resource.ResourceLoader
import java.util.function.Consumer

/**
 * Created by cout970 on 2016/12/03.
 */
class ShaderHandler(resourceLoader: ResourceLoader) {

    val tessellator = Tessellator()
    val consumer: Consumer<VAO>
    // vao formats
    val formatPC = FormatPC()
    val formatPTN = FormatPTN()
    val formatPT = FormatPT()
    val formatPCT = FormatPCT()

    val cursorTexture: Texture

    private val shaders: Map<ShaderType, IShader>

    init {
        log(Level.FINE) { "[ShaderHandler] Loading shaders..." }
        log(Level.FINE) { "[ShaderHandler] Loading ModelShader" }
        val modelShader = ModelShader(resourceLoader)
        log(Level.FINE) { "[ShaderHandler] Loading SelectionShader" }
        val selectionShader = SelectionShader(resourceLoader)
        log(Level.FINE) { "[ShaderHandler] Loading UVShader" }
        val uvShader = UVShader(resourceLoader)
        log(Level.FINE) { "[ShaderHandler] Loading GuiShader" }
        val guiShader = GuiShader(resourceLoader)
        log(Level.FINE) { "[ShaderHandler] All shaders loaded" }

        shaders = mapOf(
                ShaderType.MODEL_SHADER to modelShader,
                ShaderType.SELECTION_SHADER to selectionShader,
                ShaderType.UV_SHADER to uvShader,
                ShaderType.GUI_SHADER to guiShader
        )

        cursorTexture = resourceLoader.getTexture("assets/textures/cursor.png")

        consumer = Consumer<VAO> {
            it.bind()
            it.bindAttrib()
            it.draw()
            it.unbindAttrib()
            VAO.Companion.unbind()
        }

        MaterialNone.loadTexture(resourceLoader)
    }

    private var currentShader: IShader? = null

    fun useShader(type: ShaderType, ctx: RenderContext, func: ShaderHandler.() -> Unit) {
        currentShader = shaders[type]
        currentShader?.useShader(ctx) {
            func()
        }
        currentShader = null
    }

    var enableColor: Boolean
        get() = false
        set(value) = (currentShader as? UVShader)?.enableColor(value) ?: Unit

    object BufferSize {
        val value = 262144 //256 Mb
    }

    class FormatPC : IFormat {

        var bufferPos = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 3)
        var bufferCol = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 3)

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

        var bufferPos = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 3)
        var bufferCol = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 3)
        var bufferTex = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 2)

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

    class FormatPTN : IFormat {

        var bufferPos = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 3)
        var bufferTex = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 2)
        var bufferNorm = Buffer(IBuffer.BufferType.FLOAT, BufferSize.value, 3)

        override fun getBuffers(): List<IBuffer> = listOf(bufferPos, bufferTex, bufferNorm)

        override fun injectData(builder: VaoBuilder) {
            builder.bindAttribf(0, bufferPos.getBase().apply { flip() }, 3)
            builder.bindAttribf(1, bufferTex.getBase().apply { flip() }, 2)
            builder.bindAttribf(2, bufferNorm.getBase().apply { flip() }, 3)
        }

        override fun reset() {
            bufferPos.getBase().clear()
            bufferTex.getBase().clear()
            bufferNorm.getBase().clear()
        }
    }
}
