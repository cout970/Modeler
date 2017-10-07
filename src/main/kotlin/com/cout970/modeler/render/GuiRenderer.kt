package com.cout970.modeler.render

import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.gui.comp.PixelBorder
import com.cout970.modeler.input.event.CustomCallbackKeeper
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.listener.processor.EventProcessor
import org.liquidengine.legui.system.context.CallbackKeeper
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.handler.processor.SystemEventProcessor
import org.liquidengine.legui.system.renderer.Renderer
import org.liquidengine.legui.system.renderer.RendererProvider
import org.liquidengine.legui.system.renderer.nvg.NvgRenderer
import org.liquidengine.legui.system.renderer.nvg.NvgRendererProvider

/**
 * Created by cout970 on 2016/12/02.
 */
class GuiRenderer(val rootFrame: Frame, window: Long) {

    val context: Context
    val callbackKeeper: CallbackKeeper
    val systemEventProcessor: SystemEventProcessor
    val renderer: Renderer

    init {
        context = Context(window)
        callbackKeeper = CustomCallbackKeeper()
        systemEventProcessor = SystemEventProcessor()
        systemEventProcessor.addDefaultCallbacks(callbackKeeper)
        RendererProvider.setRendererProvider(NvgRendererProvider.getInstance())
        renderer = NvgRenderer()
        renderer.initialize()

        (RendererProvider.getInstance() as? NvgRendererProvider)
                ?.putBorderRenderer(PixelBorder::class.java, PixelBorder.PixelBorderRenderer)
    }

    fun updateEvents() {
        context.updateGlfwWindow()
        EventProcessor.getInstance().processEvents()
        systemEventProcessor.processEvents(rootFrame, context)
    }

    fun render() {
        Profiler.startSection("guiRender")
        renderer.render(rootFrame, context)
        Profiler.endSection()
    }
}