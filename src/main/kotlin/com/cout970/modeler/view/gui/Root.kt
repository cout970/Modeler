package com.cout970.modeler.view.gui

import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IInput
import com.cout970.modeler.resource.TextureHandler
import com.cout970.modeler.util.*
import com.cout970.modeler.view.controller.ButtonController
import com.cout970.modeler.view.gui.comp.CButton
import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.gui.comp.CToggleButton
import com.cout970.modeler.window.WindowHandler
import com.cout970.vector.extensions.minus
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector2f
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.component.ToggleButton
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.component.MouseClickEvent
import org.liquidengine.legui.listener.LeguiEventListener
import org.liquidengine.legui.util.ColorConstants

/**
 * Created by cout970 on 2017/03/09.
 */
class Root(
        val input: IInput,
        val windowHandler: WindowHandler,
        val buttonController: ButtonController,
        val textureHandler: TextureHandler
) : Frame(1f, 1f) {

    val topBar = TopBar(this)
    val bottomBar = CPanel()
    val leftBar = CPanel()
    val rightBar = CPanel()
    val centerPanel = CPanel()

    val topCenterPanel = CPanel()
    val bottomCenterPanel = CPanel()

    val topLeftPanel = CPanel()
    val bottomLeftPanel = CPanel()

    val dropdown = CPanel(0f, 20f, 150f, 80f)

    val searchBar = TextInput("")
    val searchPanel = CPanel()

    init {
        addComponent(topBar)
        addComponent(bottomBar)
        addComponent(leftBar)
        addComponent(rightBar)
        addComponent(centerPanel)
        addComponent(dropdown)

        leftBar.isEnabled = false
        rightBar.isEnabled = false
        topBar.isEnabled = false
        bottomBar.isEnabled = false

        centerPanel.addComponent(topCenterPanel)
        centerPanel.addComponent(bottomCenterPanel)

        leftBar.addComponent(topLeftPanel)
        leftBar.addComponent(bottomLeftPanel)

        topLeftPanel.addComponent(searchBar)

        listOf(topBar, bottomBar, leftBar, rightBar, centerPanel,
                topCenterPanel, topLeftPanel, dropdown, bottomLeftPanel
        ).forEach {
            it.backgroundColor = Config.colorPalette.lightColor.toColor()
        }

        centerPanel.backgroundColor = ColorConstants.transparent()
        bottomCenterPanel.backgroundColor = ColorConstants.transparent()
        dropdown.isVisible = false

        topCenterPanel.apply {
            addComponent(CToggleButton(5f, 0f, 32f, 32f).apply {
                leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.select.element", 0, 0))
                setImage(textureHandler.selectionModeElement)
                isToggled = true
            })
            addComponent(CToggleButton(37f, 0f, 32f, 32f).apply {
                leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.select.quad", 0, 1))
                setImage(textureHandler.selectionModeQuad)
            })
            addComponent(CToggleButton(69f, 0f, 32f, 32f).apply {
                leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.select.edge", 0, 2))
                setImage(textureHandler.selectionModeEdge)
            })
            addComponent(CToggleButton(101f, 0f, 32f, 32f).apply {
                leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.select.vertex", 0, 3))
                setImage(textureHandler.selectionModeVertex)
            })

            addComponent(CToggleButton(140f, 0f, 32f, 32f).apply {
                leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.cursor.translation", 1, 0))
                textState.horizontalAlign = HorizontalAlign.LEFT
                setImage(textureHandler.cursorTranslate)
                isToggled = true
            })
            addComponent(CToggleButton(172f, 0f, 32f, 32f).apply {
                leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.cursor.rotation", 1, 1))
                textState.horizontalAlign = HorizontalAlign.LEFT
                setImage(textureHandler.cursorRotate)
            })
            addComponent(CToggleButton(204f, 0f, 32f, 32f).apply {
                leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.cursor.scale", 1, 2))
                textState.horizontalAlign = HorizontalAlign.LEFT
                setImage(textureHandler.cursorScale)
            })
        }
    }

    inner class Wrapper(val id: String, val category: Int, val pos: Int) : LeguiEventListener<MouseClickEvent> {

        override fun update(e: MouseClickEvent) {
            if (e.action == MouseClickEvent.MouseClickAction.CLICK) {
                buttonController.onClick(id)
                topCenterPanel.components.forEachIndexed { i, it ->
                    if (it is ToggleButton) {
                        val listener = it.leguiEventListeners.getListeners(MouseClickEvent::class.java)[0]
                        val loc = when (category) {
                            1 -> i - 4
                            else -> i
                        }
                        if (listener is Wrapper && category == listener.category) {
                            it.isToggled = loc == pos
                        }
                    }
                }
            }
        }
    }

    fun update() {
        size = windowHandler.window.getFrameBufferSize().toJoml2f()

        topBar.isEnabled = Config.keyBindings.showTopMenu.check(input)

        // Size
        topBar.apply { size = Vector2f(parent.size.x, if (isEnabled) 20f else 0f) }
        bottomBar.apply { size = Vector2f(parent.size.x, if (isEnabled) 20f else 0f) }

        leftBar.apply {
            size = Vector2f(if (isEnabled) 120f else 0f, parent.size.y - topBar.size.y - bottomBar.size.y)
        }
        rightBar.apply {
            size = Vector2f(if (rightBar.isEnabled) 190f else 0f, parent.size.y - topBar.size.y - bottomBar.size.y)
        }

        centerPanel.size = Vector2f(size.x - leftBar.size.x - rightBar.size.x,
                size.y - topBar.size.y - bottomBar.size.y)

        topCenterPanel.size = Vector2f(centerPanel.size.x, 32f)
        bottomCenterPanel.size = Vector2f(centerPanel.size.x, centerPanel.size.y - topCenterPanel.size.y)

        topLeftPanel.size = Vector2f(leftBar.size.x, 20f)
        bottomLeftPanel.size = Vector2f(leftBar.size.x, leftBar.size.y - topLeftPanel.size.y)

        searchBar.size = topLeftPanel.size
        // Position
        topBar.position = Vector2f(0f, 0f)
        bottomBar.position = Vector2f(0f, size.y - bottomBar.size.y)

        leftBar.position = Vector2f(0f, topBar.size.y)
        rightBar.position = Vector2f(leftBar.size.x + centerPanel.size.x, topBar.size.y)

        centerPanel.position = Vector2f(leftBar.size.x, topBar.size.y)

        topCenterPanel.position = Vector2f(0f, 0f)
        bottomCenterPanel.position = Vector2f(0f, topCenterPanel.size.y)

        topLeftPanel.position = Vector2f(0f, 0f)
        bottomLeftPanel.position = Vector2f(0f, topLeftPanel.size.y)

        searchBar.position = Vector2f(0f, 0f)
        updateDropdownVisibility()
    }

    private fun updateDropdownVisibility() {
        val mousePos = input.mouse.getMousePos()

        val startTopBar = topBar.position.toIVector()
        val sizeTopBar = topBar.size.toIVector() + vec2Of(0, 20)

        val startDropdown = dropdown.position.toIVector() - vec2Of(20, 0)
        val sizeDropdown = dropdown.size.toIVector() + vec2Of(40, 20)

        if (!mousePos.isInside(startTopBar, sizeTopBar) && !mousePos.isInside(startDropdown, sizeDropdown)) {
            dropdown.isVisible = false
        }
    }

    inner class TopBar(val root: Root) : Panel() {

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