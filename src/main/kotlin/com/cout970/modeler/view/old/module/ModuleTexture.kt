package com.cout970.modeler.view.old.module

import com.cout970.modeler.view.newView.gui.comp.CButton
import com.cout970.modeler.view.newView.gui.comp.CCheckBox
import com.cout970.modeler.view.old.controller.ModuleController
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2017/01/19.
 */
class ModuleTexture(controller: ModuleController) : Module(controller, "Texture") {

    init {
        addSubComponent(CButton("Import Texture", 5f, 5f, 180f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.texture.import"))
        })
        addSubComponent(CButton("Export Texture", 5f, 25f, 180f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.texture.export"))
        })
        addSubComponent(CButton("Split vertex", 5f, 45f, 180f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.texture.split"))
        })
        addSubComponent(CCheckBox("Show all mesh", 6f, 66f, 178f, 18f, propertyBind("menu.texture.show_all_mesh")))
        maximize()
    }
}