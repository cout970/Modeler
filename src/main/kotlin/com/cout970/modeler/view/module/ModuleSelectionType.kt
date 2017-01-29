package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import com.cout970.modeler.view.gui.TextureHandler
import com.cout970.modeler.view.gui.comp.CToggleButton
import org.liquidengine.legui.component.ToggleButton
import org.liquidengine.legui.event.component.MouseClickEvent

/**
 * Created by cout970 on 2016/12/27.
 */
class ModuleSelectionType(controller: ModuleController, textureHandler: TextureHandler) : Module(controller,
        "Selection Mode") {

    init {
        addSubComponent(CToggleButton(15f, 5f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.select.group", 0))
            setImage(textureHandler.selectionModeGroup)
        })
        addSubComponent(CToggleButton(57f, 5f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.select.mesh", 1))
            isToggled = true
            setImage(textureHandler.selectionModeMesh)
        })
        addSubComponent(CToggleButton(99f, 5f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.select.quad", 2))
            setImage(textureHandler.selectionModeQuad)
        })
        addSubComponent(CToggleButton(141f, 5f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, Wrapper("menu.select.vertex", 3))
            setImage(textureHandler.selectionModeVertex)
        })
        maximize()
    }

    inner class Wrapper(id: String, val pos: Int) : ButtonListener(controller.buttonController, id) {

        override fun update(e: MouseClickEvent) {
            super.update(e)
            container.components.forEachIndexed { i, it ->
                if (it is ToggleButton) {
                    it.isToggled = i == pos
                }
            }
            this@ModuleSelectionType.controller.modelProvider.selectionManager.clearModelSelection()
        }
    }
}