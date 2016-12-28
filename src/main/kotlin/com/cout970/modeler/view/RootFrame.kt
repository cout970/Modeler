package com.cout970.modeler.view

import com.cout970.modeler.util.toJoml2f
import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2016/12/03.
 */

class RootFrame(val viewManager: ViewManager) : Frame() {

    val topBar = TopBar(this)
    val leftBar = SideBar(this)
    val rightBar = SideBar(this)
    val contentPanel = ContentPanel(this)

    init {
        addComponent(topBar)
        addComponent(leftBar)
        addComponent(contentPanel)
        addComponent(rightBar)

        leftBar.apply { backgroundColor = Vector4f(0.8f, 0.8f, 0.8f, 1f) }
        rightBar.apply { backgroundColor = Vector4f(0.8f, 0.8f, 0.8f, 1f) }
        contentPanel.apply { backgroundColor = Vector4f(0.73f, 0.9f, 1f, 1f) }
        rightBar.isEnabled = false
    }

    fun update() {
        size = viewManager.window.getFrameBufferSize().toJoml2f()
        position = Vector2f(0f, 0f)

        topBar.size = Vector2f(size.x, 20f)

        leftBar.size = Vector2f(if (leftBar.isEnabled) 200f else 0f, size.y - topBar.size.y)
        rightBar.size = Vector2f(if (rightBar.isEnabled) 200f else 0f, size.y - topBar.size.y)
        contentPanel.size = Vector2f(size.x - leftBar.size.x - rightBar.size.x, size.y - topBar.size.y)

        leftBar.position = Vector2f(0f, topBar.size.y)
        contentPanel.position = Vector2f(leftBar.size.x, topBar.size.y)
        rightBar.position = Vector2f(leftBar.size.x + contentPanel.size.x, topBar.size.y)
    }

    class ContentPanel(val root: RootFrame) : Panel()

    class TopBar(val root: RootFrame) : Panel() {

        init {
            var i = 0
            addComponent(Button(i++ * 60f, 0f, 60f, 20f, "File"))
            addComponent(Button(i++ * 60f, 0f, 60f, 20f, "Edit"))
            addComponent(Button(i++ * 60f, 0f, 60f, 20f, "View"))
            addComponent(Button(i++ * 60f, 0f, 60f, 20f, "Structure"))
            addComponent(Button(i * 60f, 0f, 60f, 20f, "Help"))
        }
    }

    class SideBar(val root: RootFrame) : Panel()
}