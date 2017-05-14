package com.cout970.modeler.view.old.module

import com.cout970.modeler.view.newView.gui.comp.CButton
import com.cout970.modeler.view.old.controller.ModuleController
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