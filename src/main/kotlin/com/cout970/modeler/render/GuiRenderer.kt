package com.cout970.modeler.render

import com.cout970.modeler.event.CustomCallbackKeeper
import org.liquidengine.legui.context.ILeguiCallbackKeeper
import org.liquidengine.legui.context.LeguiContext
import org.liquidengine.legui.processor.LeguiEventListenerProcessor
import org.liquidengine.legui.processor.SystemEventListenerProcessor
import org.liquidengine.legui.render.LeguiRenderer
import org.liquidengine.legui.render.nvg.NvgLeguiRenderer

/**
 * Created by cout970 on 2016/12/02.
 */
class GuiRenderer(window: Long, val renderManager: RenderManager) {

    val context: LeguiContext
    val callbackKeeper: ILeguiCallbackKeeper
    val uiEventProcessor: LeguiEventListenerProcessor
    val systemEventProcessor: SystemEventListenerProcessor
    val renderer: LeguiRenderer

    init {
        context = LeguiContext(window, renderManager.root)
        callbackKeeper = CustomCallbackKeeper()
        uiEventProcessor = LeguiEventListenerProcessor()
        systemEventProcessor = SystemEventListenerProcessor(renderManager.root, context, callbackKeeper)
        context.leguiEventProcessor = uiEventProcessor
        renderer = NvgLeguiRenderer(context)
        renderer.initialize()
    }

    fun update() {
        context.updateGlfwWindow()
        uiEventProcessor.processEvent()
        systemEventProcessor.processEvent()
    }

    fun render() {

        renderer.render(renderManager.root)
    }
}