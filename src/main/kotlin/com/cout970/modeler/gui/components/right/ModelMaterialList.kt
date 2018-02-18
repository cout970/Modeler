package com.cout970.modeler.gui.components.right

import com.cout970.modeler.api.model.material.IMaterialRef
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.material.MaterialRefNone
import com.cout970.modeler.core.model.objects
import com.cout970.modeler.core.project.IModelAccessor
import com.cout970.modeler.gui.event.EventMaterialUpdate
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.leguicomp.panel
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.reactive.invoke
import com.cout970.modeler.util.focus
import com.cout970.modeler.util.setTransparent
import com.cout970.modeler.util.toColor
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.ScrollBar
import org.liquidengine.legui.component.event.scrollbar.ScrollBarChangeValueEvent
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.event.ScrollEvent

/**
 * Created by cout970 on 2017/10/14.
 */
class ModelMaterialList : RComponent<ModelMaterialList.Props, ModelMaterialList.State>() {

    init {
        state = State(0f, false)
    }

    override fun build(ctx: RBuilder) = panel {

        position = props.pos.toJoml2f()
        size = props.size.toJoml2f()
        backgroundColor = Config.colorPalette.lightDarkColor.toColor()

        val scrollSize = size.y / 24.0
        val model = props.modelAccessor.model
        val selection = props.modelAccessor.modelSelection
        val maxScroll = Math.max(0.0, model.objects.size - Math.ceil(scrollSize)).toFloat()

        val start = Math.floor(state.scroll.toDouble()).toInt()
        val end = Math.ceil(start + scrollSize).toInt()
        val materialRefs = (model.materialRefs + listOf(MaterialRefNone))
        val selectedMaterial = props.selectedMaterial()

        val materialOfSelectedObjects = selection
                .map { it to it.objects }
                .map { (sel, objs) -> objs.filter(sel::isSelected) }
                .map { it.map { model.getObject(it).material } }
                .getOr(emptyList())

        for (index in start until end) {
            if (index !in materialRefs.indices) {
                break
            }

            val ref = materialRefs[index]
            val name = model.getMaterial(ref).name


            val color = when (ref) {
                in materialOfSelectedObjects -> {
                    Config.colorPalette.greyColor.toColor()
                }
                selectedMaterial -> {
                    Config.colorPalette.brightColor.toColor()
                }
                else -> {
                    Config.colorPalette.lightDarkColor.toColor()
                }
            }

            val position = index - start
            +ModelMaterialItem {
                ModelMaterialItem.Props(ref, name, position, color)
            }
        }

        +ScrollBar(180f, 0f, 10f, size.y).apply {
            minValue = 0f
            maxValue = maxScroll
            scrollStep = 0.1f
            visibleAmount = maxScroll * 0.1f
            curValue = state.scroll
            isScrolling = state.scrolling

            isArrowsEnabled = false
            cornerRadius = 0f
            scrollColor = Config.colorPalette.blackColor.toColor()
            setTransparent()

            if (state.scrolling) {
                ctx.leguiCtx.focus(this)
            }

            listenerMap.addListener(ScrollBarChangeValueEvent::class.java) {
                val newValue = it.newValue
                if (newValue != state.scroll) {
                    replaceState(state.copy(scroll = newValue, scrolling = state.scrolling))
                }
            }
            listenerMap.addListener(MouseClickEvent::class.java) {
                if (isScrolling != state.scrolling) {
                    replaceState(state.copy(scrolling = isScrolling))
                }
            }
        }

        listenerMap.addListener(ScrollEvent::class.java) {
            val newValue = (state.scroll - it.yoffset.toFloat()).coerceIn(0f, maxScroll)
            if (newValue != state.scroll)
                replaceState(state.copy(scroll = newValue, scrolling = state.scrolling))
        }
        listenerMap.addListener(EventSelectionUpdate::class.java) {
            replaceState(state.copy(scroll = state.scroll.coerceIn(0f, maxScroll), scrolling = state.scrolling))
        }
        listenerMap.addListener(EventMaterialUpdate::class.java) {
            replaceState(state.copy(scroll = state.scroll.coerceIn(0f, maxScroll), scrolling = state.scrolling))
        }
    }

    class Props(
            val modelAccessor: IModelAccessor,
            val selectedMaterial: () -> IMaterialRef,
            val pos: IVector2,
            val size: IVector2
    )

    data class State(val scroll: Float, val scrolling: Boolean)

    companion object : RComponentSpec<ModelMaterialList, Props, State>
}