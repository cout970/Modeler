package com.cout970.modeler.gui.components

import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.input.window.Loop
import com.cout970.modeler.util.*
import com.cout970.reactive.dsl.height
import com.cout970.reactive.dsl.width
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.event.ScrollEvent
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * Created by cout970 on 2017/09/28.
 */
class ValueInput : RComponent<ValueInput.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuilder): Component = panel {
        position = props.pos.toJoml2f()
        width = 75f
        height = 70f
        setTransparent()
        setBorderless()

        val input = StringInput("", formatter.format(props.value()), 0f, 16f, 75f, 40f).apply {
            textState.textColor = Config.colorPalette.textColor.toColor()
            textState.horizontalAlign = HorizontalAlign.CENTER
            textState.fontSize = 24f
            background { greyColor }
        }

        +input.apply {

            listenerMap.addListener(ScrollEvent::class.java) {
                dispatch(it.yoffset.toFloat(), input.text)
            }
            onLoseFocus = {
                dispatch(0f, input.text)
            }
            onEnterPress = {
                dispatch(0f, input.text)
            }
        }

        +IconButton("", "button_up", 0f, 0f, 75f, 16f).apply {
            background { lightDarkColor }
            listenerMap.addListener(MouseClickEvent::class.java) {
                if (it.action == MouseClickEvent.MouseClickAction.RELEASE)
                    dispatch(1f, input.text)
            }
        }

        +IconButton("", "button_down", 0f, 56f, 75f, 16f).apply {
            background { lightDarkColor }
            listenerMap.addListener(MouseClickEvent::class.java) {
                if (it.action == MouseClickEvent.MouseClickAction.RELEASE)
                    dispatch(-1f, input.text)
            }
        }

        if (!props.enabled) {
            disableInput()
        }
    }

    var lastTick = 0L

    fun dispatch(offset: Float, content: String) {
        // this avoid generating a million task doing the same thing
        if (lastTick == Loop.currentTick) return
        lastTick = Loop.currentTick

        val data = Panel().apply {
            metadata += props.metadata
            metadata += "offset" to offset
            metadata += "content" to content
        }
        props.dispatcher.onEvent(props.cmd, data)
    }

    class Props(
            val dispatcher: Dispatcher,
            val value: () -> Float,
            val cmd: String,
            val metadata: Map<String, Any>,
            val enabled: Boolean,
            val pos: IVector2
    )

    companion object : RComponentSpec<ValueInput, ValueInput.Props, Unit> {
        val formatter = DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
    }
}

