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

data class TinyFloatInputProps(
        val pos: Vector2f = Vector2f(),
        val increment: Float = 0.1f,
        val getter: () -> Float,
        val setter: (Float) -> Unit,
        val enabled: Boolean = true) : RProps

class TinyFloatInput : RStatelessComponent<TinyFloatInputProps>() {

    override fun RBuilder.render() = div("TinyFloatInput") {
        style {
            background { dark3 }
            borderless()
            rectCorners()
            position.set(props.pos)
            width = 120f
            height = 24f
            classes("tiny_float_input")
        }

        postMount {
            if (!props.enabled) {
                disableInput()
            }
        }

        +IconButton("", "button_left", 0f, 0f, 24f, 24f).apply {
            classes("tiny_float_input_left")
            onClick {
                value -= props.increment
                rerender()
            }
        }

        +StringInput("", "%.3f".format(Locale.ENGLISH, value), 24f, 0f, 72f, 24f).apply {
            classes("tiny_float_input_field")
            onScroll {
                text.toFloatValue()?.let { txt ->
                    value = it.yoffset.toFloat() * props.increment + txt
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
            classes("tiny_float_input_right")
            onClick {
                value += props.increment
                rerender()
            }
        }
    }

    var value: Float
        get() = props.getter()
        set(value) {
            props.setter(value)
//          rerender()
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
                height = 100f
                classes("inputGroup")
            }

            postMount {
                fillX()
            }

            val line = 0.35f

            div {
                style {
                    classes("div")
                }

                postMount {
                    width = parent.width * line
                    fillY()
                    floatTop(6f, 10f)
                }

                label("Position X") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Position Y") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Position Z") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }
            }

            div {
                style {
                    classes("div")
                }

                postMount {
                    posX = parent.width * line
                    width = parent.width * (1 - line)
                    fillY()
                    floatTop(6f, 10f)
                }

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { translation.xf },
                        setter = { cmd("pos.x", it) },
                        enabled = props.enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { translation.yf },
                        setter = { cmd("pos.y", it) },
                        enabled = props.enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { translation.zf },
                        setter = { cmd("pos.z", it) },
                        enabled = props.enable
                ))
            }
        }
    }

    fun RBuilder.rotation(rotation: IVector3) {
        div("Rotation") {
            style {
                height = 100f
                classes("inputGroup")
            }

            postMount {
                fillX()
            }

            val line = 0.35f

            div {
                style {
                    classes("div")
                }

                postMount {
                    width = parent.width * line
                    fillY()
                    floatTop(6f, 10f)
                }

                label("Rotation X") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Rotation Y") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Rotation Z") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }
            }

            div {
                style {
                    classes("div")
                }

                postMount {
                    posX = parent.width * line
                    width = parent.width * (1 - line)
                    fillY()
                    floatTop(6f, 10f)
                }

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 15f,
                        getter = { rotation.xf },
                        setter = { cmd("rot.x", it) },
                        enabled = props.enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 15f,
                        getter = { rotation.yf },
                        setter = { cmd("rot.y", it) },
                        enabled = props.enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 15f,
                        getter = { rotation.zf },
                        setter = { cmd("rot.z", it) },
                        enabled = props.enable
                ))
            }
        }
    }

    fun RBuilder.pivot(translation: IVector3) {
        div("Pivot") {
            style {
                height = 100f
                classes("inputGroup")
            }

            postMount {
                fillX()
            }

            val line = 0.35f

            div {
                style {
                    classes("div")
                }

                postMount {
                    width = parent.width * line
                    fillY()
                    floatTop(6f, 10f)
                }

                label("Pivot X") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Pivot Y") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Pivot Z") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }
            }

            div {
                style {
                    classes("div")
                }

                postMount {
                    posX = parent.width * line
                    width = parent.width * (1 - line)
                    fillY()
                    floatTop(6f, 10f)
                }

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { translation.xf },
                        setter = { cmd("pivot.x", it) },
                        enabled = props.enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { translation.yf },
                        setter = { cmd("pivot.y", it) },
                        enabled = props.enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { translation.zf },
                        setter = { cmd("pivot.z", it) },
                        enabled = props.enable
                ))
            }
        }
    }

    fun RBuilder.scale(scale: IVector3) {
        div("Scale") {
            style {
                height = 100f
                classes("inputGroup")
            }

            postMount {
                fillX()
            }

            val line = 0.35f

            div {
                style {
                    classes("div")
                }

                postMount {
                    width = parent.width * line
                    fillY()
                    floatTop(6f, 10f)
                }

                label("Scale X") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Scale Y") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }

                label("Scale Z") {
                    style {
                        width = 110f
                        height = 25f
                        classes("inputLabel")
                    }

                    postMount { marginX(10f) }
                }
            }

            div {
                style {
                    classes("div")
                }

                postMount {
                    posX = parent.width * line
                    width = parent.width * (1 - line)
                    fillY()
                    floatTop(6f, 10f)
                }

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { scale.xf },
                        setter = { cmd("size.x", it) },
                        enabled = props.enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { scale.yf },
                        setter = { cmd("size.y", it) },
                        enabled = props.enable
                ))

                child(TinyFloatInput::class, TinyFloatInputProps(
                        pos = Vector2f(5f, 0f),
                        increment = 1f,
                        getter = { scale.zf },
                        setter = { cmd("size.z", it) },
                        enabled = props.enable
                ))
            }
        }
    }

    fun cmd(txt: String, value: Float) {
        if (props.enable) {
            Panel().apply {
                metadata += mapOf("command" to txt)
                metadata += "offset" to 0f
                metadata += "content" to value.toString()
                dispatch(props.usecase)
            }
        }
    }
}