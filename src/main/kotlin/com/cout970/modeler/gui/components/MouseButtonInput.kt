package com.cout970.modeler.gui.components

import com.cout970.glutilities.device.Mouse
import com.cout970.modeler.core.config.MouseKeyBind
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.MouseClickEvent
import kotlin.reflect.KMutableProperty

class MouseButtonInput : RComponent<MouseButtonInput.Props, MouseButtonInput.State>() {

    init {
        state = State()
    }

    override fun build(ctx: RBuilder): Component = panel {
        background { darkestColor }
        setBorderless()
        cornerRadius = 0f
        position = props.pos.toJoml2f()
        width = 150f
        height = 24f

        val text = if (state.showMode) getMouseButtonName(getMouseButton()) else "Press new button"
        +TextButton("", text, 0f, 0f, 150f, 24f).apply {
            background { greyColor }
            textState.horizontalAlign = HorizontalAlign.LEFT
            textState.fontSize = 20f
            textState.padding.x = 5f
            listenerMap.addListener(MouseClickEvent::class.java) {
                if (state.showMode) {
                    if (it.action == MouseClickEvent.MouseClickAction.CLICK) {
                        replaceState(state.copy(showMode = false))
                    }
                } else {
                    if (it.action == MouseClickEvent.MouseClickAction.PRESS) {
                        setMouseButton(it.button.code)
                        replaceState(state.copy(showMode = true))
                    }
                }
            }
        }
    }

    fun getMouseButton() = props.property.call(props.obj).button

    fun setMouseButton(button: Int) {
        props.property.setter.call(props.obj, MouseKeyBind(button))
    }

    fun getMouseButtonName(button: Int): String = when (button) {
        Mouse.BUTTON_LEFT -> "Left button"
        Mouse.BUTTON_MIDDLE -> "Middle button"
        Mouse.BUTTON_RIGHT -> "Right button"
        else -> "Unknown button"
    }

    data class State(val showMode: Boolean = true)
    class Props(val pos: IVector2, val property: KMutableProperty<MouseKeyBind>, val obj: Any)

    companion object : RComponentSpec<MouseButtonInput, MouseButtonInput.Props, MouseButtonInput.State>
}