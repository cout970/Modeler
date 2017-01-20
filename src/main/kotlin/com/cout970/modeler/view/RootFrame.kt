package com.cout970.modeler.view

import com.cout970.modeler.util.inside
import com.cout970.modeler.util.onClick
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJoml2f
import com.cout970.modeler.view.popup.*
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

class RootFrame(val viewManager: ViewManager) : Frame() {

    val dropdown = Panel(0f, 20f, 100f, 80f)
    val topBar = TopBar(this)
    val leftBar = SideBar(this)
    val rightBar = SideBar(this)
    val contentPanel = ContentPanel(this)

    init {
        addComponent(topBar)
        addComponent(leftBar)
        addComponent(contentPanel)
        addComponent(dropdown)
        addComponent(rightBar)

        leftBar.apply { backgroundColor = Vector4f(0.8f, 0.8f, 0.8f, 1f) }
        rightBar.apply { backgroundColor = Vector4f(0.8f, 0.8f, 0.8f, 1f) }
        leftBar.container.apply { backgroundColor = Vector4f(0.8f, 0.8f, 0.8f, 1f) }
        rightBar.container.apply { backgroundColor = Vector4f(0.8f, 0.8f, 0.8f, 1f) }
        contentPanel.apply { backgroundColor = Vector4f(0f, 0f, 0f, 0f) }

//        rightBar.isEnabled = false
        dropdown.isVisible = false
    }

    fun update() {
        size = viewManager.getSize().toJoml2f()
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

        val mouse = viewManager.sceneController.mouse.getMousePos()
        if (!inside(mouse, topBar.position.toIVector(), topBar.size.toIVector() + vec2Of(0, 20)) &&
                !inside(mouse, dropdown.position.toIVector() - vec2Of(20, 0), dropdown.size.toIVector() + vec2Of(40, 20))) {
            dropdown.isVisible = false
        }
    }

    class ContentPanel(val root: RootFrame) : Panel()

    class SideBar(val root: RootFrame) : ScrollablePanel() {

        init {
            horizontalScrollBar.isVisible = false
        }
    }

    class TopBar(val root: RootFrame) : Panel() {

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
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "New").onClick(0, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Open").onClick(1, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Save").onClick(2, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Save as").onClick(3, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Import").onClick(4, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Export").onClick(5, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Settings").onClick(6, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Exit").onClick(7, this@TopBar::onClickDropdown))
                    size.y = i * 25f
                }
            } else if (id == 1) {
                root.dropdown.apply {
                    clearComponents()
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Undo").onClick(8, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Redo").onClick(9, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Cut").onClick(10, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Copy").onClick(11, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Paste").onClick(12, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Delete").onClick(13, this@TopBar::onClickDropdown))
                    size.y = i * 25f
                }
            } else if (id == 2) {
                root.dropdown.apply {
                    clearComponents()
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Show/Hide Left Panel").onClick(14, this@TopBar::onClickDropdown))
                    addComponent(Button(0f, i++ * 25f, 100f, 25f, "Show/Hide Right Panel").onClick(15, this@TopBar::onClickDropdown))
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

        fun onClickDropdown(id: Int) {
            val modelController = root.viewManager.sceneController.modelController
            when (id) {
                0 -> newProject(modelController)
                1 -> loadProject(modelController)
                2 -> saveProject(modelController)
                3 -> saveProjectAs(modelController)
                4 -> showImportModelPopup(modelController)
                5 -> showExportModelPopup(modelController)
                6 -> Missing("settings")
                7 -> root.viewManager.windowController.stop()

                8 -> modelController.historyRecord.undo()
                9 -> modelController.historyRecord.redo()
                10 -> modelController.clipboard.cut()
                11 -> modelController.clipboard.copy()
                12 -> modelController.clipboard.paste()
                13 -> modelController.delete()

                14 -> root.leftBar.isEnabled = !root.leftBar.isEnabled
                15 -> root.rightBar.isEnabled = !root.rightBar.isEnabled
            }
        }
    }
}