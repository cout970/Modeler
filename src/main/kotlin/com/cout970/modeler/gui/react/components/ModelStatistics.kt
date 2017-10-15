package com.cout970.modeler.gui.react.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.ModelAccessor
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.event.EventModelUpdate
import com.cout970.modeler.gui.react.leguicomp.FixedLabel
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/10/15.
 */
class ModelStatistics : RComponent<ModelStatistics.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuildContext): Component = panel {
        //        border = SimpleLineBorder(Vector4f(1f, 1f, 1f, 1f), 1f)
        backgroundColor = Config.colorPalette.darkestColor.toColor()
        cornerRadius = 0f
        width = 190f
        height = 85f

        val model = props.modelAccessor.model
        val objs = model.objects.size
        val quads = model.objects.map { it.mesh.faces.size }.sum()
        val posVertex = model.objects.map { it.mesh.pos.size }.sum()
        val texVertex = model.objects.map { it.mesh.tex.size }.sum()

        val config: FixedLabel.() -> Unit = {
            textState.horizontalAlign = HorizontalAlign.LEFT
            textState.padding.x = 10f
            backgroundColor = Config.colorPalette.lightDarkColor.toColor()
        }

        +FixedLabel("Objs: $objs", 5f, 5f, 180f, 16f).apply {
            config(this)
        }
        +FixedLabel("Quads: $quads", 5f, 25f, 180f, 16f).apply {
            config(this)
        }
        +FixedLabel("Pos vertex: $posVertex", 5f, 45f, 180f, 16f).apply {
            config(this)
        }
        +FixedLabel("Tex vertex: $texVertex", 5f, 65f, 180f, 16f).apply {
            config(this)
        }

        listenerMap.addListener(EventModelUpdate::class.java) {
            replaceState(state)
        }
    }

    class Props(val modelAccessor: ModelAccessor)

    companion object : RComponentSpec<ModelStatistics, ModelStatistics.Props, Unit>
}