package com.cout970.modeler.view.old.module

import com.cout970.modeler.view.newView.gui.comp.CButton
import com.cout970.modeler.view.old.controller.ModuleController
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleAddElement(controller: ModuleController) : Module(controller, "Add element") {

    init {
        addSubComponent(CButton("Cube", 5f, 5f, 180f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.add.cube"))
        })
        addSubComponent(CButton("Plane", 5f, 25f, 180f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.add.plane"))
        })
        addSubComponent(CButton("Group", 5f, 45f, 180f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.add.group"))
        })
        maximize()
    }
}