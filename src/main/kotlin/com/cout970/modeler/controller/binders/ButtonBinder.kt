package com.cout970.modeler.controller.binders

import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.gui.leguicomp.IconButton
import com.cout970.modeler.gui.leguicomp.TextButton
import com.cout970.modeler.util.isNotEmpty
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.listener.ListenerMap

/**
 * Created by cout970 on 2017/07/17.
 */
class ButtonBinder(val dispatcher: Dispatcher) {

    fun onButtonPress(str: String, comp: Component?) {
        dispatcher.onEvent(str, comp)
    }

    fun bindButtons(panel: Component) {
        panel.childs.forEach {
            when (it) {
                is IconButton -> {
                    if (it.command != "") {
                        log(Level.FINEST) { "Binding button: ${it.command} in $it" }
                        it.listenerMap.setButtonListener { onButtonPress(it.command, it) }
                    }
                }
                is TextButton -> {
                    if (it.command != "") {
                        log(Level.FINEST) { "Binding button: ${it.command} in $it" }
                        it.listenerMap.setButtonListener { onButtonPress(it.command, it) }
                    }
                }
                else -> if (it.isNotEmpty) bindButtons(it)
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