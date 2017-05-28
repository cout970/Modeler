package com.cout970.modeler.view.render.control

/**
 * Created by cout970 on 2016/12/02.
 */
class GuiRenderer(rootFrame: org.liquidengine.legui.component.Frame, window: Long) {

    val context: org.liquidengine.legui.context.LeguiContext
    val callbackKeeper: org.liquidengine.legui.context.ILeguiCallbackKeeper
    val uiEventProcessor: org.liquidengine.legui.processor.LeguiEventProcessor
    val systemEventProcessor: org.liquidengine.legui.processor.SystemEventProcessor
    val renderer: org.liquidengine.legui.render.LeguiRenderer

    init {
        context = org.liquidengine.legui.context.LeguiContext(window, rootFrame)
        callbackKeeper = com.cout970.modeler.view.event.CustomCallbackKeeper()
        uiEventProcessor = org.liquidengine.legui.processor.LeguiEventProcessor()
        systemEventProcessor = org.liquidengine.legui.processor.SystemEventProcessor(context, callbackKeeper)
        context.leguiEventProcessor = uiEventProcessor
        renderer = org.liquidengine.legui.render.nvg.NvgLeguiRenderer(context)
        renderer.initialize()
    }

    fun updateEvents() {
        context.updateGlfwWindow()
        uiEventProcessor.processEvent()
        systemEventProcessor.processEvent()
    }

    fun render(frame: org.liquidengine.legui.component.Frame) {
        renderer.render(frame)

    }
}