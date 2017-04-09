package com.cout970.modeler.newView.render

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.config.Config
import com.cout970.modeler.newView.gui.Root
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.util.ITickeable
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
    lateinit var scene3dRenderer: SceneRenderer
    lateinit var scene2dRenderer: Scene2dRenderer

    fun initOpenGl(resourceLoader: ResourceLoader, timer: Timer, window: GLFWWindow) {
        this.window = window
        this.timer = timer
        guiRenderer = GuiRenderer(rootFrame, window.id)
        shaderHandler = ShaderHandler(resourceLoader)
        scene3dRenderer = SceneRenderer(shaderHandler)
        scene2dRenderer = Scene2dRenderer(shaderHandler)
        val c = Config.colorPalette.modelBackgroundColor
        GLStateMachine.clearColor = Color(c.xf, c.yf, c.zf)
    }

    override fun preTick() {
        guiRenderer.updateEvents()
    }

    override fun tick() {
        GLStateMachine.clear()
        rootFrame.centerPanel.scenes.forEach {
            //TODO
        }
        guiRenderer.render(rootFrame)
    }
}