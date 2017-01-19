package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import org.liquidengine.legui.component.ToggleButton
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleSelectionType(controller: ModuleController) : Module(controller, "Selection Mode") {

    init {
        addSubComponent(ToggleButton(13f, 0f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.select.group", 0))
        })
        addSubComponent(ToggleButton(53f, 0f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.select.mesh", 1))
            isToggled = true
        })
        addSubComponent(ToggleButton(93f, 0f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.select.quad", 2))
        })
        addSubComponent(ToggleButton(133f, 0f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.select.vertex", 3))
        })
    }

    inner class Wrapper(id: String, val pos: Int) : Listener(controller.viewManager.buttonController, id) {

        override fun update(e: MouseClickEvent) {
            super.update(e)
            subPanel.components.forEachIndexed { i, it ->
                if (it is ToggleButton) {
                    it.isToggled = i == pos
                }
            }
            this@ModuleSelectionType.controller.modelController.selectionManager.clearSelection()
        }
    }
}