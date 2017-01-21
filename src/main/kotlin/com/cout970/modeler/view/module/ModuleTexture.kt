package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2017/01/19.
 */
class ModuleTexture(controller: ModuleController) : Module(controller, "Texture") {

    init {
        addSubComponent(Button("Import", 10f, 0f, 160f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.texture.import"))
        })
        addSubComponent(Button("Size", 10f, 20f, 160f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.texture.size"))
        })
        addSubComponent(Button("FlipX", 10f, 40f, 160f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.texture.flip.x"))
        })
        addSubComponent(Button("FlipY", 10f, 60f, 160f, 20f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, buttonListener("menu.texture.flip.y"))
        })
    }
}