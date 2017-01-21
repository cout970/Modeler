package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleTransform(controller: ModuleController) : Module(controller, "Transform") {

    init {
        addSubComponent(Button("Translate", 10f, 0f, 160f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.cursor.translation"))
        })
        addSubComponent(Button("Rotate", 10f, 20f, 160f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.cursor.rotation"))
        })
        addSubComponent(Button("Scale", 10f, 40f, 160f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.cursor.scale"))
        })
    }
}