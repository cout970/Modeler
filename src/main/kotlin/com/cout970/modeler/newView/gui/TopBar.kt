package com.cout970.modeler.newView.gui

import com.cout970.modeler.newView.ButtonController
import com.cout970.modeler.newView.gui.comp.CButton
import com.cout970.modeler.newView.gui.comp.CPanel
import com.cout970.modeler.util.onClick
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2017/04/08.
 */

class TopBar : Panel() {

    lateinit var buttonController: ButtonController
    lateinit var dropdown: CPanel

    fun init(buttonController: ButtonController, dropdown: CPanel) {
        this.buttonController = buttonController
        this.dropdown = dropdown
        var i = 0
        addComponent(CButton("File", i++ * 60f, 0f, 60f, 20f).onClick(0, this::onClickTopBar))
        addComponent(CButton("Edit", i++ * 60f, 0f, 60f, 20f).onClick(1, this::onClickTopBar))
        addComponent(CButton("View", i++ * 60f, 0f, 60f, 20f).onClick(2, this::onClickTopBar))
        addComponent(CButton("Structure", i++ * 60f, 0f, 60f, 20f).onClick(3, this::onClickTopBar))
        addComponent(CButton("Help", i++ * 60f, 0f, 60f, 20f).onClick(4, this::onClickTopBar))
        addComponent(CButton("max", i++ * 60f, 0f, 60f, 20f).onClick(5) { buttonController.onClick("window.maximize") })
        addComponent(CButton("min", i++ * 60f, 0f, 60f, 20f).onClick(6) { buttonController.onClick("window.minimize") })
        addComponent(CButton("X", i * 60f, 0f, 60f, 20f).onClick(7) { buttonController.onClick("exit") })
    }

    fun load(id: Int) {
        var i = 0
        val sizeRight = 150f
        if (id == 0) {
            dropdown.apply {
                clearComponents()
                addComponent(CButton("New", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.new",
                        buttonController).setTextLeft())
                addComponent(CButton("Open", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.open",
                        buttonController).setTextLeft())
                addComponent(CButton("Save", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.save",
                        buttonController).setTextLeft())
                addComponent(CButton("Save as", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.save_as",
                        buttonController).setTextLeft())
                addComponent(CButton("Import", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.import",
                        buttonController).setTextLeft())
                addComponent(CButton("Export", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.export",
                        buttonController).setTextLeft())
                addComponent(CButton("Settings", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.settings",
                        buttonController).setTextLeft())
                addComponent(CButton("Exit", 0f, i++ * 25f, sizeRight, 25f).onClick("top.file.exit",
                        buttonController).setTextLeft())
                size.y = i * 25f
            }
        } else if (id == 1) {
            dropdown.apply {
                clearComponents()
                addComponent(CButton("Undo", 0f, i++ * 25f, sizeRight, 25f).onClick("top.edit.undo",
                        buttonController).setTextLeft())
                addComponent(CButton("Redo", 0f, i++ * 25f, sizeRight, 25f).onClick("top.edit.redo",
                        buttonController).setTextLeft())
                addComponent(CButton("Cut", 0f, i++ * 25f, sizeRight, 25f).onClick("top.edit.cut",
                        buttonController).setTextLeft())
                addComponent(CButton("Copy", 0f, i++ * 25f, sizeRight, 25f).onClick("top.edit.copy",
                        buttonController).setTextLeft())
                addComponent(CButton("Paste", 0f, i++ * 25f, sizeRight, 25f).onClick("top.edit.paste",
                        buttonController).setTextLeft())
                addComponent(CButton("Delete", 0f, i++ * 25f, sizeRight, 25f).onClick("top.edit.delete",
                        buttonController).setTextLeft())
                size.y = i * 25f
            }
        } else if (id == 2) {
            dropdown.apply {
                clearComponents()
                addComponent(CButton("Show/Hide Left Panel", 0f, i++ * 25f, sizeRight, 25f).onClick(
                        "top.view.show_left", buttonController).setTextLeft())
                addComponent(CButton("Show/Hide Right Panel", 0f, i++ * 25f, sizeRight, 25f).onClick(
                        "top.view.show_right", buttonController).setTextLeft())
                addComponent(CButton("Layout Only model", 0f, i++ * 25f, sizeRight, 25f).onClick(
                        "top.view.one_model", buttonController).setTextLeft())
                addComponent(CButton("Layout 2 Model scenes", 0f, i++ * 25f, sizeRight, 25f).onClick(
                        "top.view.two_model", buttonController).setTextLeft())
                addComponent(CButton("Layout 4 Model scenes", 0f, i++ * 25f, sizeRight, 25f).onClick(
                        "top.view.four_model", buttonController).setTextLeft())
                addComponent(CButton("Layout Model and Texture", 0f, i++ * 25f, sizeRight, 25f).onClick(
                        "top.view.model_and_texture", buttonController).setTextLeft())
                addComponent(CButton("Layout 3 Model and 1 Texture", 0f, i++ * 25f, sizeRight, 25f).onClick(
                        "top.view.3_model_1_texture", buttonController).setTextLeft())
                size.y = i * 25f
            }
        } else {
            dropdown.apply {
                clearComponents()
            }
        }
    }

    fun onClickTopBar(id: Int) {
        dropdown.apply {
            isVisible = true
            position.x = id * 60f
            this@TopBar.load(id)
        }
    }
}