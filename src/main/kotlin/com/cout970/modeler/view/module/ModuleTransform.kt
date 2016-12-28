package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleTransform(controller: ModuleController) : Module(controller, "Transform") {

    init {
        addSubComponent(Button(10f, 0f, 53f, 20f, "Translate").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(13))
        })
        addSubComponent(Button(63f, 0f, 53f, 20f, "Rotate").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(14))
        })
        addSubComponent(Button(116f, 0f, 53f, 20f, "Scale").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener(15))
        })
    }
}