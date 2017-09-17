package com.cout970.modeler.gui

import com.cout970.glutilities.event.*
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.input.event.EventController
import com.cout970.modeler.render.tool.camera.CameraUpdater
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.isInside
import com.cout970.modeler.util.toIVector
import org.liquidengine.legui.component.TextInput

/**
 * Created by cout970 on 2017/05/16.
 */
class Listeners : ITickeable {

    private lateinit var gui: Gui
    lateinit var cameraUpdater: CameraUpdater

    fun initListeners(eventController: EventController, gui: Gui) {
        this.gui = gui
        eventController.addListener(EventKeyUpdate::class.java, this::onKeyPress)
        eventController.addListener(EventFrameBufferSize::class.java, gui.guiUpdater::onFramebufferSizeUpdated)
        eventController.addListener(EventMouseScroll::class.java, this::onMouseScroll)
        cameraUpdater = CameraUpdater(gui.canvasContainer, eventController, gui.timer)
        gui.root.updateSizes(gui.windowHandler.window.size)
        gui.projectManager.modelChangeListeners += this::onModelChange
        eventController.addListener(EventMouseClick::class.java, gui.canvasManager::onMouseClick)

        gui.projectManager.modelChangeListeners.add { _, new ->
            gui.state.modelHash = new.hashCode()
            gui.state.visibilityHash = new.visibilities.hashCode()
        }
        gui.selectionHandler.listeners.add { _, _ ->
            gui.state.modelSelectionHash = (gui.selectionHandler.lastModified and 0xFFFFFFFF).toInt()
            gui.state.textureSelectionHash = (gui.selectionHandler.lastModified and 0xFFFFFFFF).toInt()
        }
        gui.projectManager.materialChangeListeners.add { _, _ ->
            gui.state.materialsHash = (System.currentTimeMillis() and 0xFFFFFFFF).toInt()
        }
    }

    fun onModelChange(old: IModel, new: IModel) {
        gui.guiUpdater.onModelUpdate(old, new)
    }

    fun onMouseScroll(e: EventMouseScroll): Boolean {
        val mousePos = gui.input.mouse.getMousePos()
        gui.canvasContainer.canvas.forEach { canvas ->
            if (mousePos.isInside(canvas.absolutePosition, canvas.size.toIVector())) {
                canvas.run {
                    val camera = canvas.cameraHandler.camera
                    val scroll = -e.offsetY * Config.cameraScrollSpeed
                    if (camera.zoom > 0.5 || scroll > 0) {
                        canvas.cameraHandler.setZoom(camera.zoom + scroll * (camera.zoom / 60f))
                    }
                }
                return true
            }
        }
        if (gui.guiUpdater.handleScroll(e))
            return true
        return false
    }

    fun onKeyPress(e: EventKeyUpdate): Boolean {
        if (e.keyState != EnumKeyState.PRESS) return false
        if (gui.canvasContainer.layout.onEvent(gui, e)) return true
        if (gui.guiUpdater.leguiContext.focusedGui is TextInput) return false
        return gui.keyboardBinder.onEvent(e)
    }

    override fun tick() {
        cameraUpdater.updateCameras()
        gui.canvasManager.update()
    }
}