package com.cout970.modeler.newView

import com.cout970.glutilities.device.Mouse
import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IEventController
import com.cout970.modeler.event.IEventListener
import com.cout970.modeler.event.IInput
import com.cout970.modeler.modeleditor.ModelEditor
import com.cout970.modeler.newView.gui.ContentPanel
import com.cout970.modeler.newView.gui.Root
import com.cout970.modeler.newView.gui.Scene
import com.cout970.modeler.newView.selector.Selector
import com.cout970.modeler.newView.viewtarget.TextureViewTarget
import com.cout970.modeler.selection.ElementSelection
import com.cout970.modeler.selection.SelectionMode
import com.cout970.modeler.selection.SelectionState
import com.cout970.modeler.selection.VertexPosSelection
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.isInside
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toRads
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.unaryMinus

/**
 * Created by cout970 on 2017/04/08.
 */

class ViewEventHandler(val root: Root, val contentPanel: ContentPanel, val input: IInput, val modelEditor: ModelEditor,
                       val selector: Selector, val buttonController: ButtonController) {

    private var lastMousePos: IVector2? = null
    private var mousePress = false

    fun update() {
        selector.update()
        if (!input.mouse.isButtonPressed(Mouse.BUTTON_LEFT)) {
            mousePress = false
            lastMousePos = null
            onMouseMove()
        } else if (lastMousePos != null) {
            onDrag(EventMouseDrag(lastMousePos!!, input.mouse.getMousePos()))
        }
    }

    fun registerListeners(eventHandler: IEventController) {
        eventHandler.addListener(EventMouseScroll::class.java, object : IEventListener<EventMouseScroll> {
            override fun onEvent(e: EventMouseScroll): Boolean = onScroll(e)
        })
        eventHandler.addListener(EventKeyUpdate::class.java, object : IEventListener<EventKeyUpdate> {
            override fun onEvent(e: EventKeyUpdate): Boolean = onKey(e)
        })
        eventHandler.addListener(EventMouseClick::class.java, object : IEventListener<EventMouseClick> {
            override fun onEvent(e: EventMouseClick): Boolean = onClick(e)
        })
        modelEditor.selectionManager.listeners.add(this::onSelectionChange)
    }

    private fun onSelectionChange(old: SelectionState, new: SelectionState) {

        val empty = if (modelEditor.selectionManager.selectionMode == SelectionMode.EDIT) {
            new.pos == VertexPosSelection.EMPTY
        } else {
            new.element == ElementSelection.EMPTY
        }
        if (!empty) {
            val center = if (modelEditor.selectionManager.selectionMode == SelectionMode.EDIT) {
                new.pos.center3D(modelEditor.model)
            } else {
                new.element.getSelectedVertexPos(modelEditor.model).center3D(modelEditor.model)
            }
            contentPanel.scenes.forEach {
                it.cursor.apply {
                    enable = true
                    this.center = center
                }
            }
        } else {
            contentPanel.scenes.forEach {
                it.cursor.enable = false
            }
        }
    }

    private fun onMouseMove() {
        val mousePos = input.mouse.getMousePos()
        if (mousePos.isInside(contentPanel.absolutePosition, contentPanel.size.toIVector())) {
            contentPanel.scenes.forEach { scene ->
                if (mousePos.isInside(scene.absolutePosition, scene.size.toIVector())) {
                    contentPanel.selectedScene = scene
                }
            }
        }
    }

    private fun onScroll(e: EventMouseScroll): Boolean {
        val mousePos = input.mouse.getMousePos()
        contentPanel.scenes.forEach { scene ->
            if (mousePos.isInside(scene.absolutePosition, scene.size.toIVector())) {
                scene.run {
                    val camera = cameraHandler.camera
                    val scroll = -e.offsetY * Config.cameraScrollSpeed
                    if (camera.zoom > 0.5 || scroll > 0) {
                        cameraHandler.makeZoom(camera.zoom + scroll * (camera.zoom / 60f))
                    }
                }
            }
        }
        return true
    }

    private var lastOption = 0

    private fun onKey(e: EventKeyUpdate): Boolean {
        if (e.keyState != EnumKeyState.PRESS) return false

        if (root.searchPanel.isEnabled) return false

        contentPanel.selectedScene?.let { selectedScene ->
            if (Config.keyBindings.switchCameraAxis.check(e) && selectedScene.viewTarget.is3d) {
                val handler = selectedScene.cameraHandler
                when (lastOption) {
                    0 -> handler.setRotation(angleX = 0.0, angleY = 0.0)
                    1 -> handler.setRotation(angleX = 0.0, angleY = -90.toRads())
                    2 -> handler.setRotation(angleX = 90.toRads(), angleY = 0.0)
                    3 -> handler.setRotation(angleX = 45.toRads(), angleY = -45.toRads())
                }
                lastOption++
                if (lastOption > 3) {
                    lastOption = 0
                }
                return false
            } else if (Config.keyBindings.switchOrthoProjection.check(e)) {
                if (selectedScene.viewTarget.is3d) {
                    selectedScene.perspective = !selectedScene.perspective
                    return false
                }
            } else if (Config.keyBindings.moveCameraToCursor.check(e)) {
                selectedScene.apply {
                    cameraHandler.moveTo(-selectedScene.cursor.center)
                }
                return false
            }
        }
        when {
            Config.keyBindings.delete.check(input) -> buttonController.onClick("input.delete")
            Config.keyBindings.undo.check(input) -> buttonController.onClick("input.undo")
            Config.keyBindings.redo.check(input) -> buttonController.onClick("input.redo")
            Config.keyBindings.copy.check(input) -> buttonController.onClick("input.copy")
            Config.keyBindings.paste.check(input) -> buttonController.onClick("input.paste")
            Config.keyBindings.cut.check(input) -> buttonController.onClick("input.cut")
            Config.keyBindings.showSearchBar.check(input) -> buttonController.openSearchBar()
        }
        return false
    }

    private fun onClick(e: EventMouseClick): Boolean {
        if (e.keyState == EnumKeyState.PRESS) {
            mousePress = true
            lastMousePos = input.mouse.getMousePos()

            if (contentPanel.scenes.any { onClickScene(it, e) }) {
                return true
            }
        } else if (e.keyState == EnumKeyState.RELEASE) {
            mousePress = false
            lastMousePos = null
        }

        return false
    }

    private var lastJumpClickTime = 0L
    private fun onClickScene(scene: Scene, e: EventMouseClick): Boolean {
        val mousePos = input.mouse.getMousePos()
        if (Config.keyBindings.selectModel.check(e)) {
            if (mousePos.isInside(scene.absolutePosition, scene.size.toIVector())) {
                if (scene.viewTarget.hoveredObject == null &&
                    scene.viewTarget.selectedObject == null) {
                    val handler = scene.viewTarget
                    if (handler.is3d) {
                        modelEditor.selectionManager.selectPos(
                                selector.getMouseSpaceContext(scene, mousePos).mouseRay,
                                scene.cameraHandler.camera.zoom.toFloat(),
                                Config.keyBindings.multipleSelection.check(input)
                        )
                    }
                    if (handler is TextureViewTarget) {
                        modelEditor.selectionManager.selectTex(
                                selector.getMouseSpaceContext(scene, mousePos).mouseRay,
                                scene.cameraHandler.camera.zoom.toFloat(),
                                Config.keyBindings.multipleSelection.check(input),
                                { vec -> handler.fromTextureToWorld(vec) }
                        )
                    }
                    return true
                }
            }
        }

        if (Config.keyBindings.jumpCameraToCursor.check(e)) {
            if (mousePos.isInside(scene.absolutePosition, scene.size.toIVector())) {
                if (System.currentTimeMillis() - lastJumpClickTime < 500) {

                    val ray = selector.getMouseSpaceContext(scene, mousePos).mouseRay
                    val hit = modelEditor.selectionManager.getMouseHit(ray)

                    if (hit != null) {
                        scene.cameraHandler.moveTo(-hit.hit)
                        return true
                    }
                }
                lastJumpClickTime = System.currentTimeMillis()
            }
        }
        return false
    }

    private fun onDrag(e: EventMouseDrag) {
        selector.onDrag(contentPanel.controllerState, e)
    }
}