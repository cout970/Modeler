package com.cout970.modeler.gui

import com.cout970.glutilities.event.*
import com.cout970.modeler.api.model.IModel
import com.cout970.modeler.api.model.material.IMaterial
import com.cout970.modeler.api.model.selection.ISelection
import com.cout970.modeler.api.model.selection.SelectionType
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.gui.event.*
import com.cout970.modeler.gui.reactive.RComponentWrapper
import com.cout970.modeler.gui.reactive.RContext
import com.cout970.modeler.input.event.EventController
import com.cout970.modeler.render.tool.Animator
import com.cout970.modeler.render.tool.camera.CameraUpdater
import com.cout970.modeler.util.*
import com.cout970.reactive.core.AsyncManager
import com.cout970.reactive.core.SyncManager
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.event.Event
import org.liquidengine.legui.system.context.Context

/**
 * Created by cout970 on 2017/05/16.
 */
class Listeners : ITickeable, IGuiCmdRunner {

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

        AsyncManager.setInstance(SyncManager)

        projectManager.let {
            it.modelChangeListeners += this::onModelChange
            it.modelChangeListeners.add { _, new ->
                gui.state.modelHash = new.hashCode()
            }

            it.modelSelectionHandler.addChangeListener { _, _ ->
                gui.state.modelSelectionHash = (gui.modelAccessor.modelSelectionHandler.lastModified and 0xFFFFFFFF).toInt()
                gui.state.textureSelectionHash = (gui.modelAccessor.textureSelectionHandler.lastModified and 0xFFFFFFFF).toInt()
            }

            it.textureSelectionHandler.addChangeListener { _, _ ->
                gui.state.modelSelectionHash = (gui.modelAccessor.modelSelectionHandler.lastModified and 0xFFFFFFFF).toInt()
                gui.state.textureSelectionHash = (gui.modelAccessor.textureSelectionHandler.lastModified and 0xFFFFFFFF).toInt()
            }

            it.materialChangeListeners.add { _, _ ->
                gui.state.materialsHash = (System.currentTimeMillis() and 0xFFFFFFFF).toInt()
            }

            it.modelChangeListeners.add { _, _ -> gui.cursorManager.updateCursors(gui) }
            it.materialChangeListeners.add(this::onMaterialUpdate)

            it.modelSelectionHandler.addChangeListener(this::onSelectionUpdate)
            it.modelSelectionHandler.addChangeListener { _, _ -> gui.cursorManager.updateCursors(gui) }
            it.textureSelectionHandler.addChangeListener { _, _ -> gui.cursorManager.updateCursors(gui) }
        }
    }

    private var sizeUpdate: IVector2? = null

    fun onFramebufferSizeUpdated(event: EventFrameBufferSize): Boolean {
        if (event.height == 0 || event.width == 0) return false
        sizeUpdate = vec2Of(event.width, event.height)
        return false
    }

    private inline fun <reified T : Event<Component>> sendEventToComponents(func: (Component, Context, Root) -> T) {
        val listeners = gui.editorView.base.getListeners<T>()
        listeners.forEach { (comp, listener) ->
            listener.process(func(comp, gui.root.context, gui.root))
        }
    }

    fun onAnimatorChange(animator: Animator) =
            sendEventToComponents { component, context, root ->
                EventAnimatorUpdate(component, context, root, animator)
            }

    fun onModelChange(old: IModel, new: IModel) =
            sendEventToComponents { component, context, root ->
                EventModelUpdate(component, context, root, old, new)
            }

    fun onSelectionUpdate(old: Nullable<ISelection>, new: Nullable<ISelection>) =
            sendEventToComponents { component, context, root ->
                EventSelectionUpdate(component, context, root, old, new)
            }

    fun onSelectionTypeUpdate(old: SelectionType, new: SelectionType) =
            sendEventToComponents { component, context, root ->
                EventSelectionTypeUpdate(component, context, root, old, new)
            }


    fun onMaterialUpdate(old: IMaterial?, new: IMaterial?) =
            sendEventToComponents { component, context, root ->
                EventMaterialUpdate(component, context, root, old.asNullable(), new.asNullable())
            }

    override fun runGuiCommand(cmd: String, args: Map<String, Any>) =
            sendEventToComponents { component, context, root ->
                EventGuiCommand(component, context, root, cmd, args)
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
        gui.cursorManager.tick()

//        SyncManager.runSync()
//        getRContext(gui.root.mainView.base)?.update()

        sizeUpdate?.let {
            gui.root.updateSizes(it)
            gui.root.context.updateGlfwWindow()
            gui.windowHandler.resetViewport()
            sizeUpdate = null
        }
    }

    private fun getRContext(base: Component): RContext? {
        val wrapper = base.childs[0].childs[0] as? RComponentWrapper<*, *, *> ?: return null
        return wrapper.component.context
    }
}