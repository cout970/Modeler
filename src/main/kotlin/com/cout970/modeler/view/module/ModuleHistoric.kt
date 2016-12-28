package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleHistoric(controller: ModuleController) : Module(controller, "History") {

    init {
        addSubComponent(Button(10f, 0f, 80f, 20f, "Undo").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(8))
        })
        addSubComponent(Button(90f, 0f, 80f, 20f, "Redo").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(9))
        })
        addSubComponent(Button(10f, 25f, 50f, 20f, "Copy").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(10))
        })
        addSubComponent(Button(65f, 25f, 50f, 20f, "Cut").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(11))
        })
        addSubComponent(Button(120f, 25f, 50f, 20f, "Paste").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(12))
        })
    }
}