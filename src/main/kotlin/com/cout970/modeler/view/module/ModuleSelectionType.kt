package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleSelectionType(controller: ModuleController) : Module(controller, "Selection Mode") {

    init {
        addSubComponent(Button(10f, 0f, 40f, 40f, "").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(0))
        })
        addSubComponent(Button(50f, 0f, 40f, 40f, "").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(1))
        })
        addSubComponent(Button(90f, 0f, 40f, 40f, "").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(2))
        })
        addSubComponent(Button(130f, 0f, 40f, 40f, "").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(3))
        })
    }
}