package com.cout970.modeler.functional.binders

import com.cout970.modeler.functional.Dispatcher
import com.cout970.modeler.view.gui.comp.CButton
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Container
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.listener.ListenerMap

/**
 * Created by cout970 on 2017/07/17.
 */
class ButtonBinder(val dispatcher: Dispatcher) {

    fun onButtonPress(str: String, comp: Component?) {
        dispatcher.onEvent(str, comp)
    }

    fun bindButtons(panel: Container<*>) {
        panel.childs.forEach {
            if (it is CButton) {
                it.listenerMap.setButtonListener { onButtonPress(it.command, it) }
            } else if (it is Container<*>) {
                bindButtons(it)
            }
        }
    }

    fun ListenerMap.setButtonListener(function: () -> Unit) {
        getListeners(MouseClickEvent::class.java).toList().forEach {
            removeListener(MouseClickEvent::class.java, it)
        }
        addListener(MouseClickEvent::class.java,
                { if (it.action == MouseClickEvent.MouseClickAction.CLICK) function() })
    }
}