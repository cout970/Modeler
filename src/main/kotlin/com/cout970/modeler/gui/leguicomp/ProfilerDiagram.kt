package com.cout970.modeler.gui.leguicomp

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.Debugger
import com.cout970.modeler.controller.shuffle
import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.util.absolutePositionV
import com.cout970.modeler.util.toColor
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.plus
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import org.joml.Vector4f
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.component.optional.align.VerticalAlign
import org.liquidengine.legui.style.color.ColorConstants
import org.liquidengine.legui.style.font.FontRegistry
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.renderer.nvg.NvgComponentRenderer
import org.liquidengine.legui.system.renderer.nvg.util.NvgText
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG.*
import java.awt.Color
import kotlin.math.min

/**
 * Created by cout970 on 2017/10/14.
 */
class ProfilerDiagram(val timer: Timer) : Panel() {

    object ProfilerDiagramRenderer : NvgComponentRenderer<ProfilerDiagram>() {

        var colors = generateColors()

        fun generateColors(): List<IVector3> {
            return (1..40).map {
                val c = Color.getHSBColor(it.toFloat() / 40f, 0.5f, 1.0f)
                vec3Of(c.blue / 255f, c.green / 255f, c.red / 255f)
            }.shuffle()
        }

        override fun renderComponent(component: ProfilerDiagram, context: Context, nanovg: Long) {

            // FPS counter
            val parent = component.parent.absolutePositionV
            NvgText.drawTextLineToRect(
                    nanovg,
                    Vector4f(parent.xf + 10f, parent.yf, 60f, 24f),
                    false,
                    HorizontalAlign.LEFT,
                    VerticalAlign.MIDDLE,
                    16f,
                    FontRegistry.DEFAULT,
                    "${component.timer.fps} FPS",
                    ColorConstants.white()
            )

            if (!Debugger.showProfiling) return

            val runtime = Runtime.getRuntime()
            val totalRam = min(runtime.maxMemory(), runtime.totalMemory()) / (1024 * 1024)
            val ramUsage = totalRam - (runtime.freeMemory() / (1024 * 1024))

            // Ram usage
            NvgText.drawTextLineToRect(
                    nanovg,
                    Vector4f(parent.xf + 90f, parent.yf, 60f, 24f),
                    false,
                    HorizontalAlign.LEFT,
                    VerticalAlign.MIDDLE,
                    16f,
                    FontRegistry.DEFAULT,
                    "Ram: $ramUsage / $totalRam Mb (used/allocated)",
                    ColorConstants.white()
            )

            // Profiling results
            val radius = 50f
            val textBase = component.absolutePositionV + vec2Of(10, 10)
            val pos = component.absolutePositionV + vec2Of(300 + radius, radius)

            val pairs = Profiler.renderLog

            var sum = 0f
            val col = pairs.map { pair -> pair.first to colors[Math.abs(pair.first.hashCode()) % colors.size] }.toMap()
            val levels = pairs.groupBy { it.first.split('.').size }
            val total = levels[2]!!.map { it.second }.sum()
            val toRender = levels[2]!!.map { it.first to it.second / total }

            // Profiling pie 1
            toRender.forEach { (name, time) ->
                val size = (time * Math.PI * 2).toFloat()
                val c = col[name]!!
                val color = NVGColor.calloc().apply {
                    a(1f); r(c.xf); g(c.yf); b(c.zf)
                }
                nvgBeginPath(nanovg)
                nvgFillColor(nanovg, color)
                nvgArc(nanovg, pos.xf, pos.yf + 24f, radius, sum, sum + size, NVG_CW)
                nvgLineTo(nanovg, pos.xf, pos.yf + 24f)
                nvgFill(nanovg)
                sum += size
                color.free()
            }


            val total2 = levels[3]!!.map { it.second }.sum()
            val toRender2 = levels[3]!!.map { it.first to it.second / total2 }

            // Profiling pie 2
            toRender2.forEach { (name, time) ->
                val size = (time * Math.PI * 2).toFloat()
                val c = col[name]!!
                val color = NVGColor.calloc().apply {
                    a(1f); r(c.xf); g(c.yf); b(c.zf)
                }
                nvgBeginPath(nanovg)
                nvgFillColor(nanovg, color)
                nvgArc(nanovg, pos.xf, pos.yf * 2 + 48f, radius, sum, sum + size, NVG_CW)
                nvgLineTo(nanovg, pos.xf, pos.yf * 2 + 48f)
                nvgFill(nanovg)
                sum += size
                color.free()
            }

            // Time list
            pairs.sortedBy { it.first }.forEachIndexed { index, (name, time) ->
                val parts = name.split(".")
                val lastName = parts.last()
                val level = parts.size


                NvgText.drawTextLineToRect(
                        nanovg,
                        Vector4f(textBase.xf, textBase.yf + index * 16f + 8f, 200f, 24f),
                        false,
                        HorizontalAlign.LEFT,
                        VerticalAlign.MIDDLE,
                        16f,
                        FontRegistry.DEFAULT,
                        "(%06.2f ms) ".format(time * 1000) + spaces(
                                level * 5) + lastName,
                        col[name]!!.toColor()
                )
            }

            // Text on top the pies
            NvgText.drawTextLineToRect(
                    nanovg,
                    Vector4f(pos.xf - 20f, 48f, 200f, 24f),
                    false,
                    HorizontalAlign.LEFT,
                    VerticalAlign.MIDDLE,
                    16f,
                    FontRegistry.DEFAULT,
                    "Level 1",
                    Vector4f(1f, 1f, 1f, 1f)
            )

            NvgText.drawTextLineToRect(
                    nanovg,
                    Vector4f(pos.xf - 20f, 170f, 200f, 24f),
                    false,
                    HorizontalAlign.LEFT,
                    VerticalAlign.MIDDLE,
                    16f,
                    FontRegistry.DEFAULT,
                    "Level 2",
                    Vector4f(1f, 1f, 1f, 1f)
            )
        }
    }
}