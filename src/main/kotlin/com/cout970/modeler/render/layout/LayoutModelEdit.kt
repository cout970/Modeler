package com.cout970.modeler.render.layout

import com.cout970.glutilities.device.Keyboard
import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.render.RenderManager
import com.cout970.modeler.render.controller.IViewController
import com.cout970.modeler.render.controller.LambdaViewController
import com.cout970.modeler.util.toIMatrix
import com.cout970.vector.extensions.vec2Of
import org.joml.Matrix4d
import org.joml.Vector2f
import org.joml.Vector3d
import org.joml.Vector4f
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.Label
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.ScrollablePanel

/**
 * Created by cout970 on 2016/12/03.
 */
class LayoutModelEdit(renderManager: RenderManager) : Layout(renderManager) {

    val sidePanel = ScrollablePanel()
    val modelPanel = Panel()
    override val contentPanel = Panel()
    val sidePanelLength: Float

    init {
        modelPanel.apply {
            border.isEnabled = false
            backgroundColor = Vector4f(0.73f, 0.9f, 1f, 1f)
        }
        contentPanel.addComponent(sidePanel)
        contentPanel.addComponent(modelPanel)

        var line = 5f
        sidePanel.horizontalScrollBar.isVisible = false
        sidePanel.container.let { sidePanel ->
            //selection mode
            sidePanel.addComponent(Label(10f, line, 160f, 20f, "Selection Mode"))
            line += 25f
            sidePanel.addComponent(Button(10f, line, 40f, 40f, ""))
            sidePanel.addComponent(Button(50f, line, 40f, 40f, ""))
            sidePanel.addComponent(Button(90f, line, 40f, 40f, ""))
            sidePanel.addComponent(Button(130f, line, 40f, 40f, ""))
            line += 50f
            sidePanel.addComponent(Button(10f, line, 80f, 20f, "Cube"))
            sidePanel.addComponent(Button(90f, line, 80f, 20f, "Plane"))
            line += 25f
            sidePanel.addComponent(Button(10f, line, 80f, 20f, "Mesh"))
            sidePanel.addComponent(Button(90f, line, 80f, 20f, ""))
            line += 30f
            sidePanel.addComponent(Button(10f, line, 80f, 20f, "Undo"))
            sidePanel.addComponent(Button(90f, line, 80f, 20f, "Redo"))
            line += 25f
            sidePanel.addComponent(Button(10f, line, 50f, 20f, "Copy"))
            sidePanel.addComponent(Button(65f, line, 50f, 20f, "Cut"))
            sidePanel.addComponent(Button(120f, line, 50f, 20f, "Paste"))
            line += 30f
            sidePanel.addComponent(Button(10f, line, 160f, 20f, "Translate"))
            line += 25f
            sidePanel.addComponent(Button(10f, line, 160f, 20f, "Rotate"))
            line += 25f
            sidePanel.addComponent(Button(10f, line, 160f, 20f, "Scale"))
            line += 30f
            sidePanel.addComponent(Button(10f, line, 160f, 20f, "Grids"))
            line += 25f
            sidePanel.addComponent(Button(10f, line, 160f, 20f, "Light"))
            line += 25f
        }
        sidePanelLength = line
    }

    var x = 0f
    var y = 0f
    var zoom = -10f

    override fun renderExtras() {
        //update sizes relatives to the window size
        val border = sidePanelLength > contentPanel.size.y / 2
        sidePanel.position = Vector2f()
        sidePanel.size = Vector2f(180f + if (border) 10 else 0, contentPanel.size.y / 2)
        sidePanel.container.size = Vector2f(180f, sidePanelLength)
        sidePanel.verticalScrollBar.isVisible = border
        sidePanel.resize()
        modelPanel.position = Vector2f(180f + if (border) 10 else 0, 0f)
        modelPanel.size = Vector2f(contentPanel.size.x - (180 + if (border) 10 else 0), contentPanel.size.y)
        val pos = modelPanel.position
        val size = modelPanel.size
        //if the model window is less than 1x1 do not render it.
        if (size.x < 1 || size.y < 1) return

        renderManager.modelRenderer.run {
            matrixP = Matrix4d().setPerspective(Math.toRadians(60.0), (modelPanel.size.x / modelPanel.size.y).toDouble(), 0.001, 1000.0).toIMatrix()
            matrixM = Matrix4d().apply {
                translate(Vector3d(0.0, 0.0, zoom.toDouble()))
                rotate(x.toDouble(), Vector3d(1.0, 0.0, 0.0))
                rotate(y.toDouble(), Vector3d(0.0, 1.0, 0.0))
            }.toIMatrix()

            start(vec2Of(pos.x, pos.y), vec2Of(size.x, size.y))
            render(renderManager.modelController.model)
            stop()
        }
    }

    override val viewController: IViewController = LambdaViewController { eventController ->

        eventController.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean {
                if (e.keyState == EnumKeyState.RELEASE) return false
                if (e.keycode == Keyboard.KEY_DOWN) {
                    x += 0.05f
                } else if (e.keycode == Keyboard.KEY_UP) {
                    x -= 0.05f
                }
                if (e.keycode == Keyboard.KEY_RIGHT) {
                    y += 0.05f
                } else if (e.keycode == Keyboard.KEY_LEFT) {
                    y -= 0.05f
                }
                if (e.keycode == Keyboard.KEY_Q) {
                    zoom += 0.5f
                } else if (e.keycode == Keyboard.KEY_E) {
                    zoom -= 0.5f
                }
                if (e.keycode == Keyboard.KEY_P) {
                    renderManager.modelRenderer.cache.clear()
                }
                return false
            }
        })
    }
}