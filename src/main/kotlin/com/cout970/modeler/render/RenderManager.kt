package com.cout970.modeler.render

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.ITickeable
import com.cout970.modeler.render.layout.IView
import com.cout970.modeler.render.layout.ViewModelEdit

/**
 * Created by cout970 on 2016/11/29.
 */
class RenderManager() : ITickeable {

    val allViews = mutableListOf(ViewModelEdit())
    lateinit var view: IView
    lateinit var window: GLFWWindow
    lateinit var gui: GuiRenderer
    val root = RootPanel(this)


    fun initOpenGl(window: GLFWWindow) {
        this.window = window
        gui = GuiRenderer(window.id, this)
        view = allViews.first()
        root.loadView(view)
    }

    override fun preTick() {
        gui.update()
        root.updateViewport()
    }

    override fun tick() {
        GLStateMachine.clear()
        gui.render()
        view.renderExtras()
    }
}