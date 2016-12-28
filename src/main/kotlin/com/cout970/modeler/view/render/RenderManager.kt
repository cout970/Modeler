package com.cout970.modeler.view.render

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.ResourceManager
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.view.ViewManager

/**
 * Created by cout970 on 2016/11/29.
 */
class RenderManager(val viewManager: ViewManager) : ITickeable {

    lateinit var window: GLFWWindow
    lateinit var guiRenderer: GuiRenderer
    lateinit var modelRenderer: ModelRenderer
    lateinit var timer: Timer

    fun initOpenGl(resourceManager: ResourceManager, timer: Timer, window: GLFWWindow) {
        this.window = window
        this.timer = timer
        guiRenderer = GuiRenderer(viewManager.root, window.id)
        modelRenderer = ModelRenderer(resourceManager)
    }

    override fun preTick() {
        guiRenderer.updateEvents()
        viewManager.root.update()
    }

    override fun tick() {
        GLStateMachine.clear()
        guiRenderer.render()
        viewManager.sceneController.scenes.forEach {
            it.render(this)
        }
    }
}