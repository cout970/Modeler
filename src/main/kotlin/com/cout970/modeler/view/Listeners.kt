package com.cout970.modeler.view

import com.cout970.glutilities.event.*
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.ITickeable
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.isInside
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.event.EventController
import com.cout970.modeler.view.gui.comp.canvas.Canvas
import com.cout970.modeler.view.render.tool.camera.CameraUpdater

/**
 * Created by cout970 on 2017/05/16.
 */
class Listeners : ITickeable {

    private lateinit var gui: Gui
    lateinit var cameraUpdater: CameraUpdater
    lateinit var hotKeyHandler: HotKeyHandler

    fun initListeners(eventController: EventController, gui: Gui) {
        this.gui = gui
        this.hotKeyHandler = HotKeyHandler(gui.commandExecutor)
        eventController.addListener(EventKeyUpdate::class.java, this::onKeyPress)
        eventController.addListener(EventFrameBufferSize::class.java, gui.guiUpdater::onFramebufferSizeUpdated)
        eventController.addListener(EventMouseScroll::class.java, this::onMouseScroll)
        eventController.addListener(EventMouseClick::class.java, this::onMouseClick)
        cameraUpdater = CameraUpdater(gui.canvasContainer, eventController, gui.timer)
        gui.root.updateSizes(gui.windowHandler.window.size)
        gui.projectManager.modelChangeListeners += this::onModelChange
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
        return if (e.keyState == EnumKeyState.PRESS) {
            val ret = gui.canvasContainer.layout.onEvent(gui, e)
            if (ret) {
                true
            } else {
                if (gui.guiUpdater.leguiContext.focusedGui == null ||
                    gui.guiUpdater.leguiContext.focusedGui is Canvas) {

                    hotKeyHandler.onPress(e)
                } else {
                    false
                }
            }
        } else false
    }

    fun onMouseClick(e: EventMouseClick): Boolean {
        gui.canvasContainer.canvas.forEach { canvas ->
            val pos = gui.input.mouse.getMousePos()
            if (pos.isInside(canvas.absolutePosition, canvas.size.toIVector())) {
                gui.selector.onClick(e, canvas)
                return true
            }
        }
        return false
    }

    override fun tick() {
        cameraUpdater.updateCameras()
        gui.selector.update(gui.canvasContainer, gui.actionExecutor.actionTrigger)
    }
}