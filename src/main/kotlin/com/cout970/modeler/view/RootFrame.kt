package com.cout970.modeler.view

import com.cout970.modeler.event.IInput
import com.cout970.modeler.util.inside
import com.cout970.modeler.util.onClick
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.view.controller.ButtonController
import com.cout970.modeler.window.WindowHandler
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.ScrollablePanel

/**
 * Created by cout970 on 2016/12/03.
 */

class RootFrame(val input: IInput, val windowHandler: WindowHandler, val buttonController: ButtonController) : Frame() {

    val dropdown = Panel(0f, 20f, 100f, 80f)
    val topBar = TopBar(this)
    val leftBar = SideBar(this, true)
    val rightBar = SideBar(this, false)
    val contentPanel = ContentPanel(this)

    init {
        addComponent(topBar)
        addComponent(leftBar)
        addComponent(contentPanel)
        addComponent(rightBar)
        addComponent(dropdown)

        leftBar.apply { backgroundColor = Vector4f(0.8f, 0.8f, 0.8f, 1f) }
        rightBar.apply { backgroundColor = Vector4f(0.8f, 0.8f, 0.8f, 1f) }
        leftBar.container.apply { backgroundColor = Vector4f(0.8f, 0.8f, 0.8f, 1f) }
        rightBar.container.apply { backgroundColor = Vector4f(0.8f, 0.8f, 0.8f, 1f) }
        contentPanel.apply { backgroundColor = Vector4f(0f, 0f, 0f, 0f) }
        rightBar.isEnabled = false
        dropdown.isVisible = false
    }

    fun update() {
        size = windowHandler.window.getFrameBufferSize().toJoml2f()
        position = Vector2f(0f, 0f)

        topBar.size = Vector2f(size.x, 20f)
        leftBar.container.size.x = 200f
        rightBar.container.size.x = 200f

        leftBar.size = Vector2f(if (leftBar.isEnabled) 200f else 0f, size.y - topBar.size.y)
        rightBar.size = Vector2f(if (rightBar.isEnabled) 200f else 0f, size.y - topBar.size.y)
        contentPanel.size = Vector2f(size.x - leftBar.size.x - rightBar.size.x, size.y - topBar.size.y)

        leftBar.position = Vector2f(0f, topBar.size.y)
        contentPanel.position = Vector2f(leftBar.size.x, topBar.size.y)
        rightBar.position = Vector2f(leftBar.size.x + contentPanel.size.x, topBar.size.y)

        leftBar.resize()
        rightBar.resize()

        val mouse = input.mouse.getMousePos()
        if (!inside(mouse, topBar.position.toIVector(), topBar.size.toIVector() + vec2Of(0, 20)) &&
                !inside(mouse, dropdown.position.toIVector() - vec2Of(20, 0), dropdown.size.toIVector() + vec2Of(40, 20))) {
            dropdown.isVisible = false
        }
    }

    class ContentPanel(val root: RootFrame) : Panel()

    data class SideBar(val root: RootFrame, val left: Boolean) : ScrollablePanel() {

        init {
            horizontalScrollBar.isVisible = false
        }
    }

    inner class TopBar(val root: RootFrame) : Panel() {

        val controller get() = root.buttonController

        init {
            var i = 0
            addComponent(Button(i++ * 60f, 0f, 60f, 20f, "File").onClick(0, this::onClickTopBar))
            addComponent(Button(i++ * 60f, 0f, 60f, 20f, "Edit").onClick(1, this::onClickTopBar))
            addComponent(Button(i++ * 60f, 0f, 60f, 20f, "View").onClick(2, this::onClickTopBar))
            addComponent(Button(i++ * 60f, 0f, 60f, 20f, "Structure").onClick(3, this::onClickTopBar))
            addComponent(Button(i * 60f, 0f, 60f, 20f, "Help").onClick(4, this::onClickTopBar))
        }

        fun load(id: Int) {
            var i = 0
            if (id == 0) {
                root.dropdown.apply {
                    clearComponents()
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "New").onClick("top.file.add", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Open").onClick("top.file.open", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Save").onClick("top.file.save", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Save as").onClick("top.file.saveas", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Import").onClick("top.file.import", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Export").onClick("top.file.export", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Settings").onClick("top.file.settings", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Exit").onClick("top.file.exit", controller))
                    size.y = i * 25f
                }
            } else if (id == 1) {
                root.dropdown.apply {
                    clearComponents()
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Undo").onClick("top.edit.undo", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Redo").onClick("top.edit.redo", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Cut").onClick("top.edit.cut", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Copy").onClick("top.edit.copy", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Paste").onClick("top.edit.paste", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Delete").onClick("top.edit.delete", controller))
                    size.y = i * 25f
                }
            } else if (id == 2) {
                root.dropdown.apply {
                    clearComponents()
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Show/Hide Left Panel")
                            .onClick("top.view.showleft", controller))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Show/Hide Right Panel")
                            .onClick("top.view.showright", controller))
                    size.y = i * 25f
                }
            } else {
                root.dropdown.apply {
                    clearComponents()
                }
            }
        }

        fun onClickTopBar(id: Int) {
            root.dropdown.apply {
                isVisible = true
                position.x = id * 60f
                root.topBar.load(id)
            }
        }
    }
}