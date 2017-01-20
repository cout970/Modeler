package com.cout970.modeler.view.render

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.resource.ResourceLoader
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.view.UIManager
import java.awt.Color

/**
 * Created by cout970 on 2016/11/29.
 */
class RenderManager : ITickeable {

    lateinit var uiManager: UIManager
    lateinit var window: GLFWWindow
    lateinit var guiRenderer: GuiRenderer
    lateinit var modelRenderer: ModelRenderer
    lateinit var timer: Timer

    fun initOpenGl(resourceLoader: ResourceLoader, timer: Timer, window: GLFWWindow) {
        this.window = window
        this.timer = timer
        guiRenderer = GuiRenderer(uiManager.rootFrame, window.id)
        modelRenderer = ModelRenderer(resourceLoader)
        GLStateMachine.clearColor = Color(0.73f, 0.9f, 1f)
    }

    override fun preTick() {
        guiRenderer.updateEvents()
        uiManager.rootFrame.update()
    }

    override fun tick() {
        GLStateMachine.clear()
        uiManager.sceneController.scenes.forEach {
            it.render(this)
        }
        guiRenderer.render()
    }
}