package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleAddElement(controller: ModuleController) : Module(controller, "Add element") {

    init {
        addSubComponent(Button(10f, 0f, 80f, 20f, "Cube").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(4))
        })
        addSubComponent(Button(90f, 0f, 80f, 20f, "Plane").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(5))
        })
        addSubComponent(Button(10f, 25f, 80f, 20f, "Mesh").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(6))
        })
        addSubComponent(Button(90f, 25f, 80f, 20f, "Submodel").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(7))
        })
    }
}