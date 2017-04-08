package com.cout970.modeler.view.module

import com.cout970.modeler.config.Config
import com.cout970.modeler.newView.gui.comp.CBorderRenderer
import com.cout970.modeler.util.toColor
import com.cout970.modeler.view.controller.ButtonController
import com.cout970.modeler.view.controller.ModuleController
import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Widget
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.component.MouseClickEvent
import org.liquidengine.legui.listener.LeguiEventListener

/**
 * Created by cout970 on 2016/12/27.
 */

abstract class Module(val controller: ModuleController, val name: String) : Widget() {

    init {
        title.textState.text = name
        container.border.isEnabled = false
        closeButton.isVisible = false
        isDraggable = false
        position = Vector2f(0f, 0f)

        title.textState.horizontalAlign = HorizontalAlign.CENTER
        title.size = Vector2f(190f, 20f)

        minimizeButton.position = Vector2f(170f, 0f)
        minimizeButton.size = Vector2f(16f, 16f)
        minimizeButton.backgroundColor = Config.colorPalette.buttonColor.toColor()
        minimizeButton.leguiEventListeners.addListener(MouseClickEvent::class.java, {
            if (it.action == MouseClickEvent.MouseClickAction.CLICK) {
                if (container.isEnabled) {
                    minimize()
                } else {
                    maximize()
                }
                controller.recalculateModules()
            }
        })

        border.renderer = CBorderRenderer
        backgroundColor = Config.colorPalette.darkColor.toColor()
        container.backgroundColor = Config.colorPalette.primaryColor.toColor()
        title.textState.textColor = Config.colorPalette.textColor.toColor()
    }

    open fun tick() {}

    fun minimize() {
        container.isVisible = false
        container.isEnabled = false
        size = Vector2f(190f, 21f)
        resize()
    }

    fun maximize() {
        container.isEnabled = true
        container.isVisible = true
        size = Vector2f(190f, 21f)

        container.position = Vector2f(0f, 21f)
        container.size = Vector2f(190f, 0f)
        if (container.components.isNotEmpty()) {
            for (component in container.components) {
                container.size.y = Math.max(container.size.y, component.position.y + component.size.y)
            }
            container.size.y += 10f
            size.y = container.size.y + 15f
        }
        resize()
    }

    fun addSubComponent(component: Component) {
        container.addComponent(component)
    }

    fun buttonListener(id: String) = ButtonListener(controller.buttonController, id)

    fun propertyBind(id: String) = controller.buttonController.getBindProperty(id)

    open class ButtonListener(val controller: ButtonController, val id: String) : LeguiEventListener<MouseClickEvent> {
        override fun update(e: MouseClickEvent) {
            if (e.action == MouseClickEvent.MouseClickAction.CLICK)
                controller.onClick(id)
        }
    }
}