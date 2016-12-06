package com.cout970.modeler.render

import com.cout970.modeler.render.layout.Layout
import com.cout970.modeler.util.toJoml2f
import org.joml.Vector2f
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.Viewport
import org.liquidengine.legui.context.LeguiContext

/**
 * Created by cout970 on 2016/12/03.
 */

class RootPanel(val renderManager: RenderManager) : Panel(), Viewport {

    lateinit var contentPanel: Panel
    lateinit var topBar: TopBar

    override fun updateViewport() {
        size = renderManager.window.getFrameBufferSize().toJoml2f()
        position = Vector2f(0f, 0f)
        contentPanel.size = Vector2f(size.x, size.y - 20)
        contentPanel.position = Vector2f(0f, 20f)
    }

    fun loadView(layout: Layout) {
        layout.onLoad()
        contentPanel = layout.contentPanel
        clearComponents()
        topBar = TopBar(this)
        addComponent(topBar)
        addComponent(contentPanel)
    }

    class TopBar(val root: RootPanel) : Panel() {

        init {
            position = Vector2f(0f, 0f)
            size = Vector2f(root.size.x, 20f)
            addComponent(Button(0f, 0f, 60f, 20f, "File"))
            addComponent(Button(60f, 0f, 60f, 20f, "Edit"))
            addComponent(Button(120f, 0f, 60f, 20f, "View"))
        }

        override fun render(context: LeguiContext?) {
            position = Vector2f(0f, 0f)
            size = Vector2f(root.size.x, 20f)
            super.render(context)
        }
    }
}