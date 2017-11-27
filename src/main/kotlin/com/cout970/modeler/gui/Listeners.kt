package com.cout970.modeler.gui

import com.cout970.glutilities.event.*
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.gui.event.EventMaterialUpdate
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.input.event.EventController
import com.cout970.modeler.render.tool.camera.CameraUpdater
import com.cout970.modeler.util.*
import com.cout970.vector.api.IVector2
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
        eventController.let {
            it.addListener(EventKeyUpdate::class.java, this::onKeyPress)
            it.addListener(EventFrameBufferSize::class.java, this::onFramebufferSizeUpdated)
            it.addListener(EventMouseScroll::class.java, this::onMouseScroll)
            it.addListener(EventMouseClick::class.java, gui.canvasManager::onMouseClick)

            cameraUpdater = CameraUpdater(gui.canvasContainer, it, gui.timer)
        }

        projectManager.let {
            it.modelChangeListeners += this::onModelChange
            it.modelChangeListeners.add { _, new ->
                gui.state.modelHash = new.hashCode()
                gui.state.visibilityHash = new.visibilities.hashCode()
            }

            it.modelSelectionHandler.addChangeListener { _, _ ->
                gui.state.modelSelectionHash = (gui.modelAccessor.modelSelectionHandler.lastModified and 0xFFFFFFFF).toInt()
                gui.state.textureSelectionHash = (gui.modelAccessor.modelSelectionHandler.lastModified and 0xFFFFFFFF).toInt()
            }

            it.materialChangeListeners.add { _, _ ->
                gui.state.materialsHash = (System.currentTimeMillis() and 0xFFFFFFFF).toInt()
            }

            it.modelChangeListeners.add(gui.canvasManager::onModelUpdate)
            it.materialChangeListeners.add(this::onMaterialUpdate)

            it.modelSelectionHandler.addChangeListener(this::onSelectionUpdate)
            it.modelSelectionHandler.addChangeListener(gui.canvasManager::onSelectionUpdate)
        }
    }

    private var sizeUpdate: IVector2? = null

    fun onFramebufferSizeUpdated(event: EventFrameBufferSize): Boolean {
        if (event.height == 0 || event.width == 0) return false
        sizeUpdate = vec2Of(event.width, event.height)
        return false
    }

    fun onModelChange(old: IModel, new: IModel) {
        gui.editorView.base.getListeners<EventModelUpdate>().forEach { (comp, listener) ->
            listener.process(EventModelUpdate(comp, gui.root.context, gui.root, new, old))
        }
    }

    fun onSelectionUpdate(old: Nullable<ISelection>, new: Nullable<ISelection>) {
        gui.editorView.base.getListeners<EventSelectionUpdate>().forEach { (comp, listener) ->
            listener.process(EventSelectionUpdate(comp, gui.root.context, gui.root, new, old))
        }
    }

    fun onMaterialUpdate(old: IMaterial?, new: IMaterial?) {
        gui.editorView.base.getListeners<EventMaterialUpdate>().forEach { (comp, listener) ->
            listener.process(EventMaterialUpdate(comp, gui.root.context, gui.root, new.asNullable(), old.asNullable()))
        }
    }

    fun onMouseScroll(e: EventMouseScroll): Boolean {
        val mousePos = gui.input.mouse.getMousePos()
        gui.canvasContainer.canvas.forEach { canvas ->
            if (mousePos.isInside(canvas.absolutePositionV, canvas.size.toIVector())) {
                cameraUpdater.updateZoom(canvas, e)
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
        sizeUpdate?.let {
            gui.root.updateSizes(it)
            gui.root.context.updateGlfwWindow()
            gui.windowHandler.resetViewport()
            sizeUpdate = null
        }
    }
}