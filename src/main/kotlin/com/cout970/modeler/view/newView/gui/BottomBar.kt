package com.cout970.modeler.view.newView.gui

import com.cout970.modeler.view.newView.ButtonController
import com.cout970.modeler.view.newView.GuiResources
import com.cout970.modeler.view.newView.gui.comp.CPanel
import com.cout970.modeler.view.newView.gui.comp.CToggleButton
import org.liquidengine.legui.component.ToggleButton
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.component.MouseClickEvent
import org.liquidengine.legui.listener.LeguiEventListener

/**
 * Created by cout970 on 2017/04/08.
 */

class BottomBar : CPanel() {

    fun init(buttonController: ButtonController, guiResources: GuiResources) {
        addComponent(CToggleButton(5f, 0f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java,
                    Wrapper(buttonController, "menu.select.element", 0, 0))
            setImage(guiResources.selectionModeElement)
            isToggled = true
        })
        addComponent(CToggleButton(37f, 0f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java,
                    Wrapper(buttonController, "menu.select.quad", 0, 1))
            setImage(guiResources.selectionModeQuad)
        })
        addComponent(CToggleButton(69f, 0f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java,
                    Wrapper(buttonController, "menu.select.edge", 0, 2))
            setImage(guiResources.selectionModeEdge)
        })
        addComponent(CToggleButton(101f, 0f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java,
                    Wrapper(buttonController, "menu.select.vertex", 0, 3))
            setImage(guiResources.selectionModeVertex)
        })

        addComponent(CToggleButton(140f, 0f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java,
                    Wrapper(buttonController, "menu.cursor.translation", 1, 0))
            textState.horizontalAlign = HorizontalAlign.LEFT
            setImage(guiResources.cursorTranslate)
            isToggled = true
        })
        addComponent(CToggleButton(172f, 0f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java,
                    Wrapper(buttonController, "menu.cursor.rotation", 1, 1))
            textState.horizontalAlign = HorizontalAlign.LEFT
            setImage(guiResources.cursorRotate)
        })
        addComponent(CToggleButton(204f, 0f, 32f, 32f).apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java,
                    Wrapper(buttonController, "menu.cursor.scale", 1, 2))
            textState.horizontalAlign = HorizontalAlign.LEFT
            setImage(guiResources.cursorScale)
        })
    }

    inner class Wrapper(val buttonController: ButtonController, val id: String, val category: Int,
                        val pos: Int) : LeguiEventListener<MouseClickEvent> {

        override fun update(e: MouseClickEvent) {
            if (e.action == MouseClickEvent.MouseClickAction.CLICK) {
                buttonController.onClick(id)
                components.forEachIndexed { i, it ->
                    if (it is ToggleButton) {
                        val listener = it.leguiEventListeners.getListeners(MouseClickEvent::class.java)[0]
                        val loc = when (category) {
                            1 -> i - 4
                            else -> i
                        }
                        if (listener is Wrapper && category == listener.category) {
                            it.isToggled = loc == pos
                        }
                    }
                }
            }
        }
    }
}