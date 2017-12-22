package com.cout970.modeler.gui.components

import com.cout970.modeler.core.config.KeyBind
import com.cout970.modeler.core.config.KeyboardModifiers
import com.cout970.modeler.core.config.getName
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.toJoml2f
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.event.FocusEvent
import org.liquidengine.legui.event.KeyEvent
import org.lwjgl.glfw.GLFW
import kotlin.reflect.KMutableProperty

class KeyboardKeyInput : RComponent<KeyboardKeyInput.Props, KeyboardKeyInput.State>() {

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

        val text = if (state.showMode) getKey().getName() else "Press new button"
        +StringInput(text, 0f, 0f, 150f, 24f).apply {
            background { greyColor }
            listenerMap.addListener(FocusEvent::class.java) {
                if (it.isFocused) {
                    if (state.showMode) {
                        replaceState(state.copy(showMode = false))
                    }
                } else {
                    if (!state.showMode) {
                        replaceState(state.copy(showMode = true))
                    }
                }
            }
            listenerMap.addListener(KeyEvent::class.java) {
                if (!state.showMode) {
                    if (it.action == GLFW.GLFW_RELEASE) {
                        setKey(it.key, it.mods)
                        replaceState(state.copy(showMode = true))
                    }
                }
            }
        }
    }

    fun getKey() = props.property.call(props.obj)

    fun setKey(key: Int, mods: Int) {

        val modifiers = mutableListOf<KeyboardModifiers>()
        if (mods and GLFW.GLFW_MOD_CONTROL != 0) modifiers += KeyboardModifiers.CTRL
        if (mods and GLFW.GLFW_MOD_ALT != 0) modifiers += KeyboardModifiers.ALT
        if (mods and GLFW.GLFW_MOD_SHIFT != 0) modifiers += KeyboardModifiers.SHIFT
        if (mods and GLFW.GLFW_MOD_SUPER != 0) modifiers += KeyboardModifiers.SUPER

        props.property.setter.call(props.obj, KeyBind(key, *modifiers.toTypedArray()))
    }

    data class State(val showMode: Boolean = true)
    class Props(val pos: IVector2, val property: KMutableProperty<KeyBind>, val obj: Any)

    companion object : RComponentSpec<KeyboardKeyInput, Props, State>
}