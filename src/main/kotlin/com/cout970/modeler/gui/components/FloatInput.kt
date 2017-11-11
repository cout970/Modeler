package com.cout970.modeler.gui.components

import com.cout970.modeler.controller.usecases.UpdateTemplateCube
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuildContext
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.text
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component
import kotlin.reflect.KMutableProperty

class FloatInput : RComponent<FloatInput.Props, Unit>() {

    init {
        state = Unit
    }

    override fun build(ctx: RBuildContext): Component = panel {
        background { darkestColor }
        setBorderless()
        cornerRadius = 0f
        position = props.pos.toJoml2f()
        width = 100f
        height = 24f

        val value: Float = props.property.call(props.obj)

        +IconButton("", "button_left", 0f, 0f, 24f, 24f).apply {
            onClick {
                dispatch(value - 0.1f)
                rebuild()
            }
            setBorderless()
        }
        +StringInput(value.toString(), 24f, 0f, 52f, 24f).apply {

            onScroll = {
                text.toFloatValue()?.let { txt ->
                    dispatch(it.yoffset.toFloat() + txt)
                } ?: rebuild()
            }

            onEnterPress = {
                text.toFloatValue()?.let { txt ->
                    dispatch(txt)
                } ?: rebuild()
            }

            onLoseFocus = onEnterPress
        }
        +IconButton("", "button_right", width - 24f, 0f, 24f, 24f).apply {
            onClick {
                dispatch(value + 0.1f)
                rebuild()
            }
            setBorderless()
        }
    }

    fun dispatch(value: Float) {
        props.property.setter.call(props.obj, value)
    }

    fun String.toFloatValue(): Float? {
        return (UpdateTemplateCube.scriptEngine.eval(this) as? Number)?.toFloat()
    }

    class Props(val pos: IVector2, val property: KMutableProperty<Float>, val obj: Any)

    companion object : RComponentSpec<FloatInput, Props, Unit>
}