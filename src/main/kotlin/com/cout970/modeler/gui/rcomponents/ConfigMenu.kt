package com.cout970.modeler.gui.rcomponents

import com.cout970.modeler.core.config.*
import com.cout970.modeler.core.config.ConfigManager.getDefaultConfig
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.project.Author
import com.cout970.modeler.core.project.IProjectPropertiesHolder
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.gui.components.KeyboardKeyInput
import com.cout970.modeler.gui.components.MouseButtonInput
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.invoke
import com.cout970.modeler.util.text
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.core.RState
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import com.cout970.vector.extensions.vec2Of
import com.google.gson.JsonObject
import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.style.color.ColorConstants
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
            border(2f) { greyColor }
            background { darkestColor }
            width = 700f
            height = 550f
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
                background { darkColor }
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
                background { darkestColor }
                borderless()
                posX = 20f
                posY = 20f
                width = 660f
                height = 32f
            }

            +TextButton("", "Project", 0f, 0f, 80f, 32f).apply {
                if (state.tab == Tab.PROJECT) background { greyColor } else background { darkColor }
                textState.fontSize = 18f
                style.border = PixelBorder().apply { enableRight = true }
                onClick { setState { copy(tab = Tab.PROJECT) } }
            }
            +TextButton("", "Parameters", 80f, 0f, 80f, 32f).apply {
                if (state.tab == Tab.PARAMETERS) background { greyColor } else background { darkColor }
                textState.fontSize = 18f
                style.border = PixelBorder().apply { enableRight = true }
                onClick { setState { copy(tab = Tab.PARAMETERS) } }
            }
            +TextButton("", "Controls", 160f, 0f, 80f, 32f).apply {
                if (state.tab == Tab.CONTROLS) background { greyColor } else background { darkColor }
                textState.fontSize = 18f
                style.border = PixelBorder().apply { enableRight = true }
                onClick { setState { copy(tab = Tab.CONTROLS) } }
            }
            +TextButton("", "About", 240f, 0f, 80f, 32f).apply {
                if (state.tab == Tab.ABOUT) background { greyColor } else background { darkColor }
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
                background { darkColor }
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
                background { darkestColor }
                textState.padding.z = 10f
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
                background { darkestColor }
                textState.padding.z = 10f
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
                background { darkestColor }
                textState.padding.z = 10f
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
                background { darkColor }
                posX = 20f
                posY = 130f + 180f
                width = 660f
                height = 125f
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
                background { darkestColor }
                textState.padding.z = 10f
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
                background { darkestColor }
                textState.padding.z = 10f
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
                background { darkestColor }
                textState.padding.z = 10f
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
        scrollPanel {
            style {
                background { darkColor }
                posX = 20f
                posY = 60f
                sizeX = 660f
                sizeY = 430f
            }

            horizontalScroll { hide() }

            val properties = getProperties()

            verticalScroll {
                if (properties.size * 30f + 10f > 430f) {
                    visibleAmount = 50f
                } else {
                    hide()
                }
            }

            container {

                style {
                    transparent()
                    width = 650f
                    height = properties.size * 30f + 10f
                }

                properties.sortedBy { it.first }.forEachIndexed { index, (name, tooltip, prop) ->

                    div {
                        style {
                            background { darkestColor }
                            borderless()
                            posX = 5f
                            posY = 6f + index * 30f
                            width = 631f
                            height = 24f
                        }

                        +FixedLabel(name.capitalize(), 10f, 0f, 100f, 24f).apply {
                            textState.horizontalAlign = HorizontalAlign.LEFT
                            textState.fontSize = 20f
                            tooltip?.let { this.tooltip = InstantTooltip(it) }
                        }

                        child(TinyFloatInput::class, TinyFloatInputProps(
                                Vector2f(510f, 0f), prop.first, prop.second
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
        scrollPanel {
            style {
                background { darkColor }
                posX = 20f
                posY = 60f
                sizeX = 660f
                sizeY = 430f
            }

            horizontalScroll { hide() }

            verticalScroll {
                visibleAmount = 10f
            }

            container {

                style {
                    transparent()
                    borderless()
                }

                val mouseKeybinds = getMouseKeyBinds()
                val keybinds = getKeyBinds()

                style {
                    transparent()
                    width = 650f
                    height = mouseKeybinds.size * 30f + keybinds.size * 30f + 10f
                }

                mouseKeybinds.sortedBy { it.first }.forEachIndexed { index, (name, prop) ->

                    div {
                        style {
                            background { darkestColor }
                            borderless()
                            posX = 5f
                            posY = 6f + index * 30f
                            width = 631f
                            height = 24f
                        }

                        +FixedLabel(name.capitalize(), 10f, 0f, 100f, 24f).apply {
                            textState.horizontalAlign = HorizontalAlign.LEFT
                            textState.fontSize = 20f
                            prop.getTooltip()?.let { tooltip = InstantTooltip(it) }
                        }

                        // TODO
                        +MouseButtonInput {
                            MouseButtonInput.Props(vec2Of(480f, 0f),
                                    prop as KMutableProperty<MouseKeyBind>,
                                    Config.keyBindings)
                        }
                    }
                }

                keybinds.sortedBy { it.first }.forEachIndexed { index, (name, prop) ->

                    div {
                        style {
                            background { darkestColor }
                            borderless()
                            posX = 5f
                            posY = 6f + index * 30f + mouseKeybinds.size * 30f
                            width = 631f
                            height = 24f
                        }

                        +FixedLabel(name.capitalize(), 10f, 0f, 100f, 24f).apply {
                            textState.horizontalAlign = HorizontalAlign.LEFT
                            textState.fontSize = 20f
                            prop.getTooltip()?.let { tooltip = InstantTooltip(it) }
                        }

                        // TODO
                        +KeyboardKeyInput {
                            KeyboardKeyInput.Props(vec2Of(480f, 0f),
                                    prop as KMutableProperty<KeyBind>,
                                    Config.keyBindings)
                        }
                    }
                }
            }
        }
    }

    fun RBuilder.aboutTab() {
        val width = 700f

        +FixedLabel("Modeler made by Cout970",
                0f, 60f, width, 24f).apply {
            fontSize(22f)
            textState.horizontalAlign = HorizontalAlign.CENTER
        }
        +FixedLabel("Special thanks to:",
                20f, 90f, width - 40f, 24f).apply {
            fontSize(22f)
            textState.horizontalAlign = HorizontalAlign.LEFT
        }
        +FixedLabel("- MechWarrior99 for the inspiration to start the project, the initial gui design and testing",
                20f, 110f, width - 40f, 24f).apply {
            fontSize(22f)
            textState.horizontalAlign = HorizontalAlign.LEFT
        }
        +FixedLabel("- ShchAlexander for develop Legui and support the project",
                20f, 130f, width - 40f, 24f).apply {
            fontSize(22f)
            textState.horizontalAlign = HorizontalAlign.LEFT
        }
        +FixedLabel("Technologies used:",
                20f, 160f, width - 40f, 24f).apply {
            fontSize(22f)
            textState.horizontalAlign = HorizontalAlign.LEFT
        }
        +FixedLabel("- Kotlin: made by Jetbrains",
                20f, 180f, width - 40f, 24f).apply {
            fontSize(22f)
            textState.horizontalAlign = HorizontalAlign.LEFT
        }
        +FixedLabel("- Legui: made by ShchAlexander",
                20f, 200f, width - 40f, 24f).apply {
            fontSize(22f)
            textState.horizontalAlign = HorizontalAlign.LEFT
        }
        +FixedLabel("- LWJGL: from the LWJGL Team",
                20f, 220f, width - 40f, 24f).apply {
            fontSize(22f)
            textState.horizontalAlign = HorizontalAlign.LEFT
        }
        +FixedLabel("Source code:",
                20f, 250f, width - 40f, 24f).apply {
            fontSize(22f)
            textState.horizontalAlign = HorizontalAlign.LEFT
        }
        +FixedLabel("https://github.com/cout970/Modeler",
                20f, 270f, width - 40f, 24f).apply {
            fontSize(22f)
            textState.horizontalAlign = HorizontalAlign.LEFT
            textState.textColor = Vector4f(0f, 119f / 255f, 204f / 255f, 1.0f)
            onClick {
                openLink("https://github.com/cout970/Modeler")
            }
        }
        +FixedLabel("You can ask for support at the Magneticraft discord:",
                20f, 310f, width - 40f, 24f).apply {
            fontSize(22f)
            textState.horizontalAlign = HorizontalAlign.LEFT
        }
        +FixedLabel("Discord",
                20f, 330f, width - 40f, 24f).apply {
            fontSize(22f)
            textState.horizontalAlign = HorizontalAlign.LEFT
            textState.textColor = Vector4f(0f, 119f / 255f, 204f / 255f, 1.0f)
            onClick {
                openLink("https://discord.gg/EhYbA97")
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

    fun getMouseKeyBinds(): List<Pair<String, KMutableProperty<*>>> {
        return KeyBindings::class
                .memberProperties
                .filterIsInstance<KMutableProperty<*>>()
                .filter { it.returnType.javaType == MouseKeyBind::class.java }
                .map { it.name to it }
    }

    fun getKeyBinds(): List<Pair<String, KMutableProperty<*>>> {
        return KeyBindings::class
                .memberProperties
                .filterIsInstance<KMutableProperty<*>>()
                .filter { it.returnType.javaType == KeyBind::class.java }
                .map { it.name to it }
    }

    fun mergeParameters(current: JsonObject, new: JsonObject): JsonObject {
        listOf("keyBindings", "user", "logLevel", "colorPalette").forEach {
            new.remove(it)
            new.add(it, current[it])
        }
        return new
    }

    enum class Tab {
        PROJECT, PARAMETERS, CONTROLS, ABOUT
    }

    data class State(val tab: Tab, val previousConfig: JsonObject, val editingConfig: JsonObject) : RState
}