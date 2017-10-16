package com.cout970.modeler.gui

import com.cout970.glutilities.event.*
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionTarget
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.gui.react.event.EventMaterialUpdate
import com.cout970.modeler.gui.react.event.EventModelUpdate
import com.cout970.modeler.gui.react.event.EventSelectionUpdate
import com.cout970.modeler.input.event.EventController
import com.cout970.modeler.render.tool.camera.CameraUpdater
import com.cout970.modeler.util.*
import com.cout970.vector.extensions.vec2Of
import org.liquidengine.legui.component.TextInput

/**
 * Created by cout970 on 2017/05/16.
 */
class Listeners : ITickeable {

    private lateinit var gui: Gui
    lateinit var cameraUpdater: CameraUpdater

    fun initListeners(eventController: EventController, projectManager: ProjectManager, gui: Gui) {
        this.gui = gui
        eventController.addListener(EventKeyUpdate::class.java, this::onKeyPress)
        eventController.addListener(EventFrameBufferSize::class.java, this::onFramebufferSizeUpdated)
        eventController.addListener(EventMouseScroll::class.java, this::onMouseScroll)
        cameraUpdater = CameraUpdater(gui.canvasContainer, eventController, gui.timer)
        gui.root.updateSizes(gui.windowHandler.window.size)
        projectManager.modelChangeListeners += this::onModelChange
        eventController.addListener(EventMouseClick::class.java, gui.canvasManager::onMouseClick)

        projectManager.modelChangeListeners.add { _, new ->
            gui.state.modelHash = new.hashCode()
            gui.state.visibilityHash = new.visibilities.hashCode()
        }
        gui.modelAccessor.modelSelectionHandler.addChangeListener { _, _ ->
            gui.state.modelSelectionHash = (gui.modelAccessor.modelSelectionHandler.lastModified and 0xFFFFFFFF).toInt()
            gui.state.textureSelectionHash = (gui.modelAccessor.modelSelectionHandler.lastModified and 0xFFFFFFFF).toInt()
        }
        projectManager.materialChangeListeners.add { _, _ ->
            gui.state.materialsHash = (System.currentTimeMillis() and 0xFFFFFFFF).toInt()
        }

        projectManager.modelChangeListeners.add(gui.canvasManager::onModelUpdate)
        projectManager.materialChangeListeners.add(this::onMaterialUpdate)
        gui.modelAccessor.modelSelectionHandler.addChangeListener(this::onSelectionUpdate)
        gui.modelAccessor.modelSelectionHandler.addChangeListener(gui.canvasManager::onSelectionUpdate)
    }

    fun onFramebufferSizeUpdated(event: EventFrameBufferSize): Boolean {
        if (event.height == 0 || event.width == 0) return false
        gui.root.updateSizes(vec2Of(event.width, event.height))
        return false
    }

    fun onModelChange(old: IModel, new: IModel) {
        gui.editorPanel.reactBase.getListeners<EventModelUpdate>().forEach { (comp, listener) ->
            listener.process(EventModelUpdate(comp, gui.root.context, gui.root, new, old))
        }
    }

    fun onSelectionUpdate(old: Nullable<ISelection>, new: Nullable<ISelection>) {
        gui.editorPanel.reactBase.getListeners<EventSelectionUpdate>().forEach { (comp, listener) ->
            listener.process(EventSelectionUpdate(comp, gui.root.context, gui.root, new, old))
        }
    }

    fun onMaterialUpdate(old: IMaterial?, new: IMaterial?) {
        gui.editorPanel.reactBase.getListeners<EventMaterialUpdate>().forEach { (comp, listener) ->
            listener.process(EventMaterialUpdate(comp, gui.root.context, gui.root, new.asNullable(), old.asNullable()))
        }
    }

    fun onMouseScroll(e: EventMouseScroll): Boolean {
        val mousePos = gui.input.mouse.getMousePos()
        gui.canvasContainer.canvas.forEach { canvas ->
            if (mousePos.isInside(canvas.absolutePositionV, canvas.size.toIVector())) {
                canvas.run {
                    val camera = canvas.cameraHandler.camera
                    val scroll = -e.offsetY * Config.cameraScrollSpeed * if (canvas.viewMode == SelectionTarget.TEXTURE) 2.0 else 1.0
                    if (camera.zoom > 0.5 || scroll > 0) {
                        canvas.cameraHandler.setZoom(camera.zoom + scroll * (camera.zoom / 60f))
                    }
                }
                return true
            }
        }
        return false
    }

    fun onKeyPress(e: EventKeyUpdate): Boolean {
        if (e.keyState != EnumKeyState.PRESS) return false
        if (gui.canvasContainer.layout.onEvent(gui, e)) return true
        if (gui.root.context.focusedGui is TextInput) return false
        return gui.keyboardBinder.onEvent(e)
    }

    override fun tick() {
        cameraUpdater.updateCameras()
        gui.canvasManager.update()
    }
}