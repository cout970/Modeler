package com.cout970.modeler.newView.render

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IInput
import com.cout970.modeler.log.Level
import com.cout970.modeler.log.log
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.newView.gui.Root
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.window.WindowHandler
import com.cout970.vector.extensions.xf
import com.cout970.vector.extensions.yf
import com.cout970.vector.extensions.zf
import java.awt.Color

/**
 * Created by cout970 on 2016/11/29.
 */
class RenderManager : ITickeable {

    lateinit var rootFrame: Root
    lateinit var window: GLFWWindow
    lateinit var guiRenderer: GuiRenderer
    lateinit var shaderHandler: ShaderHandler
    lateinit var timer: Timer
    lateinit var sceneRenderer: SceneRenderer

    fun initOpenGl(resourceLoader: ResourceLoader, windowHandler: WindowHandler, modelEditor: ModelEditor,
                   input: IInput) {
        this.window = windowHandler.window
        this.timer = windowHandler.timer
        log(Level.FINE) { "[RenderManager] Creating GuiRenderer" }
        guiRenderer = GuiRenderer(rootFrame, window.id)
        log(Level.FINE) { "[RenderManager] Creating ShaderHandler" }
        shaderHandler = ShaderHandler(resourceLoader)
        log(Level.FINE) { "[RenderManager] Creating SceneRenderer" }
        sceneRenderer = SceneRenderer(shaderHandler, modelEditor, windowHandler, input)
        val c = Config.colorPalette.modelBackgroundColor
        GLStateMachine.clearColor = Color(c.xf, c.yf, c.zf)
    }

    override fun preTick() {
        guiRenderer.updateEvents()
    }

    override fun tick() {
        GLStateMachine.clear()
        rootFrame.contentPanel.scenes.forEach(sceneRenderer::render)
        guiRenderer.render(rootFrame)
    }
}