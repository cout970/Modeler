package com.cout970.modeler.gui.react.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.ModelAccessor
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponent
import com.cout970.modeler.gui.react.core.RComponentSpec
import com.cout970.modeler.gui.react.core.invoke
import com.cout970.modeler.gui.react.event.EventModelUpdate
import com.cout970.modeler.gui.react.event.EventSelectionUpdate
import com.cout970.modeler.gui.react.panel
import com.cout970.modeler.util.focus
import com.cout970.modeler.util.toColor
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.ScrollBar
import org.liquidengine.legui.component.event.scrollbar.ScrollBarChangeValueEvent
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.event.ScrollEvent

/**
 * Created by cout970 on 2017/10/07.
 */
class ModelObjectList : RComponent<ModelObjectList.Props, ModelObjectList.State>() {

    init {
        state = State(0f, false)
    }

    override fun build(ctx: RBuildContext) = panel {

        position = props.pos.toJoml2f()
        size = props.size.toJoml2f()
        backgroundColor = Config.colorPalette.lightDarkColor.toColor()

        val scrollSize = size.y / 24.0
        val model = props.modelAccessor.model
        val selection = props.modelAccessor.selection
        val maxScroll = Math.max(0.0, model.objects.size - Math.ceil(scrollSize)).toFloat()

        val start = Math.floor(state.scroll.toDouble()).toInt()
        val end = Math.ceil(start + scrollSize).toInt()
        val objectRefs = model.objectRefs

        for (index in start until end) {
            if (index !in objectRefs.indices) break

            val ref = objectRefs[index]
            val name = model.getObject(ref).name
            val selected = selection.map { it.isSelected(ref) }.getOr(false)

            val color = if (selected) {
                Config.colorPalette.selectedButton.toColor()
            } else {
                Config.colorPalette.lightDarkColor.toColor()
            }

            val position = index - start
            +ModelObjectItem { ModelObjectProps(ref, name, model.isVisible(ref), color, position.toFloat()) }
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
        listenerMap.addListener(EventModelUpdate::class.java) {
            replaceState(state.copy(scroll = state.scroll.coerceIn(0f, maxScroll), scrolling = state.scrolling))
        }
    }

    class Props(val modelAccessor: ModelAccessor, val pos: IVector2, val size: IVector2)
    data class State(val scroll: Float, val scrolling: Boolean)

    companion object : RComponentSpec<ModelObjectList, ModelObjectList.Props, ModelObjectList.State>
}