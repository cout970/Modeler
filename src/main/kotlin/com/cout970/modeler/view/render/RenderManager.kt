package com.cout970.modeler.view.render

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.config.Config
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.view.UIManager
import com.cout970.modeler.view.gui.GuiRenderer
import com.cout970.modeler.view.scene.Scene2d
import com.cout970.modeler.view.scene.Scene3d
import com.cout970.vector.extensions.xf
import com.cout970.vector.extensions.yf
import com.cout970.vector.extensions.zf
import java.awt.Color

/**
 * Created by cout970 on 2016/11/29.
 */
class RenderManager : ITickeable {

    lateinit var uiManager: UIManager
    lateinit var window: GLFWWindow
    lateinit var guiRenderer: GuiRenderer
    lateinit var shaderHandler: ShaderHandler
    lateinit var timer: Timer
    lateinit var scene3dRenderer: Scene3dRenderer
    lateinit var scene2dRenderer: Scene2dRenderer

    fun initOpenGl(resourceLoader: ResourceLoader, timer: Timer, window: GLFWWindow) {
        this.window = window
        this.timer = timer
        guiRenderer = GuiRenderer(uiManager.rootFrame, window.id)
        shaderHandler = ShaderHandler(resourceLoader)
        scene3dRenderer = Scene3dRenderer(shaderHandler)
        scene2dRenderer = Scene2dRenderer(shaderHandler)
        val c = Config.colorPalette.modelBackgroundColor
        GLStateMachine.clearColor = Color(c.xf, c.yf, c.zf)
    }

    override fun preTick() {
        guiRenderer.updateEvents()
        uiManager.rootFrame.update()
    }

    override fun tick() {
        GLStateMachine.clear()
        uiManager.sceneController.scenes.forEach {
            when (it) {
                is Scene3d -> scene3dRenderer.render(it)
                is Scene2d -> scene2dRenderer.render(it)
            }
        }
        guiRenderer.render(uiManager.rootFrame)
    }
}