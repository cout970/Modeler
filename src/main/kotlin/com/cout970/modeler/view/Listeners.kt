package com.cout970.modeler.view

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventFrameBufferSize
import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.view.event.EventController
import com.cout970.modeler.view.gui.camera.CameraUpdater

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
        cameraUpdater = CameraUpdater(guiState.canvasContainer, eventController, guiState.timer)
        guiState.guiUpdater.updateSizes(guiState.windowHandler.window.size)
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