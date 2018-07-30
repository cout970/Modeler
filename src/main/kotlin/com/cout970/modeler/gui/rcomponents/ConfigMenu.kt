package com.cout970.modeler.gui.rcomponents

import com.cout970.glutilities.device.Mouse
import com.cout970.modeler.core.config.*
import com.cout970.modeler.core.config.ConfigManager.getDefaultConfig
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.project.Author
import com.cout970.modeler.core.project.IProjectPropertiesHolder
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.util.text
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RState
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.style.color.ColorConstants
import org.liquidengine.legui.system.context.Context
import org.lwjgl.glfw.GLFW
import java.awt.Desktop
import java.net.URI
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType

class ConfigMenuProps(
        val returnFunc: (Any?) -> Unit,
        val propertyHolder: IProjectPropertiesHolder
) : RProps

class ConfigMenu : RComponent<ConfigMenuProps, ConfigMenu.State>() {

    override fun getInitialState() = State(Tab.PROJECT, ConfigManager.getConfigAsJson(),
            ConfigManager.getConfigAsJson())

    override fun RBuilder.render() = div("ConfigMenu") {
        style {
            width = 700f
            height = 550f
            classes("popup_back")
        }

        postMount {
            center()
        }

        tabs()

        when (state.tab) {
            Tab.PROJECT -> projectTab()
            Tab.PARAMETERS -> parametersTab()
            Tab.CONTROLS -> controlsTab()
            Tab.ABOUT -> aboutTab()
        }

        // padding: 20px
        +TextButton("", "Ok", 400f, 505f, 90f, 24f).apply {
            onClick {
                ConfigManager.setConfigFromJson(state.editingConfig)
                ConfigManager.saveConfig()
                props.returnFunc(null)
            }
        }

        +TextButton("", "Cancel", 500f, 505f, 90f, 24f).apply {
            onClick {
                ConfigManager.setConfigFromJson(state.previousConfig)
                props.returnFunc(null)
            }
        }

        +TextButton("", "Apply", 600f, 505f, 90f, 24f).apply {
            if (state.editingConfig == ConfigManager.getConfigAsJson()) {
                isEnabled = false
                background { dark2 }
                onCursorEnter {
                    isHovered = false
                }
            } else {
                onClick {
                    ConfigManager.setConfigFromJson(state.editingConfig)

                }
            }
        }
    }

    fun RBuilder.tabs() {
        div("Tabs") {
            style {
                posX = 20f
                posY = 20f
                width = 660f
                height = 32f
                classes("popup_section")
            }

            +TextButton("", "Project", 0f, 0f, 80f, 32f).apply {
                if (state.tab == Tab.PROJECT) background { grey } else background { dark2 }
                textState.fontSize = 18f
                style.border = PixelBorder().apply { enableRight = true }
                onClick { setState { copy(tab = Tab.PROJECT) } }
            }
            +TextButton("", "Parameters", 80f, 0f, 80f, 32f).apply {
                if (state.tab == Tab.PARAMETERS) background { grey } else background { dark2 }
                textState.fontSize = 18f
                style.border = PixelBorder().apply { enableRight = true }
                onClick { setState { copy(tab = Tab.PARAMETERS) } }
            }
            +TextButton("", "Controls", 160f, 0f, 80f, 32f).apply {
                if (state.tab == Tab.CONTROLS) background { grey } else background { dark2 }
                textState.fontSize = 18f
                style.border = PixelBorder().apply { enableRight = true }
                onClick { setState { copy(tab = Tab.CONTROLS) } }
            }
            +TextButton("", "About", 240f, 0f, 80f, 32f).apply {
                if (state.tab == Tab.ABOUT) background { grey } else background { dark2 }
                textState.fontSize = 18f
                onClick { setState { copy(tab = Tab.ABOUT) } }
            }
        }
    }

    fun RBuilder.projectTab() {
        div("ProjectTab") {
            style {
                posX = 20f
                posY = 80f
                width = 660f
                height = 125f + 80f
                classes("popup_section")
            }

            val project = props.propertyHolder.projectProperties

            +FixedLabel("Project", 0f, 4f, 660f, 24f).apply {
                textState.fontSize = 22f
            }

            // Owner
            +FixedLabel("Project owner", 10f, 30f, 120f, 24f).apply {
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.LEFT
            }

            +FixedLabel(project.owner.name, 160f, 30f, 480f).apply {
                background { dark3 }
                paddingRight(10f)
                textState.fontSize = 20f
                textState.textColor = ColorConstants.lightBlue()
                textState.horizontalAlign = HorizontalAlign.RIGHT
            }

            // Name
            +FixedLabel("Project name", 10f, 60f, 120f, 24f).apply {
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.LEFT
            }

            +StringInput("", project.name, 160f, 60f, 480f).apply {
                background { dark3 }
                paddingRight(10f)
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.RIGHT
                onTextChange = {
                    updateProjectProperties(project.copy(name = it.newValue))
                }
                onLoseFocus = {
                    updateProjectProperties(project.copy(name = text))
                }
                onEnterPress = {
                    updateProjectProperties(project.copy(name = text))
                }
            }

            // Description
            +FixedLabel("Project description", 10f, 90f, 120f, 24f).apply {
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.LEFT
            }

            +MultilineStringInput(project.description, 160f, 90f, 480f, 24f + 80f).apply {
                background { dark3 }
                paddingRight(10f)
                textState.fontSize = 20f
                onTextChange = {
                    updateProjectProperties(project.copy(description = it.newValue))
                }
                onLoseFocus = {
                    updateProjectProperties(project.copy(description = text))
                }
                onEnterPress = {
                    updateProjectProperties(project.copy(description = text))
                }
            }
        }

        div("UsedData") {
            style {
                posX = 20f
                posY = 130f + 180f
                width = 660f
                height = 125f
                classes("popup_section")
            }

            val user = Config.user

            +FixedLabel("User", 0f, 4f, 660f, 24f).apply {
                textState.fontSize = 22f
            }

            // Username
            +FixedLabel("Name", 10f, 30f, 80f, 24f).apply {
                textState.fontSize = 18f
                textState.horizontalAlign = HorizontalAlign.LEFT
            }

            +StringInput("", user.name, 160f, 30f, 480f).apply {
                background { dark3 }
                paddingRight(10f)
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.RIGHT
                onTextChange = {
                    updateUser(user.copy(name = it.newValue))
                }
                onLoseFocus = {
                    updateUser(user.copy(name = text))
                }
                onEnterPress = {
                    updateUser(user.copy(name = text))
                }
            }

            +FixedLabel("Email", 10f, 60f, 80f, 24f).apply {
                textState.fontSize = 18f
                textState.horizontalAlign = HorizontalAlign.LEFT
            }

            +StringInput("", user.email, 160f, 60f, 480f).apply {
                background { dark3 }
                paddingRight(10f)
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.RIGHT
                onTextChange = {
                    updateUser(user.copy(email = it.newValue))
                }
                onLoseFocus = {
                    updateUser(user.copy(email = text))
                }
                onEnterPress = {
                    updateUser(user.copy(email = text))
                }
            }

            +FixedLabel("Web", 10f, 90f, 80f, 24f).apply {
                textState.fontSize = 18f
                textState.horizontalAlign = HorizontalAlign.LEFT
            }

            +StringInput("", user.web, 160f, 90f, 480f).apply {
                background { dark3 }
                paddingRight(10f)
                textState.fontSize = 20f
                textState.horizontalAlign = HorizontalAlign.RIGHT
                onTextChange = {
                    updateUser(user.copy(web = it.newValue))
                }
                onLoseFocus = {
                    updateUser(user.copy(web = text))
                }
                onEnterPress = {
                    updateUser(user.copy(web = text))
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun RBuilder.parametersTab() {
        scrollablePanel {
            style {
                posX = 20f
                posY = 60f
                sizeX = 660f
                sizeY = 430f
                transparent()
                classes("popup_section")
            }

            horizontalScroll { style { hide() } }

            val properties = getProperties()

            verticalScroll {
                style {
                    //                                        if (properties.size * 30f + 10f > 430f) {
                    visibleAmount = 50f
//                    } else {
//                        hide()
//                    }
                }
            }

            viewport {
                style { classes("popup_section") }
            }

            container {

                style {
                    transparent()
                    width = 650f
                    height = properties.size * 30f + 10f
                    classes("popup_section")
                }

                properties.sortedBy { it.first }.forEachIndexed { index, (name, tooltip, prop) ->

                    div("Item") {
                        style {
                            posX = 5f
                            posY = 6f + index * 30f
                            width = 631f
                            height = 24f
                            classes("popup_parameter_item")
                        }

                        +FixedLabel(name.capitalize(), 10f, 0f, 100f, 24f).apply {
                            textState.horizontalAlign = HorizontalAlign.LEFT
                            textState.fontSize = 20f
                            tooltip?.let { this.tooltip = InstantTooltip(it) }
                        }

                        child(TinyFloatInput::class, TinyFloatInputProps(
                                Vector2f(510f, 0f), 0.1f, prop.first, prop.second
                        ))
                    }
                }
            }
        }

        +TextButton("", "Reset defaults", 300f, 505f, 90f, 24f).apply {
            onClick {

                setState { copy(editingConfig = mergeParameters(editingConfig, getDefaultConfig())) }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun RBuilder.controlsTab() {
        scrollablePanel {
            style {
                posX = 20f
                posY = 60f
                sizeX = 660f
                sizeY = 430f
                classes("popup_section")
            }

            horizontalScroll { style { hide() } }

            verticalScroll {
                style {
                    visibleAmount = 10f
                    classes("popup_section")
                }
            }

            container {

                val mouseKeybinds = getMouseKeyBinds()
                val keybinds = getKeyBinds()

                style {
                    classes("popup_section")
                    width = 650f
                    height = mouseKeybinds.size * 30f + keybinds.size * 30f + 10f
                }

                mouseKeybinds.sortedBy { it.first }.forEachIndexed { index, (name, tooltip, prop) ->

                    div {
                        style {
                            posX = 5f
                            posY = 6f + index * 30f
                            width = 631f
                            height = 24f
                            classes("popup_parameter_item")
                        }

                        +FixedLabel(name.capitalize(), 10f, 0f, 100f, 24f).apply {
                            textState.horizontalAlign = HorizontalAlign.LEFT
                            textState.fontSize = 20f
                            tooltip?.let { this.tooltip = InstantTooltip(it) }
                        }

                        child(MouseButtonInput::class, MouseButtonInputProps(
                                Vector2f(480f, 0f), prop.first, prop.second
                        ))
                    }
                }

                keybinds.sortedBy { it.first }.forEachIndexed { index, (name, tooltip, prop) ->

                    div {
                        style {
                            posX = 5f
                            posY = 6f + index * 30f + mouseKeybinds.size * 30f
                            width = 631f
                            height = 24f
                            classes("popup_parameter_item")
                        }

                        +FixedLabel(name.capitalize(), 10f, 0f, 100f, 24f).apply {
                            horizontalAlign = HorizontalAlign.LEFT
                            fontSize = 20f
                            tooltip?.let { this.tooltip = InstantTooltip(it) }
                        }

                        child(KeyboardKeyInput::class, KeyboardKeyInputProps(
                                pos = Vector2f(480f, 0f), getter = prop.first, setter = prop.second
                        ))
                    }
                }
            }
        }

        +TextButton("", "Reset defaults", 300f, 505f, 90f, 24f).apply {
            onClick {
                setState { copy(editingConfig = mergeControls(editingConfig, getDefaultConfig())) }
            }
        }
    }

    fun RBuilder.aboutTab() {
        val width = 700f

        div {
            style {
                posX = 20f
                posY = 60f
                sizeX = 660f
                sizeY = 430f
                classes("popup_section")
            }
            +FixedLabel("Modeler made by Cout970",
                    0f, 10f, width, 24f).apply {
                fontSize(22f)
                textState.horizontalAlign = HorizontalAlign.CENTER
            }
            +FixedLabel("Special thanks to:",
                    10f, 40f, width - 40f, 24f).apply {
                fontSize(22f)
                textState.horizontalAlign = HorizontalAlign.LEFT
            }
            +FixedLabel("- MechWarrior99 for the inspiration to start the project, the initial gui design and testing",
                    10f, 60f, width - 40f, 24f).apply {
                fontSize(22f)
                textState.horizontalAlign = HorizontalAlign.LEFT
            }
            +FixedLabel("- ShchAlexander for develop Legui and support the project",
                    10f, 80f, width - 40f, 24f).apply {
                fontSize(22f)
                textState.horizontalAlign = HorizontalAlign.LEFT
            }
            +FixedLabel("Technologies used:",
                    10f, 110f, width - 40f, 24f).apply {
                fontSize(22f)
                textState.horizontalAlign = HorizontalAlign.LEFT
            }
            +FixedLabel("- Kotlin: made by Jetbrains",
                    10f, 130f, width - 40f, 24f).apply {
                fontSize(22f)
                textState.horizontalAlign = HorizontalAlign.LEFT
            }
            +FixedLabel("- Legui: made by ShchAlexander",
                    10f, 150f, width - 40f, 24f).apply {
                fontSize(22f)
                textState.horizontalAlign = HorizontalAlign.LEFT
            }
            +FixedLabel("- LWJGL: from the LWJGL Team",
                    10f, 170f, width - 40f, 24f).apply {
                fontSize(22f)
                textState.horizontalAlign = HorizontalAlign.LEFT
            }
            +FixedLabel("Source code:",
                    10f, 200f, width - 40f, 24f).apply {
                fontSize(22f)
                textState.horizontalAlign = HorizontalAlign.LEFT
            }
            +FixedLabel("https://github.com/cout970/Modeler",
                    10f, 220f, width - 40f, 24f).apply {
                fontSize(22f)
                textState.horizontalAlign = HorizontalAlign.LEFT
                textState.textColor = Vector4f(0f, 119f / 255f, 204f / 255f, 1.0f)
                onClick {
                    openLink("https://github.com/cout970/Modeler")
                }
            }
            +FixedLabel("You can ask for support at the Magneticraft discord:",
                    10f, 260f, width - 40f, 24f).apply {
                fontSize(22f)
                textState.horizontalAlign = HorizontalAlign.LEFT
            }
            +FixedLabel("Discord",
                    10f, 280f, width - 40f, 24f).apply {
                fontSize(22f)
                textState.horizontalAlign = HorizontalAlign.LEFT
                textState.textColor = Vector4f(0f, 119f / 255f, 204f / 255f, 1.0f)
                onClick {
                    openLink("https://discord.gg/EhYbA97")
                }
            }
        }
    }

    fun openLink(link: String) {
        val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(URI(link))
            } catch (e: Exception) {
                e.print()
            }
        }
    }

    fun updateUser(user: Author) {
        Config.user = user
        rerender()
    }

    fun updateProjectProperties(properties: ProjectProperties) {
        props.propertyHolder.updateProperties(properties)
        rerender()
    }

    fun KMutableProperty<*>.getTooltip(): String? {
        return annotations
                .filterIsInstance<ConfigComment>()
                .firstOrNull()
                ?.run { comment } ?: return null
    }

    fun getProperties(): List<Triple<String, String?, Pair<() -> Float, (Float) -> Unit>>> {
        return Config::class
                .memberProperties
                .filterIsInstance<KMutableProperty<*>>()
                .filter { it.returnType.javaType == Float::class.java }
                .map {
                    Triple(
                            it.name,
                            it.getTooltip(),
                            Pair(
                                    { state.editingConfig[it.name].asFloat },
                                    { value: Float ->
                                        state.editingConfig.remove(it.name)
                                        state.editingConfig.addProperty(it.name, value)
                                        rerender()
                                    }
                            )
                    )
                }
    }

    fun getMouseKeyBinds(): List<Triple<String, String?, Pair<() -> MouseKeyBind, (MouseKeyBind) -> Unit>>> {
        val gson = Gson()
        return KeyBindings::class
                .memberProperties
                .filterIsInstance<KMutableProperty<*>>()
                .filter { it.returnType.javaType == MouseKeyBind::class.java }
                .map {
                    Triple(it.name,
                            it.getTooltip(),
                            Pair(
                                    {
                                        val keybinds = state.editingConfig["keyBindings"].asJsonObject
                                        gson.fromJson(keybinds[it.name], MouseKeyBind::class.java)
                                    },
                                    { value: MouseKeyBind ->
                                        val keybinds = state.editingConfig["keyBindings"].asJsonObject
                                        keybinds.remove(it.name)
                                        keybinds.add(it.name, gson.toJsonTree(value))
                                        rerender()
                                    }
                            )
                    )
                }
    }

    fun getKeyBinds(): List<Triple<String, String?, Pair<() -> KeyBind, (KeyBind) -> Unit>>> {
        val gson = Gson()
        return KeyBindings::class
                .memberProperties
                .filterIsInstance<KMutableProperty<*>>()
                .filter { it.returnType.javaType == KeyBind::class.java }
                .map {
                    Triple(it.name,
                            it.getTooltip(),
                            Pair(
                                    {
                                        val keybinds = state.editingConfig["keyBindings"].asJsonObject
                                        gson.fromJson(keybinds[it.name], KeyBind::class.java)
                                    },
                                    { value: KeyBind ->
                                        val keybinds = state.editingConfig["keyBindings"].asJsonObject
                                        keybinds.remove(it.name)
                                        keybinds.add(it.name, gson.toJsonTree(value))
                                        rerender()
                                    }
                            )
                    )
                }
    }

    fun mergeParameters(current: JsonObject, new: JsonObject): JsonObject {
        listOf("keyBindings", "user", "logLevel", "colorPalette").forEach {
            new.remove(it)
            new.add(it, current[it])
        }
        return new
    }

    fun mergeControls(current: JsonObject, new: JsonObject): JsonObject {
        current.entrySet().forEach { (key, value) ->
            if (key != "keyBindings") {
                new.remove(key)
                new.add(key, value)
            }
        }
        return new
    }

    enum class Tab {
        PROJECT, PARAMETERS, CONTROLS, ABOUT
    }

    data class State(val tab: Tab, val previousConfig: JsonObject, val editingConfig: JsonObject) : RState
}

class MouseButtonInputProps(
        val pos: Vector2f,
        val getter: () -> MouseKeyBind,
        val setter: (MouseKeyBind) -> Unit
) : RProps

class MouseButtonInput : RComponent<MouseButtonInputProps, MouseButtonInput.State>() {

    private var context: Context? = null
    private var lastComponent: Component? = null

    override fun getInitialState() = State(true)

    override fun RBuilder.render() = div("MouseButtonInput") {
        style {
            position.set(props.pos)
            width = 150f
            height = 24f
            classes("popup_parameter_item_value")
        }

        val text = if (state.showMode) getMouseButtonName(props.getter().button) else "Press new button"

        comp(TextButton("", text, 0f, 0f, 150f, 24f)) {
            style {
                background { grey }
                horizontalAlign = HorizontalAlign.LEFT
                fontSize = 20f
                paddingLeft(5f)
            }

            postMount {
                context?.let { ctx ->
                    if (lastComponent != null && ctx.focusedGui == lastComponent) {
                        ctx.focusedGui = this
                        this.isFocused = true
                    }
                    lastComponent = null
                }
                context = null
            }

            on<MouseClickEvent<TextButton>> {
                if (state.showMode) {
                    if (it.action == MouseClickEvent.MouseClickAction.CLICK) {
                        context = it.context
                        lastComponent = it.targetComponent
                        setState { copy(showMode = false) }
                    }
                } else {
                    if (it.action == MouseClickEvent.MouseClickAction.PRESS) {
                        props.setter(MouseKeyBind(it.button.code))
                        setState { copy(showMode = true) }
                    }
                }
            }

            onFocus {
                if (!it.isFocused && !state.showMode) {
                    setState { copy(showMode = true) }
                }
            }
        }
    }

    fun getMouseButtonName(button: Int): String = when (button) {
        Mouse.BUTTON_LEFT -> "Left button"
        Mouse.BUTTON_MIDDLE -> "Middle button"
        Mouse.BUTTON_RIGHT -> "Right button"
        else -> "Unknown button"
    }

    data class State(val showMode: Boolean) : RState
}

class KeyboardKeyInputProps(
        val pos: Vector2f,
        val getter: () -> KeyBind,
        val setter: (KeyBind) -> Unit
) : RProps

class KeyboardKeyInput : RComponent<KeyboardKeyInputProps, KeyboardKeyInput.State>() {

    private var context: Context? = null
    private var lastComponent: Component? = null

    override fun getInitialState() = State(true)

    override fun RBuilder.render() = div("KeyboardKeyInput") {
        style {
            position.set(props.pos)
            width = 150f
            height = 24f
            classes("popup_parameter_item_value")
        }

        val text = if (state.showMode) props.getter().getName() else "Press new key"

        comp(StringInput("", text, 0f, 0f, 150f, 24f)) {
            style {
                background { grey }
            }

            postMount {
                context?.let { ctx ->
                    if (lastComponent != null && ctx.focusedGui == lastComponent) {
                        ctx.focusedGui = this
                        this.isFocused = true
                    }
                    lastComponent = null
                }
                context = null
            }

            onClick {
                if (state.showMode) {
                    context = it.context
                    lastComponent = it.targetComponent
                    setState { copy(showMode = false) }
                }
            }

            onFocus {
                if (!it.isFocused && !state.showMode) {
                    setState { copy(showMode = true) }
                }
            }

            onKey {
                if (!state.showMode) {
                    if (it.action == GLFW.GLFW_RELEASE) {
                        setKey(it.key, it.mods)
                        setState { copy(showMode = true) }
                    }
                }
            }
        }
    }

    fun setKey(key: Int, mods: Int) {

        val modifiers = mutableListOf<KeyboardModifiers>()
        if (mods and GLFW.GLFW_MOD_CONTROL != 0) modifiers += KeyboardModifiers.CTRL
        if (mods and GLFW.GLFW_MOD_ALT != 0) modifiers += KeyboardModifiers.ALT
        if (mods and GLFW.GLFW_MOD_SHIFT != 0) modifiers += KeyboardModifiers.SHIFT
        if (mods and GLFW.GLFW_MOD_SUPER != 0) modifiers += KeyboardModifiers.SUPER

        props.setter(KeyBind(key, *modifiers.toTypedArray()))
    }

    data class State(val showMode: Boolean) : RState
}