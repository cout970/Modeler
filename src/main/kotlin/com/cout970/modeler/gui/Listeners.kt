package com.cout970.modeler.gui

import com.cout970.glutilities.event.*
import com.cout970.modeler.core.project.ProjectManager
import com.cout970.modeler.input.event.EventController
import com.cout970.modeler.render.tool.camera.CameraUpdater
import com.cout970.modeler.util.*
import com.cout970.reactive.core.AsyncManager
import com.cout970.reactive.core.SyncManager
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.component.TextArea
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.event.Event
import org.liquidengine.legui.system.context.Context

/**
 * Created by cout970 on 2017/05/16.
 */
private lateinit var listeners: Listeners

fun sendCmd(cmd: String, args: Map<String, Any> = emptyMap()) {
    listeners.runGuiCommand(cmd, args)
}

class Listeners : ITickeable, IGuiCmdRunner {

    private lateinit var gui: Gui
    lateinit var cameraUpdater: CameraUpdater

    fun initListeners(eventController: EventController, projectManager: ProjectManager, gui: Gui) {
        this.gui = gui
        listeners = this

        eventController.let {
            it.addListener(EventKeyUpdate::class.java, this::onKeyPress)
            it.addListener(EventFrameBufferSize::class.java, this::onFramebufferSizeUpdated)
            it.addListener(EventMouseScroll::class.java, this::onMouseScroll)
            it.addListener(EventMouseClick::class.java, this::onMouseClick)

            cameraUpdater = CameraUpdater(gui.canvasContainer, it, gui.timer) { gui.state.popup != null }
        }

        AsyncManager.setInstance(SyncManager)

        projectManager.let {
            it.modelChangeListeners.add { _, new ->
                gui.state.modelHash = new.hashCode()
                gui.state.cursor.update(gui)
                runGuiCommand("updateModel")
                runGuiCommand("updateAnimation")
            }

            it.modelSelectionHandler.addChangeListener { _, _ ->
                gui.state.modelSelectionHash = (gui.programState.modelSelectionHandler.lastModified and 0xFFFFFFFF).toInt()
                gui.state.textureSelectionHash = (gui.programState.textureSelectionHandler.lastModified and 0xFFFFFFFF).toInt()
                gui.state.cursor.update(gui)
                runGuiCommand("updateSelection")
            }

            it.textureSelectionHandler.addChangeListener { _, _ ->
                gui.state.modelSelectionHash = (gui.programState.modelSelectionHandler.lastModified and 0xFFFFFFFF).toInt()
                gui.state.textureSelectionHash = (gui.programState.textureSelectionHandler.lastModified and 0xFFFFFFFF).toInt()
                gui.state.cursor.update(gui)
                runGuiCommand("updateSelection")
            }

            it.materialChangeListeners.add { _, _ ->
                gui.state.materialsHash = (System.currentTimeMillis() and 0xFFFFFFFF).toInt()
                runGuiCommand("updateMaterial")
            }
        }
    }

    /**
     * Send a command to all the components on the gui that are listening
     * This is used to cause updates in the gui when the data displayed has changed
     */
    override fun runGuiCommand(cmd: String, args: Map<String, Any>) {
        sendEventToComponents { component, context, root ->
            EventGuiCommand(component, context, root, cmd, args)
        }
    }

    private inline fun <reified T : Event<Component>> sendEventToComponents(func: (Component, Context, Root) -> T) {
        val listeners = gui.editorView.base.getListeners<T>()
        listeners.forEach { (comp, listener) ->
            listener.process(func(comp, gui.root.context, gui.root))
        }
    }

    // This prevents resizing the gui several times per tick
    private var sizeUpdate: IVector2? = null

    override fun tick() {
        SyncManager.runSync()
        cameraUpdater.updateCameras()
        gui.cursorManager.tick()

        sizeUpdate?.let {
            gui.root.updateSizes(it)
            gui.root.context.updateGlfwWindow()
            gui.windowHandler.resetViewport()
            sizeUpdate = null
        }
    }

    fun onFramebufferSizeUpdated(event: EventFrameBufferSize): Boolean {
        if (event.height == 0 || event.width == 0) return false
        sizeUpdate = vec2Of(event.width, event.height)
        return false
    }

    fun onMouseScroll(e: EventMouseScroll): Boolean {
        val mousePos = gui.input.mouse.getMousePos()
        gui.canvasContainer.canvas.forEach { canvas ->
            if (mousePos.isInside(canvas.absolutePositionV, canvas.size.toIVector()) && gui.state.popup == null) {

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
        if (gui.root.context.focusedGui is TextArea) return false
        return gui.keyboardBinder.onEvent(e)
    }

    fun onMouseClick(e: EventMouseClick): Boolean {
        if (gui.state.popup != null) return false
        if (e.keyState != EnumKeyState.PRESS) return false
        return gui.canvasManager.onClick(e)
    }
}

class EventGuiCommand(comp: Component, ctx: Context, frame: Frame,
                      val command: String, val args: Map<String, Any>
) : Event<Component>(comp, ctx, frame)