package com.cout970.modeler.view.render

import com.cout970.modeler.event.CustomCallbackKeeper
import com.cout970.modeler.view.RootFrame
import org.liquidengine.legui.context.ILeguiCallbackKeeper
import org.liquidengine.legui.context.LeguiContext
import org.liquidengine.legui.processor.LeguiEventProcessor
import org.liquidengine.legui.processor.SystemEventProcessor
import org.liquidengine.legui.render.LeguiRenderer
import org.liquidengine.legui.render.nvg.NvgLeguiRenderer

/**
 * Created by cout970 on 2016/12/02.
 */
class GuiRenderer(val rootFrame: RootFrame, window: Long) {

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

    fun render() {
        renderer.render(rootFrame)
    }
}