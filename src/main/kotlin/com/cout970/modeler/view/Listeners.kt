package com.cout970.modeler.view

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventFrameBufferSize
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.glutilities.event.EventMouseScroll
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.isInside
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.event.EventController
import com.cout970.modeler.view.render.tool.camera.CameraUpdater

/**
 * Created by cout970 on 2017/05/16.
 */
class Listeners : ITickeable {

    private lateinit var guiState: GuiState
    lateinit var cameraUpdater: CameraUpdater

    fun initListeners(eventController: EventController, guiState: GuiState) {
        this.guiState = guiState
        eventController.addListener(EventKeyUpdate::class.java, this::onKeyPress)
        eventController.addListener(EventFrameBufferSize::class.java, guiState.guiUpdater::onFramebufferSizeUpdated)
        eventController.addListener(EventMouseScroll::class.java, this::onMouseScroll)
        cameraUpdater = CameraUpdater(guiState.canvasContainer, eventController, guiState.timer)
        guiState.guiUpdater.updateSizes(guiState.windowHandler.window.size)
    }

    fun onMouseScroll(e: EventMouseScroll): Boolean {
        val mousePos = guiState.input.mouse.getMousePos()
        guiState.canvasContainer.canvas.forEach { canvas ->
            if (mousePos.isInside(canvas.absolutePosition, canvas.size.toIVector())) {
                canvas.run {
                    val camera = canvas.state.cameraHandler.camera
                    val scroll = -e.offsetY * Config.cameraScrollSpeed
                    if (camera.zoom > 0.5 || scroll > 0) {
                        canvas.state.cameraHandler.setZoom(camera.zoom + scroll * (camera.zoom / 60f))
                    }
                }
            }
        }
        return false
    }

    fun onKeyPress(e: EventKeyUpdate): Boolean {
        return if (e.keyState == EnumKeyState.PRESS) {
            guiState.canvasContainer.layout.onEvent(guiState, e)
        } else false
    }

    override fun tick() {
        cameraUpdater.updateCameras()
    }
}