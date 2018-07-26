package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.api.model.ITransformation
import com.cout970.modeler.controller.usecases.scriptEngine
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.TRTSTransformation
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.input.window.Loop
import com.cout970.modeler.util.disableInput
import com.cout970.modeler.util.text
import com.cout970.modeler.util.toAxisRotations
import com.cout970.modeler.util.toJoml2f
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RStatelessComponent
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector2f
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
                fontSize = 24f
                posX = 0f
                posY = 16f
                sizeX = 75f
                sizeY = 40f
                text = formatter.format(props.getter())
                classes("float_input")
                horizontalAlign = HorizontalAlign.CENTER

                onLoseFocus = {
                    dispatch(this, 0f, text)
                }
                onEnterPress = {
                    dispatch(this, 0f, text)
                }
            }

            on<ScrollEvent<StringInput>> {
                val off = it.yoffset.toFloat()
                dispatch(it.targetComponent, off, it.targetComponent.text)
            }
        }

        comp(IconButton()) {
            style {
                sizeX = 75f
                sizeY = 16f
                icon = "button_up"
                classes("float_input_button")
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
                classes("float_input_button")
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

data class TinyFloatInputProps(val pos: Vector2f, val getter: () -> Float, val setter: (Float) -> Unit) : RProps

class TinyFloatInput : RStatelessComponent<TinyFloatInputProps>() {

    override fun RBuilder.render() = div("TinyFloatInput") {
        style {
            background { dark3 }
            borderless()
            rectCorners()
            position.set(props.pos)
            width = 120f
            height = 24f
        }

        +IconButton("", "button_left", 0f, 0f, 24f, 24f).apply {
            borderless()
            onClick {
                value -= 0.1f
                rerender()
            }
        }

        +StringInput("", "%.3f".format(Locale.ENGLISH, value), 24f, 0f, 72f, 24f).apply {
            horizontalAlign = HorizontalAlign.CENTER
            onScroll {
                text.toFloatValue()?.let { txt ->
                    value = it.yoffset.toFloat() + txt
                } ?: rerender()
            }

            onEnterPress = {
                text.toFloatValue()?.let { txt ->
                    value = txt
                } ?: rerender()
            }

            onLoseFocus = onEnterPress
        }

        +IconButton("", "button_right", 96f, 0f, 24f, 24f).apply {
            borderless()
            onClick {
                value += 0.1f
                rerender()
            }
        }
    }

    var value: Float
        get() = props.getter()
        set(value) {
            props.setter(value)
            rerender()
        }

    fun String.toFloatValue(): Float? {
        return (scriptEngine.eval(this) as? Number)?.toFloat()
    }
}

data class TransformationInputProps(val usecase: String, val transformation: ITransformation, val enable: Boolean) : RProps

class TransformationInput : RStatelessComponent<TransformationInputProps>() {

    override fun RBuilder.render() {

        val t = props.transformation

        when (t) {
            is TRSTransformation -> {
                scale(t.scale)
                position(t.translation)
                rotation(t.rotation.toAxisRotations())
            }
            is TRTSTransformation -> {
                scale(t.scale)
                position(t.translation)
                rotation(t.rotation)
                pivot(t.pivot)
            }
        }
    }

    fun RBuilder.position(translation: IVector3) {
        div("Position") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            +FixedLabel("Position", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput({ translation.xf }, "pos.x", vec2Of(10f, 20f))
            valueInput({ translation.yf }, "pos.y", vec2Of(98f, 20f))
            valueInput({ translation.zf }, "pos.z", vec2Of(185f, 20f))
        }
    }

    fun RBuilder.rotation(rotation: IVector3) {
        div("Rotation") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            +FixedLabel("Rotation", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput({ rotation.xf }, "rot.x", vec2Of(10f, 20f))
            valueInput({ rotation.yf }, "rot.y", vec2Of(98f, 20f))
            valueInput({ rotation.zf }, "rot.z", vec2Of(185f, 20f))
        }
    }

    fun RBuilder.pivot(translation: IVector3) {
        div("Pivot") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            +FixedLabel("Pivot", 0f, 0f, 278f, 18f).apply { textState.fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { textState.fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { textState.fontSize = 18f }

            valueInput({ translation.xf }, "pivot.x", vec2Of(10f, 20f))
            valueInput({ translation.yf }, "pivot.y", vec2Of(98f, 20f))
            valueInput({ translation.zf }, "pivot.z", vec2Of(185f, 20f))
        }
    }


    fun RBuilder.scale(scale: IVector3) {
        div("Scale") {
            style {
                transparent()
                borderless()
                height = 110f
            }

            postMount {
                fillX()
            }

            +FixedLabel("Scale", 0f, 0f, 278f, 18f).apply { fontSize = 22f }
            +FixedLabel("x", 10f, 90f, 75f, 20f).apply { fontSize = 18f }
            +FixedLabel("y", 98f, 90f, 75f, 20f).apply { fontSize = 18f }
            +FixedLabel("z", 185f, 90f, 75f, 20f).apply { fontSize = 18f }

            valueInput({ scale.xf }, "size.x", vec2Of(10f, 20f))
            valueInput({ scale.yf }, "size.y", vec2Of(98f, 20f))
            valueInput({ scale.zf }, "size.z", vec2Of(185f, 20f))
        }
    }

    fun DivBuilder.valueInput(getter: () -> Float, cmd: String, pos: IVector2) {
        child(FloatInput::class, FloatInputProps(
                getter = getter,
                command = props.usecase,
                metadata = mapOf("command" to cmd),
                enabled = props.enable,
                pos = pos)
        )
    }
}