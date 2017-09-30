package com.cout970.modeler.gui.react.leguicomp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.component.SelectBox
import org.liquidengine.legui.component.misc.listener.selectbox.SelectBoxElementClickEventListener
import org.liquidengine.legui.event.Event
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.input.Mouse
import org.liquidengine.legui.system.context.Context

/**
 * Created by cout970 on 2017/09/30.
 */
class DropDown(val cmd: String, val x: Float = 0f, val y: Float = 0f, val width: Float = 10f,
               val height: Float = 10f) : SelectBox(x, y, width, height) {

    val selectedIndex get() = elements.indexOf(selection)

    init {
        selectionListPanel.verticalScrollBar.apply {
            backgroundColor = Config.colorPalette.brightestColor.toColor()
            scrollColor = Config.colorPalette.greyColor.toColor()
            arrowColor = Config.colorPalette.whiteColor.toColor()
        }
    }

    override fun addElement(element: String?) {
        super.addElement(element)
        selectBoxElements.forEach {
            it.listenerMap.getListeners(MouseClickEvent::class.java).forEach { listener ->
                it.listenerMap.removeListener(MouseClickEvent::class.java, listener)
            }
            it.listenerMap.addListener(MouseClickEvent::class.java, DropDownClickEventListener(this))
        }
    }

    class DropDownClickEventListener(val box: SelectBox) : SelectBoxElementClickEventListener(box) {

        override fun process(event: MouseClickEvent<*>) {
            val component = event.component as SelectBox.SelectBoxElement
            if (event.action == MouseClickEvent.MouseClickAction.CLICK && event.button == Mouse.MouseButton.MOUSE_BUTTON_1) {
                val old = box.selection
                box.setSelected(component.text, true)
                box.isCollapsed = true
                box.listenerMap.getListeners(DropDownEvent::class.java).forEach {
                    it.process(DropDownEvent(component, event.context, event.frame, box.selection, old))
                }
            }
        }
    }

    class DropDownEvent(
            comp: Component,
            ctx: Context,
            frame: Frame,
            val newSelection: String?,
            val oldSelection: String?
    ) : Event<Component>(comp, ctx, frame)
}