package com.cout970.modeler.render

import com.cout970.glutilities.tessellator.BufferPTNC
import com.cout970.modeler.Debugger
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.gui.CSSTheme
import com.cout970.modeler.gui.leguicomp.AnimationPanel
import com.cout970.modeler.gui.leguicomp.AnimationPanelHead
import com.cout970.modeler.gui.leguicomp.PixelBorder
import com.cout970.modeler.gui.leguicomp.ProfilerDiagram
import com.cout970.modeler.input.event.CustomCallbackKeeper
import com.cout970.modeler.render.gui.LeguiComponentRenderer
import org.liquidengine.legui.animation.AnimatorProvider
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.listener.processor.EventProcessorProvider
import org.liquidengine.legui.system.context.CallbackKeeper
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.handler.processor.SystemEventProcessor
import org.liquidengine.legui.system.handler.processor.SystemEventProcessorImpl
import org.liquidengine.legui.system.renderer.Renderer
import org.liquidengine.legui.system.renderer.RendererProvider
import org.liquidengine.legui.system.renderer.nvg.NvgComponentRenderer
import org.liquidengine.legui.system.renderer.nvg.NvgRenderer
import org.liquidengine.legui.system.renderer.nvg.NvgRendererProvider
import org.liquidengine.legui.theme.Themes

/**
 * Created by cout970 on 2016/12/02.
 */
@Suppress("UNCHECKED_CAST")
class GuiRenderer(val rootFrame: Frame, window: Long) {

    val context: Context = Context(window)
    val callbackKeeper: CallbackKeeper
    val systemEventProcessor: SystemEventProcessor
    val renderer: Renderer
    val buffer = BufferPTNC()

    init {
        callbackKeeper = CustomCallbackKeeper()
        systemEventProcessor = SystemEventProcessorImpl()
        SystemEventProcessor.addDefaultCallbacks(callbackKeeper, systemEventProcessor)
        RendererProvider.setRendererProvider(NvgRendererProvider.getInstance())
        renderer = NvgRenderer()
        renderer.initialize()
        Themes.setDefaultTheme(CSSTheme)
        context.isDebugEnabled = Debugger.DYNAMIC_DEBUG

        (RendererProvider.getInstance() as? NvgRendererProvider)?.let {
//            it.putBorderRenderer(PixelBorder::class.java, PixelBorder.PixelBorderRenderer)
            it.addBorderRenderer(PixelBorder::class.java, PixelBorder.PixelBorderRenderer)
            it.putComponentRenderer(ProfilerDiagram::class.java, ProfilerDiagram.ProfilerDiagramRenderer)
            it.putComponentRenderer(AnimationPanel::class.java, AnimationPanel.Renderer)
            it.putComponentRenderer(AnimationPanelHead::class.java, AnimationPanelHead.Renderer)
            it.putComponentRenderer(Panel::class.java, LeguiComponentRenderer as NvgComponentRenderer<Panel>)
            it.putComponentRenderer(
                com.cout970.modeler.gui.leguicomp.Panel::class.java,
                LeguiComponentRenderer as NvgComponentRenderer<com.cout970.modeler.gui.leguicomp.Panel>
            )
//            it.putComponentRenderer(LayerContainer::class.java, LeguiComponentRenderer as NvgComponentRenderer<LayerContainer>)
        }
    }

    fun updateEvents() {
        context.updateGlfwWindow()
        EventProcessorProvider.getInstance().processEvents()
        systemEventProcessor.processEvents(rootFrame, context)
        AnimatorProvider.getAnimator().runAnimations()
    }

    fun render() {
        Profiler.startSection("guiRender")
        renderer.render(rootFrame, context)
        Profiler.endSection()
    }
}