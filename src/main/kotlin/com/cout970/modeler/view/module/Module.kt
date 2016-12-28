package com.cout970.modeler.view.module

import com.cout970.modeler.view.controller.ModuleController
import org.joml.Vector2f
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.component.MouseClickEvent
import org.liquidengine.legui.listener.LeguiEventListener

/**
 * Created by cout970 on 2016/12/27.
 */
abstract class Module(val controller: ModuleController, val name: String) : Panel() {

    val label: Label
    val minimizeButton: Button
    val subPanel: Panel

    init {
        label = Label(name).apply { this@Module.addComponent(this) }
        minimizeButton = Button().apply { this@Module.addComponent(this) }
        subPanel = Panel().apply { this@Module.addComponent(this); border.isEnabled = false }

        position = Vector2f(5f, 0f)

        label.textState.horizontalAlign = HorizontalAlign.CENTER
        label.size = Vector2f(180f, 20f)

        minimizeButton.position = Vector2f(165f, 5f)

        minimizeButton.leguiEventListeners.addListener(MouseClickEvent::class.java, LeguiEventListener {
            if (it.action == MouseClickEvent.MouseClickAction.CLICK) {
                if (subPanel.isEnabled) {
                    minimize()
                } else {
                    maximize()
                }
                controller.viewManager.recalculateModules()
            }
        })

        maximize()
    }

    fun minimize() {
        size = Vector2f(180f, 20f)
        subPanel.isEnabled = false
        minimizeButton.textState.text = ">"
    }

    fun maximize() {
        subPanel.isEnabled = true
        minimizeButton.textState.text = "V"
        size = Vector2f(180f, 20f)

        subPanel.position.y = 20f
        subPanel.position.x = 1f
        subPanel.size = Vector2f(178f, 0f)
        for (component in subPanel.components) {
            subPanel.size.y = Math.max(subPanel.size.y, component.position.y + component.size.y)
            size.y = subPanel.size.y + 25f
        }
    }

    fun addSubComponent(component: Component) {
        subPanel.size.y = Math.max(subPanel.size.y, component.position.y + component.size.y)
        size.y = subPanel.size.y + 25f
        subPanel.addComponent(component)
    }

    fun buttonListener(id: Int) = Listener(controller, id)

    class Listener(val controller: ModuleController, val id: Int) : LeguiEventListener<MouseClickEvent> {
        override fun update(e: MouseClickEvent) {
            if (e.action == MouseClickEvent.MouseClickAction.CLICK)
                controller.onButtonPress(id)
        }
    }
}