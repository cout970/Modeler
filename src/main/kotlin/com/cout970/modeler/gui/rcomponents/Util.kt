package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.input.window.Loop
import com.cout970.modeler.util.disableInput
import com.cout970.modeler.util.text
import com.cout970.modeler.util.toJoml2f
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.comp
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.ScrollEvent
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


data class FloatInputProps(
        val getter: () -> Float,
        val command: String,
        val metadata: Map<String, Any>,
        val enabled: Boolean,
        val pos: IVector2
) : RProps

private val formatter = DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH))

class FloatInput : RStatelessComponent<FloatInputProps>() {

    override fun RBuilder.render() = div("FloatInput") {
        style {
            transparent()
            borderless()
            sizeX = 75f
            sizeY = 70f
            position = props.pos.toJoml2f()
        }

        postMount {
            if (!props.enabled) {
                disableInput()
            }
        }

        comp(StringInput(null)) {
            style {
                defaultTextColor()
                background { greyColor }
                horizontalAlign = HorizontalAlign.CENTER
                fontSize = 24f
                posX = 0f
                posY = 16f
                sizeX = 75f
                sizeY = 40f
                text = formatter.format(props.getter())

                onLoseFocus = {
                    dispatch(this, 0f, text)
                }
                onEnterPress = {
                    dispatch(this, 0f, text)
                }
            }

            on<ScrollEvent<StringInput>> {
                val off = it.yoffset.toFloat()
                dispatch(it.component, off, it.component.text)
            }
        }

        comp(IconButton()) {
            style {
                sizeX = 75f
                sizeY = 16f
                icon = "button_up"
                background { lightDarkColor }
                onRelease {
                    dispatch(this, 1f, formatter.format(props.getter()))
                }
            }
        }

        comp(IconButton()) {
            style {
                posY = 56f
                sizeX = 75f
                sizeY = 16f
                icon = "button_down"
                background { lightDarkColor }
                onRelease {
                    dispatch(this, -1f, formatter.format(props.getter()))
                }
            }
        }
    }

    var lastTick = 0L

    fun dispatch(comp: Component, offset: Float, content: String) {
        // this avoid generating a million task doing the same thing
        if (lastTick == Loop.currentTick) return
        lastTick = Loop.currentTick

        comp.apply {
            metadata += props.metadata
            metadata += "offset" to offset
            metadata += "content" to content
            dispatch(props.command)
        }
    }
}