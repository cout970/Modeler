package com.cout970.modeler.render

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.structure.Timer
import com.cout970.glutilities.window.GLFWWindow
import com.cout970.modeler.ITickeable
import com.cout970.modeler.ResourceManager
import com.cout970.modeler.event.EventController
import com.cout970.modeler.modelcontrol.ModelController
import com.cout970.modeler.render.layout.Layout
import com.cout970.modeler.render.layout.LayoutModelEdit
import com.cout970.modeler.render.renderer.GuiRenderer
import com.cout970.modeler.render.renderer.ModelRenderer

/**
 * Created by cout970 on 2016/11/29.
 */
class RenderManager() : ITickeable {

    val allLayouts = mutableListOf(LayoutModelEdit(this))
    var layout: Layout = allLayouts.first()
        private set

    lateinit var window: GLFWWindow
    lateinit var gui: GuiRenderer
    lateinit var modelController: ModelController
    lateinit var modelRenderer: ModelRenderer
    lateinit var timer: Timer
    val root: RootPanel

    init {
        root = RootPanel(this)
        root.loadView(layout)
    }

    fun load(resourceManager: ResourceManager, eventController: EventController, modelController: ModelController, timer: Timer) {
        modelRenderer = ModelRenderer(resourceManager)
        this.modelController = modelController
        this.timer = timer
        allLayouts.forEach {
            it.loadResources(resourceManager)
            it.viewController.registerListeners(eventController)
        }
    }

    fun initOpenGl(window: GLFWWindow) {
        this.window = window
        gui = GuiRenderer(root, window.id)
    }

    override fun preTick() {
        gui.updateEvents()
        root.updateViewport()
    }

    override fun tick() {
        GLStateMachine.clear()
        gui.render()
        layout.renderExtras()
    }
}