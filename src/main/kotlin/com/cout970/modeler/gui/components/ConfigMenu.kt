package com.cout970.modeler.gui.components

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.config.ConfigComment
import com.cout970.modeler.gui.Popup
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuildContext
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.reactive.invoke
import com.cout970.modeler.util.text
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.MouseClickEvent
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType

class ConfigMenu : RComponent<ConfigMenu.Props, Config>() {

    init {
        state = Config
    }

    @Suppress("UNCHECKED_CAST")
    override fun build(ctx: RBuildContext): Component = panel {
        backgroundColor = Vector4f(1f, 1f, 1f, 0.05f)
        fill(ctx)

        // Centered panel
        +panel {
            width = 700f
            height = 550f
            center(ctx)
            border(2f) { greyColor }
            background { darkestColor }

            +panel {
                posX = 20f
                posY = 20f
                width = 300f
                height = 125f
                background { darkColor }

                +FixedLabel("Author", 0f, 2f, 300f, 24f).apply {
                    textState.fontSize = 21f
                }

                // Username
                +FixedLabel("Name", 10f, 30f, 80f, 24f).apply {
                    textState.fontSize = 18f
                    textState.horizontalAlign = HorizontalAlign.LEFT
                }
                +StringInput(state.user.name, 90f, 30f, 200f).apply {
                    background { darkestColor }
                    onTextChange = {
                        state.user = state.user.copy(name = it.newValue)
                        rebuild()
                    }
                    onLoseFocus = {
                        state.user = state.user.copy(name = text)
                        rebuild()
                    }
                    onEnterPress = onEnterPress
                }

                +FixedLabel("Email", 10f, 60f, 80f, 24f).apply {
                    textState.fontSize = 18f
                    textState.horizontalAlign = HorizontalAlign.LEFT
                }

                +StringInput(state.user.email, 90f, 60f, 200f).apply {
                    background { darkestColor }
                    onTextChange = {
                        state.user = state.user.copy(email = it.newValue)
                        rebuild()
                    }
                    onLoseFocus = {
                        state.user = state.user.copy(email = text)
                        rebuild()
                    }
                    onEnterPress = onEnterPress
                }

                +FixedLabel("Web", 10f, 90f, 80f, 24f).apply {
                    textState.fontSize = 18f
                    textState.horizontalAlign = HorizontalAlign.LEFT
                }

                +StringInput(state.user.web, 90f, 90f, 200f).apply {
                    background { darkestColor }
                    onTextChange = {
                        state.user = state.user.copy(web = it.newValue)
                        rebuild()
                    }
                    onLoseFocus = {
                        state.user = state.user.copy(web = text)
                        rebuild()
                    }
                    onEnterPress = onEnterPress
                }
            }

            val properties = getProperties()

            +panel {
                posX = 20f
                posY = 155f
                width = 300f
                height = 370f //24f * properties.size + 10f
                background { darkColor }

                properties.forEachIndexed { index, (name, prop) ->

                    +FixedLabel(name.capitalize(), 10f, 5f + index * 24f, 100f, 24f).apply {
                        textState.horizontalAlign = HorizontalAlign.LEFT
                        textState.fontSize = 18f
                        prop.getTooltip()?.let { tooltip = InstantTooltip(it) }
                    }

                    +FloatInput {
                        FloatInput.Props(vec2Of(195f, 5f + index * 24f), prop as KMutableProperty<Float>, Config)
                    }
                }
            }

            // padding: 20px
            +TextButton("", "Exit", 600f, 505f, 80f, 24f).apply {
                listenerMap.addListener(MouseClickEvent::class.java) {
                    props.popup.returnFunc(null)
                }
            }
        }
    }

    fun KMutableProperty<*>.getTooltip(): String? {
        return annotations
                       .filterIsInstance<ConfigComment>()
                       .firstOrNull()
                       ?.run { comment } ?: return null
    }

    fun getProperties(): List<Pair<String, KMutableProperty<*>>> {
        return Config::class
                .memberProperties
                .filterIsInstance<KMutableProperty<*>>()
                .filter { it.returnType.javaType == Float::class.java }
                .map { it.name to it }
    }

    class Props(val popup: Popup)

    companion object : RComponentSpec<ConfigMenu, Props, Config>
}