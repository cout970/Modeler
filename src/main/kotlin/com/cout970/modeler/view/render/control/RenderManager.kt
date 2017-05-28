package com.cout970.modeler.view.render.control

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.view.GuiState
import com.cout970.modeler.view.event.IInput
import com.cout970.modeler.view.render.tool.shader.UniversalShader
import com.cout970.modeler.view.window.WindowHandler
import com.cout970.vector.extensions.xf
import com.cout970.vector.extensions.yf
import com.cout970.vector.extensions.zf
import java.awt.Color

/**
 * Created by cout970 on 2016/11/29.
 */
class RenderManager : ITickeable {

    // @Injected
    lateinit var guiState: GuiState

    lateinit var guiRenderer: GuiRenderer
    lateinit var shader: UniversalShader
    lateinit var canvasRenderer: CanvasRenderer

    fun initOpenGl(resourceLoader: ResourceLoader, windowHandler: WindowHandler, input: IInput) {

        log(Level.FINE) { "[RenderManager] Creating GuiRenderer" }
        guiRenderer = GuiRenderer(guiState.root, windowHandler.window.id)
        log(Level.FINE) { "[RenderManager] Creating Universal Shader" }
        shader = UniversalShader(resourceLoader)
        log(Level.FINE) { "[RenderManager] Creating CanvasRenderer" }
        canvasRenderer = CanvasRenderer(this, input)
        val c = Config.colorPalette.modelBackgroundColor
        GLStateMachine.clearColor = Color(c.xf, c.yf, c.zf)
    }

    override fun preTick() {
        guiRenderer.updateEvents()
    }

    override fun tick() {
        GLStateMachine.clear()
//        guiState.windowHandler.resetViewport()
        canvasRenderer.render(guiState)
//        guiState.windowHandler.resetViewport()
        guiRenderer.render(guiState.root)
        guiState.windowHandler.resetViewport()
    }
}