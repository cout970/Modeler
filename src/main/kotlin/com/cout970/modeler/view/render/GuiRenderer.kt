package com.cout970.modeler.view.render

import com.cout970.modeler.view.event.CustomCallbackKeeper
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.context.ILeguiCallbackKeeper
import org.liquidengine.legui.context.LeguiContext
import org.liquidengine.legui.processor.LeguiEventProcessor
import org.liquidengine.legui.processor.SystemEventProcessor
import org.liquidengine.legui.render.LeguiRenderer
import org.liquidengine.legui.render.nvg.NvgLeguiRenderer

/**
 * Created by cout970 on 2016/12/02.
 */
class GuiRenderer(rootFrame: Frame, window: Long) {

    val context: LeguiContext
    val callbackKeeper: ILeguiCallbackKeeper
    val uiEventProcessor: LeguiEventProcessor
    val systemEventProcessor: SystemEventProcessor
    val renderer: LeguiRenderer

    init {
        context = LeguiContext(window, rootFrame)
        callbackKeeper = CustomCallbackKeeper()
        uiEventProcessor = LeguiEventProcessor()
        systemEventProcessor = SystemEventProcessor(context, callbackKeeper)
        context.leguiEventProcessor = uiEventProcessor
        renderer = NvgLeguiRenderer(context)
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