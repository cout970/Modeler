package com.cout970.modeler.view.module

import com.cout970.modeler.newView.gui.comp.CButton
import com.cout970.modeler.newView.gui.comp.CCheckBox
import com.cout970.modeler.view.controller.ModuleController
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleBoundingBoxes(controller: ModuleController) : Module(controller, "Add element") {

    init {
        addSubComponent(CButton("Export", 5f, 5f, 180f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.aabb.export"))
        })
        addSubComponent(CCheckBox("Show/Hide Bounding boxes", 6f, 26f, 178f, 18f, propertyBind("menu.aabb.show_aabb")))
        maximize()
    }
}