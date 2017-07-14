package com.cout970.modeler.view.render

import com.cout970.modeler.view.event.CustomCallbackKeeper
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.listener.EventProcessor
import org.liquidengine.legui.system.context.CallbackKeeper
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.processor.SystemEventProcessor
import org.liquidengine.legui.system.renderer.Renderer
import org.liquidengine.legui.system.renderer.RendererProvider
import org.liquidengine.legui.system.renderer.nvg.NvgRenderer
import org.liquidengine.legui.system.renderer.nvg.NvgRendererProvider

/**
 * Created by cout970 on 2016/12/02.
 */
class GuiRenderer(rootFrame: Frame, window: Long) {

    val context: Context
    val callbackKeeper: CallbackKeeper
    val uiEventProcessor: EventProcessor
    val systemEventProcessor: SystemEventProcessor
    val renderer: Renderer

    init {
        uiEventProcessor = EventProcessor()
        context = Context(window, rootFrame, uiEventProcessor)
        callbackKeeper = CustomCallbackKeeper()
        systemEventProcessor = SystemEventProcessor(rootFrame, context, callbackKeeper)
        RendererProvider.setRendererProvider(NvgRendererProvider.getInstance())
        renderer = NvgRenderer(context, NvgRendererProvider.getInstance())
        renderer.initialize()
    }

    fun updateEvents() {
        context.updateGlfwWindow()
        uiEventProcessor.processEvent()
        systemEventProcessor.processEvent()
    }

    fun render(frame: Frame) {
        renderer.render(frame)
    }
}