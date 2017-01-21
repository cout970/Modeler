package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleHistoric(controller: ModuleController) : Module(controller, "History") {

    init {
        addSubComponent(Button("Undo", 10f, 0f, 80f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.history.undo"))
        })
        addSubComponent(Button("Redo", 90f, 0f, 80f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.history.redo"))
        })
        addSubComponent(Button("Copy", 10f, 25f, 50f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.clipboard.copy"))
        })
        addSubComponent(Button("Cut", 65f, 25f, 50f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.clipboard.cut"))
        })
        addSubComponent(Button("Paste", 120f, 25f, 50f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.clipboard.paste"))
        })
    }
}