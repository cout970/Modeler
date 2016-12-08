package com.cout970.modeler.render.layout

import com.cout970.matrix.extensions.times
import com.cout970.modeler.render.RenderManager
import com.cout970.modeler.render.controller.Camera
import com.cout970.modeler.render.controller.ViewControllerModelEdit
import com.cout970.modeler.util.toIMatrix
import com.cout970.vector.extensions.vec2Of
import org.joml.*
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.ScrollablePanel
import org.liquidengine.legui.event.component.MouseClickEvent
import org.liquidengine.legui.listener.LeguiEventListener

/**
 * Created by cout970 on 2016/12/03.
 */
class LayoutModelEdit(renderManager: RenderManager) : Layout(renderManager) {

    val sidePanel = Panel()
    val modelPanel = Panel()
    val controlPanel = ScrollablePanel()
    override val contentPanel = Panel()
    val controlPanelLength: Float
    var camera = Camera.DEFAULT
    var zoom = -10f
    override val viewController = ViewControllerModelEdit(this)

    init {
        modelPanel.apply {
            border.isEnabled = false
            backgroundColor = Vector4f(0.73f, 0.9f, 1f, 1f)
        }
        contentPanel.addComponent(sidePanel)
        contentPanel.addComponent(modelPanel)

        var line = 5f
        sidePanel.addComponent(controlPanel)
        controlPanel.let { sidePanel ->
            sidePanel.horizontalScrollBar.isVisible = false
            sidePanel.container.let { sidePanel ->
                //selection mode
                sidePanel.addComponent(Label(10f, line, 160f, 20f, "Selection Mode"))
                line += 25f
                sidePanel.addComponent(Button(10f, line, 40f, 40f, "").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 0))
                })
                sidePanel.addComponent(Button(50f, line, 40f, 40f, "").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 1))
                })
                sidePanel.addComponent(Button(90f, line, 40f, 40f, "").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 2))
                })
                sidePanel.addComponent(Button(130f, line, 40f, 40f, "").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 3))
                })
                line += 50f
                sidePanel.addComponent(Button(10f, line, 80f, 20f, "Cube").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 4))
                })
                sidePanel.addComponent(Button(90f, line, 80f, 20f, "Plane").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 5))
                })
                line += 25f
                sidePanel.addComponent(Button(10f, line, 80f, 20f, "Mesh").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 6))
                })
                sidePanel.addComponent(Button(90f, line, 80f, 20f, "Submodel").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 7))
                })
                line += 30f
                sidePanel.addComponent(Button(10f, line, 80f, 20f, "Undo").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 8))
                })
                sidePanel.addComponent(Button(90f, line, 80f, 20f, "Redo").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 9))
                })
                line += 25f
                sidePanel.addComponent(Button(10f, line, 50f, 20f, "Copy").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 10))
                })
                sidePanel.addComponent(Button(65f, line, 50f, 20f, "Cut").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 11))
                })
                sidePanel.addComponent(Button(120f, line, 50f, 20f, "Paste").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 12))
                })
                line += 30f
                sidePanel.addComponent(Button(10f, line, 160f, 20f, "Translate").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 13))
                })
                line += 25f
                sidePanel.addComponent(Button(10f, line, 160f, 20f, "Rotate").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 14))
                })
                line += 25f
                sidePanel.addComponent(Button(10f, line, 160f, 20f, "Scale").apply {
                    leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 15))
                })
                line += 30f
            }
        }

        controlPanelLength = line

        line += 25f
        sidePanel.addComponent(Button(10f, line, 160f, 20f, "Grids").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 1))
        })
        line += 25f
        sidePanel.addComponent(Button(10f, line, 160f, 20f, "Light").apply {
            leguiEventListeners.addListener(MouseClickEvent::class.java, Listener(this@LayoutModelEdit, 1))
        })
        line += 25f
    }

    override fun renderExtras() {
        //update sizes relatives to the window size
        sidePanel.position = Vector2f()
        sidePanel.size = Vector2f(190f, contentPanel.size.y)

        //control panel external
        controlPanel.position = Vector2f(0f, 1f)
        controlPanel.size = Vector2f(190f, contentPanel.size.y)
        //control panel internal
        controlPanel.container.size = Vector2f(180f, controlPanelLength)
        controlPanel.verticalScrollBar.isVisible = controlPanelLength > controlPanel.size.y
        controlPanel.resize()

        //model render
        modelPanel.position = Vector2f(190f, 0f)
        modelPanel.size = Vector2f(contentPanel.size.x - 190, contentPanel.size.y)

        val pos = modelPanel.position
        val size = modelPanel.size
        //if the model window is less than 1x1 do not render it.
        if (size.x < 1 || size.y < 1) return

        viewController.update()

        renderManager.modelRenderer.run {
            matrixP = Matrix4d().setPerspective(Math.toRadians(60.0), (modelPanel.size.x / modelPanel.size.y).toDouble(), 0.001, 1000.0).toIMatrix()
            matrixV = Matrix4d().apply { translate(Vector3d(0.0, 0.0, zoom.toDouble())) }.toIMatrix() * camera.matrix
            if (renderManager.modelController.modelUpdate) {
                renderManager.modelController.modelUpdate = false
                cache.clear()
            }
            start(vec2Of(pos.x, pos.y), vec2Of(size.x, size.y))
            render(renderManager.modelController.model)
            startSelection()
            renderSelection(renderManager.modelController.model, renderManager.modelController.selectionManager)
            stop()
        }
    }

    override fun onLoad() {
        super.onLoad()
        viewController.enableControl = true
    }

    override fun onRemove() {
        super.onRemove()
        viewController.enableControl = false
    }

    class Listener(val layout: LayoutModelEdit, val id: Int) : LeguiEventListener<MouseClickEvent> {
        override fun update(e: MouseClickEvent) {
            if (!layout.viewController.enableControl) return
            if (e.action == MouseClickEvent.MouseClickAction.CLICK)
                layout.viewController.onButtonPress(id)
        }
    }
}
