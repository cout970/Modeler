package com.cout970.modeler.view.gui

import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IInput
import com.cout970.modeler.util.*
import com.cout970.modeler.view.controller.ButtonController
import com.cout970.modeler.view.gui.comp.CButton
import com.cout970.modeler.window.WindowHandler
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.ScrollablePanel

/**
 * Created by cout970 on 2016/12/03.
 */

class RootFrame(val input: IInput,
                val windowHandler: WindowHandler,
                val buttonController: ButtonController) : Frame() {

    val dropdown = Panel(0f, 20f, 150f, 80f)
    val topBar = TopBar(this)
    val leftBar = SideBar(this)
    val rightBar = SideBar(this)
    val contentPanel = ContentPanel(this)

    init {
        addComponent(topBar)
        addComponent(leftBar)
        addComponent(contentPanel)
        addComponent(rightBar)
        addComponent(dropdown)

        leftBar.apply { backgroundColor = Config.colorPalette.lightColor.toColor() }
        rightBar.apply { backgroundColor = Config.colorPalette.lightColor.toColor() }
        topBar.apply { backgroundColor = Config.colorPalette.lightColor.toColor() }
        dropdown.apply { backgroundColor = Config.colorPalette.lightColor.toColor() }

        leftBar.container.apply { backgroundColor = Config.colorPalette.lightColor.toColor() }
        rightBar.container.apply { backgroundColor = Config.colorPalette.lightColor.toColor() }
        contentPanel.apply { backgroundColor = Vector4f(0f, 0f, 0f, 0f) }

        rightBar.isEnabled = false
        dropdown.isVisible = false

        leftBar.verticalScrollBar.apply {
            backgroundColor = Config.colorPalette.darkColor.toColor()
            scrollColor = Config.colorPalette.lightColor.toColor()
            isArrowsEnabled = false
        }
    }

    fun update() {
        size = windowHandler.window.getFrameBufferSize().toJoml2f()

        topBar.size = Vector2f(size.x, 20f)
        leftBar.container.size.x = 200f
        rightBar.container.size.x = 200f

        leftBar.size = Vector2f(if (leftBar.isEnabled) 200f else 0f, size.y - topBar.size.y)
        rightBar.size = Vector2f(if (rightBar.isEnabled) 200f else 0f, size.y - topBar.size.y)

        leftBar.resize()
        rightBar.resize()

        if (leftBar.container.size.y >= leftBar.size.y) {
            leftBar.verticalScrollBar.isVisible = true
        } else {
            if (leftBar.isEnabled) {
                leftBar.size.x = 190f
            }
            contentPanel.position
            leftBar.verticalScrollBar.isVisible = false
        }

        contentPanel.size = Vector2f(size.x - leftBar.size.x - rightBar.size.x, size.y - topBar.size.y)

        leftBar.position = Vector2f(0f, topBar.size.y)
        contentPanel.position = Vector2f(leftBar.size.x, topBar.size.y)
        rightBar.position = Vector2f(leftBar.size.x + contentPanel.size.x, topBar.size.y)

        val mouse = input.mouse.getMousePos()
        if (!inside(mouse, topBar.position.toIVector(), topBar.size.toIVector() + vec2Of(0, 20)) &&
            !inside(mouse, dropdown.position.toIVector() - vec2Of(20, 0), dropdown.size.toIVector() + vec2Of(40, 20))) {
            dropdown.isVisible = false
        }
    }

    class ContentPanel(val root: RootFrame) : Panel()

    data class SideBar(val root: RootFrame) : ScrollablePanel() {
        init {
            horizontalScrollBar.isVisible = false
        }
    }

    inner class TopBar(val root: RootFrame) : Panel() {

        val controller get() = root.buttonController

        init {
            var i = 0
            addComponent(CButton("File", i++ * 60f, 0f, 60f, 20f).onClick(0, this::onClickTopBar))
            addComponent(CButton("Edit", i++ * 60f, 0f, 60f, 20f).onClick(1, this::onClickTopBar))
            addComponent(CButton("View", i++ * 60f, 0f, 60f, 20f).onClick(2, this::onClickTopBar))
            addComponent(CButton("Structure", i++ * 60f, 0f, 60f, 20f).onClick(3, this::onClickTopBar))
            addComponent(CButton("Help", i * 60f, 0f, 60f, 20f).onClick(4, this::onClickTopBar))
        }

        fun load(id: Int) {
            var i = 0
            val sizeRight = 150f
            if (id == 0) {
                root.dropdown.apply {
                    clearComponents()
                    addComponent(CButton("New", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.new",
                            controller).setTextLeft())
                    addComponent(CButton("Open", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.open",
                            controller).setTextLeft())
                    addComponent(CButton("Save", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.save",
                            controller).setTextLeft())
                    addComponent(CButton("Save as", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.save_as",
                            controller).setTextLeft())
                    addComponent(CButton("Import", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.import",
                            controller).setTextLeft())
                    addComponent(CButton("Export", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.export",
                            controller).setTextLeft())
                    addComponent(CButton("Settings", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.settings",
                            controller).setTextLeft())
                    addComponent(CButton("Exit", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.exit",
                            controller).setTextLeft())
                    size.y = i * 25f
                }
            } else if (id == 1) {
                root.dropdown.apply {
                    clearComponents()
                    addComponent(CButton("Undo", 0f, i++ * 25f, sizeRight, 25f).onClick("top.edit.undo",
                            controller).setTextLeft())
                    addComponent(CButton("Redo", 0f, i++ * 25f, sizeRight, 25f).onClick("top.edit.redo",
                            controller).setTextLeft())
                    addComponent(CButton("Cut", 0f, i++ * 25f, sizeRight, 25f).onClick("top.edit.cut",
                            controller).setTextLeft())
                    addComponent(CButton("Copy", 0f, i++ * 25f, sizeRight, 25f).onClick("top.edit.copy",
                            controller).setTextLeft())
                    addComponent(CButton("Paste", 0f, i++ * 25f, sizeRight, 25f).onClick("top.edit.paste",
                            controller).setTextLeft())
                    addComponent(CButton("Delete", 0f, i++ * 25f, sizeRight, 25f).onClick("top.edit.delete",
                            controller).setTextLeft())
                    size.y = i * 25f
                }
            } else if (id == 2) {
                root.dropdown.apply {
                    clearComponents()
                    addComponent(CButton("Show/Hide Left Panel", 0f, i++ * 25f, sizeRight, 25f).onClick(
                            "top.view.show_left", controller).setTextLeft())
                    addComponent(CButton("Show/Hide Right Panel", 0f, i++ * 25f, sizeRight, 25f).onClick(
                            "top.view.show_right", controller).setTextLeft())
                    addComponent(CButton("Layout Only model", 0f, i++ * 25f, sizeRight, 25f).onClick(
                            "top.view.one_model", controller).setTextLeft())
                    addComponent(CButton("Layout 2 Model scenes", 0f, i++ * 25f, sizeRight, 25f).onClick(
                            "top.view.two_model", controller).setTextLeft())
                    addComponent(CButton("Layout 4 Model scenes", 0f, i++ * 25f, sizeRight, 25f).onClick(
                            "top.view.four_model", controller).setTextLeft())
                    addComponent(CButton("Layout Model and Texture", 0f, i++ * 25f, sizeRight, 25f).onClick(
                            "top.view.model_and_texture", controller).setTextLeft())
                    addComponent(CButton("Layout 3 Model and 1 Texture", 0f, i++ * 25f, sizeRight, 25f).onClick(
                            "top.view.3_model_1_texture", controller).setTextLeft())
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