package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import com.cout970.modeler.view.gui.comp.CButton
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2017/02/04.
 */
class ModuleSelection(controller: ModuleController) : Module(controller, "Selection") {

    init {
        addSubComponent(CButton("Enable non blocking selection", 5f, 5f, 180f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.add.cube"))
        })
        addSubComponent(CButton("Render selection outline", 5f, 25f, 180f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.add.plane"))
        })
        maximize()
    }
}