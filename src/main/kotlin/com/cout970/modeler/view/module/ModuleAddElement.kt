package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleAddElement(controller: ModuleController) : Module(controller, "Add element") {

    init {
        addSubComponent(Button(10f, 0f, 160f, 20f, "Cube").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.add.cube"))
        })
        addSubComponent(Button(10f, 20f, 160f, 20f, "Plane").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.add.plane"))
        })
        addSubComponent(Button(10f, 40f, 160f, 20f, "Group").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.add.group"))
        })
    }
}