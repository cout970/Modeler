package com.cout970.modeler.gui.components.right

import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.event.EventModelUpdate
import com.cout970.modeler.gui.leguicomp.FixedLabel
import com.cout970.modeler.gui.leguicomp.background
import com.cout970.modeler.gui.leguicomp.panel
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.reactive.dsl.height
import com.cout970.reactive.dsl.width
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign

/**
 * Created by cout970 on 2017/10/15.
 */
class ModelStatistics : RComponent<ModelStatistics.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuilder): Component = panel {
        background { darkestColor }
        style.cornerRadius.set(0f)
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
            background { lightDarkColor }
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

    class Props(val modelAccessor: IModelAccessor)

    companion object : RComponentSpec<ModelStatistics, Props, Unit>
}