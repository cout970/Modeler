package com.cout970.modeler.gui.components

import com.cout970.modeler.core.config.*
import com.cout970.modeler.core.log.print
import com.cout970.modeler.core.project.Author
import com.cout970.modeler.core.project.IProjectPropertiesHolder
import com.cout970.modeler.core.project.ProjectProperties
import com.cout970.modeler.gui.Popup
import com.cout970.modeler.gui.leguicomp.*
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponent
import com.cout970.modeler.gui.reactive.RComponentSpec
import com.cout970.modeler.gui.reactive.invoke
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import com.cout970.modeler.util.text
import com.cout970.reactive.dsl.*
import com.cout970.vector.extensions.vec2Of
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.ScrollablePanel
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.style.color.ColorConstants
import java.awt.Desktop
import java.net.URI
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType


class ConfigMenu : RComponent<ConfigMenu.Props, ConfigMenu.State>() {

    init {
        state = State(Tab.PROJECT)
    }

    @Suppress("UNCHECKED_CAST")
    override fun build(ctx: RBuilder): Component = panel {
        style.background.color = Vector4f(1f, 1f, 1f, 0.05f)
        fill()

        // Centered panel
        +panel {
            width = 700f
            height = 550f
            center()
            border(2f) { greyColor }
            background { darkestColor }

            tabs()

            when (state.tab) {
                ConfigMenu.Tab.PROJECT -> projectTab()
                ConfigMenu.Tab.PARAMETERS -> parametersTab()
                ConfigMenu.Tab.CONTROLS -> controlsTab()
                ConfigMenu.Tab.ABOUT -> aboutTab()
            }

            // padding: 20px
            +TextButton("", "Save", 510f, 505f, 80f, 24f).apply {
                listenerMap.addListener(MouseClickEvent::class.java) {
                    ConfigManager.saveConfig()
                }
            }
            +TextButton("", "Exit", 600f, 505f, 80f, 24f).apply {
                listenerMap.addListener(MouseClickEvent::class.java) {
                    props.popup.returnFunc(null)
                }
            }
        }
    }

    fun Panel.tabs() {
        +panel {
            background { darkestColor }
            setBorderless()
            posX = 20f
            posY = 20f
            width = 660f
            height = 32f

            +TextButton("", "Project", 0f, 0f, 80f, 32f).apply {
                if (state.tab == Tab.PROJECT) background { greyColor } else background { darkColor }
                textState.fontSize = 18f
                style.border = PixelBorder().apply { enableRight = true }
                onClick { replaceState(state.copy(tab = Tab.PROJECT)) }
            }
            +TextButton("", "Parameters", 80f, 0f, 80f, 32f).apply {
                if (state.tab == Tab.PARAMETERS) background { greyColor } else background { darkColor }
                textState.fontSize = 18f
                style.border = PixelBorder().apply { enableRight = true }
                onClick { replaceState(state.copy(tab = Tab.PARAMETERS)) }
            }
            +TextButton("", "Controls", 160f, 0f, 80f, 32f).apply {
                if (state.tab == Tab.CONTROLS) background { greyColor } else background { darkColor }
                textState.fontSize = 18f
                style.border = PixelBorder().apply { enableRight = true }
                onClick { replaceState(state.copy(tab = Tab.CONTROLS)) }
            }
            +TextButton("", "About", 240f, 0f, 80f, 32f).apply {
                if (state.tab == Tab.ABOUT) background { greyColor } else background { darkColor }
                textState.fontSize = 18f
                onClick { replaceState(state.copy(tab = Tab.ABOUT)) }
            }
        }
    }

    fun Panel.projectTab() {
        +panel {
            posX = 20f
            posY = 80f
            width = 660f
            height = 125f + 80f
            background { darkColor }

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
                onEnterPress = onEnterPress
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
                onEnterPress = onEnterPress
            }
        }

        +panel {
            posX = 20f
            posY = 130f + 180f
            width = 660f
            height = 125f
            background { darkColor }

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
                onEnterPress = onEnterPress
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
                onEnterPress = onEnterPress
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
                onEnterPress = onLoseFocus
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun Panel.parametersTab() {
        +ScrollablePanel(20f, 60f, 660f, 430f).apply {
            background { darkColor }
            horizontalScrollBar.hide()
            horizontalScrollBar.size.y = 10f

            container.run {
                val properties = getProperties()
                setTransparent()
                width = 650f
                height = properties.size * 30f + 10f
                verticalScrollBar.visibleAmount = 10f

                properties.sortedBy { it.first }.forEachIndexed { index, (name, prop) ->

                    add(panel {
                        background { darkestColor }
                        setBorderless()
                        posX = 5f
                        posY = 6f + index * 30f
                        width = 631f
                        height = 24f

                        +FixedLabel(name.capitalize(), 10f, 0f, 100f, 24f).apply {
                            textState.horizontalAlign = HorizontalAlign.LEFT
                            textState.fontSize = 20f
                            prop.getTooltip()?.let { tooltip = InstantTooltip(it) }
                        }

                        +FloatInput {
                            FloatInput.Props(vec2Of(510f, 0f), prop as KMutableProperty<Float>, Config)
                        }
                    })
                }
            }
        }
    }

    fun Panel.controlsTab() {
        +ScrollablePanel(20f, 60f, 660f, 430f).apply {
            background { darkColor }
            horizontalScrollBar.hide()
            horizontalScrollBar.size.y = 10f

            container.run {
                val mouseKeybinds = getMouseKeyBinds()
                val keybinds = getKeyBinds()
                setTransparent()
                width = 650f
                height = mouseKeybinds.size * 30f + keybinds.size * 30f + 10f
                verticalScrollBar.visibleAmount = 10f

                mouseKeybinds.sortedBy { it.first }.forEachIndexed { index, (name, prop) ->

                    add(panel {
                        background { darkestColor }
                        setBorderless()
                        posX = 5f
                        posY = 6f + index * 30f
                        width = 631f
                        height = 24f

                        +FixedLabel(name.capitalize(), 10f, 0f, 100f, 24f).apply {
                            textState.horizontalAlign = HorizontalAlign.LEFT
                            textState.fontSize = 20f
                            prop.getTooltip()?.let { tooltip = InstantTooltip(it) }
                        }

//                        +MouseButtonInput {
//                                        MouseButtonInput.Props(vec2Of(480f, 0f),
//                                                prop as KMutableProperty<MouseKeyBind>,
//                                                Config.keyBindings)
//                        }
                    })
                }

                keybinds.sortedBy { it.first }.forEachIndexed { index, (name, prop) ->

                    add(panel {
                        background { darkestColor }
                        setBorderless()
                        posX = 5f
                        posY = 6f + index * 30f + mouseKeybinds.size * 30f
                        width = 631f
                        height = 24f

                        +FixedLabel(name.capitalize(), 10f, 0f, 100f, 24f).apply {
                            textState.horizontalAlign = HorizontalAlign.LEFT
                            textState.fontSize = 20f
                            prop.getTooltip()?.let { tooltip = InstantTooltip(it) }
                        }

//                        +KeyboardKeyInput {
//                            KeyboardKeyInput.Props(vec2Of(480f, 0f),
//                                    prop as KMutableProperty<KeyBind>,
//                                    Config.keyBindings)
//                        }
                    })
                }
            }
        }
    }

    fun Panel.aboutTab() {
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
        rebuild()
    }

    fun updateProjectProperties(properties: ProjectProperties) {
        props.propertyHolder.updateProperties(properties)
        rebuild()
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

    enum class Tab {
        PROJECT, PARAMETERS, CONTROLS, ABOUT
    }

    data class State(val tab: Tab)

    class Props(
            val popup: Popup,
            val propertyHolder: IProjectPropertiesHolder
    )

    companion object : RComponentSpec<ConfigMenu, Props, State>
}