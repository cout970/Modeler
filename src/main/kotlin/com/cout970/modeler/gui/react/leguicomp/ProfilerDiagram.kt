package com.cout970.modeler.gui.react.leguicomp

import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.util.absolutePositionV
import com.cout970.modeler.util.getColor
import com.cout970.modeler.util.toIVector
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.times
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.renderer.nvg.NvgComponentRenderer
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG.*

/**
 * Created by cout970 on 2017/10/14.
 */
class ProfilerDiagram : Panel() {

    object ProfilerDiagramRenderer : NvgComponentRenderer<ProfilerDiagram>() {

        override fun renderComponent(component: ProfilerDiagram, context: Context, nanovg: Long) {
            val pos = component.absolutePositionV + component.size.toIVector() * 0.5
            val radius = component.width / 2

            val pairs = Profiler.renderLog.data
                    .map { it.name to (it.end - it.start) }
                    .map { getColor(it.first.hashCode()) to it.second }

            val total = pairs.map { it.second }.sum()
            val sections = pairs.map { it.first to (it.second / total) }
            var sum = 0f


            sections.forEach { (col, time) ->
                val size = (time * Math.PI * 2).toFloat()
                val color = NVGColor.calloc().apply {
                    a(1f); r(col.xf); g(col.yf); b(col.zf)
                }
                nvgBeginPath(nanovg)
                nvgFillColor(nanovg, color)
                nvgArc(nanovg, pos.xf, pos.yf, radius, sum, sum + size, NVG_CW)
                nvgLineTo(nanovg, pos.xf, pos.yf)
                nvgFill(nanovg)
                sum += size
                color.free()
                return@forEach
            }

//            NvgRenderUtils.renderTextLineToBounds()
        }
    }
}