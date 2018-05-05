package com.cout970.modeler.render

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.modeler.Debugger
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.resource.ResourceLoader
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.render.tool.shader.UniversalShader
import com.cout970.modeler.util.ITickeable
import com.cout970.vector.extensions.toVector4

/**
 * Created by cout970 on 2016/11/29.
 */
class RenderManager : ITickeable {

    // @Injected
    lateinit var gui: Gui

    lateinit var guiRenderer: GuiRenderer
    lateinit var shader: UniversalShader
    lateinit var canvasRenderer: CanvasRenderer

    fun initOpenGl(resourceLoader: ResourceLoader, gui: Gui) {
        this.gui = gui
        log(Level.FINE) { "[RenderManager] Creating GuiRenderer" }
        guiRenderer = GuiRenderer(gui.root, gui.windowHandler.window.id)
        gui.root.context = guiRenderer.context
        log(Level.FINE) { "[RenderManager] Creating Universal Shader" }
        shader = UniversalShader(resourceLoader)
        log(Level.FINE) { "[RenderManager] Creating CanvasRenderer" }
        canvasRenderer = CanvasRenderer(this)
        GLStateMachine.clearColor = Config.colorPalette.modelBackgroundColor.toVector4(1.0f)
    }

    override fun preTick() {
        Profiler.startSection("leguiEvents")
        guiRenderer.updateEvents()
        Profiler.endSection()
    }

    override fun tick() {
        Profiler.startSection("render")
        GLStateMachine.clear()
        gui.animator.updateTime(gui.timer)
        canvasRenderer.render(gui)
        guiRenderer.render()
        gui.windowHandler.resetViewport()
        Profiler.endSection()
    }

    override fun postTick() {
        Debugger.postTick()
        Profiler.startSection("leguiEvents")
        guiRenderer.updateEvents()
        Profiler.endSection()
    }
}