package com.cout970.modeler.render

import com.cout970.modeler.render.layout.IView
import org.joml.Vector2f
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.Viewport

/**
 * Created by cout970 on 2016/12/03.
 */

class RootPanel(val renderManager: RenderManager) : Panel(), Viewport {

    lateinit var contentPanel: Panel
    lateinit var topBar: TopBar

    override fun updateViewport() {
        size = renderManager.gui.context.framebufferSize
        position = Vector2f(0f, 0f)
        contentPanel.size = Vector2f(size.x, size.y - 20)
        contentPanel.position = Vector2f(0f, 20f)
    }

    fun loadView(view: IView) {
        view.onLoad(renderManager)
        contentPanel = view.getContentPanel()
        clearComponents()
        topBar = TopBar(this)
        addComponent(topBar)
        addComponent(contentPanel)
    }

    class TopBar(val root: RootPanel) : Panel() {

        init {
            size = Vector2f(root.size.x, 20f)
            position = Vector2f(0f, 0f)
        }
    }
}