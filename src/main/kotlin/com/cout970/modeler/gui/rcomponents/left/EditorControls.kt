package com.cout970.modeler.gui.rcomponents.left

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.GuiState
import com.cout970.modeler.gui.leguicomp.classes
import com.cout970.modeler.gui.leguicomp.defaultTextColor
import com.cout970.modeler.gui.leguicomp.onClick
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RComponent
import com.cout970.reactive.core.RProps
import com.cout970.reactive.dsl.*
import com.cout970.reactive.nodes.DivBuilder
import com.cout970.reactive.nodes.child
import com.cout970.reactive.nodes.div
import com.cout970.reactive.nodes.style
import org.joml.Vector2f
import org.liquidengine.legui.component.CheckBox
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.icon.CharIcon

class EditorControlsProps(val guiState: GuiState) : RProps

class EditorControls : RComponent<EditorControlsProps, VisibleWidget>() {

    override fun getInitialState() = VisibleWidget(false)

    override fun RBuilder.render() = div("EditorControls") {
        style {
            classes("left_panel_group", "editor_controls")
            height = if (state.on) 12f * 26f else 24f
        }

        postMount {
            marginX(5f)
        }

        child(GroupTitle::class.java, GroupTitleProps("Editor Controls", state.on) { setState { copy(on = !on) } })

        div {
            style {
                transparent()
                borderless()
                posY = 24f
                sizeY = 12f * 24f
            }

            postMount {
                marginX(5f)
                floatTop(2f, 3f)
            }

            checkbox("Texture grid lines", "drawTextureGridLines")
            checkbox("Render lights", "renderLights")
            checkbox("Use textures", "useTexture")
            checkbox("Use colors", "useColor")
            checkbox("Use light", "useLight")
            checkbox("Show transparent sides", "showInvisible")
            checkbox("Render base cube", "renderBase")
            checkbox("Render texture marks", "drawTextureProjection")
            checkbox("Render skybox", "renderSkybox")
            checkbox("Draw face outline", "drawOutline")
            checkbox("Sync selection render", "syncSelection")
        }
    }

    fun DivBuilder.checkbox(text: String, property: String) {
        val prop = props.guiState.getBooleanProperties()[property]!!
        checkbox(text, { prop.get() }, { prop.set(it) })
    }

    fun DivBuilder.checkbox(text: String, isCheckedFunc: () -> Boolean, checkFunc: (Boolean) -> Unit) {
        +CheckBox(text, 0f, 0f, 260f, 24f).apply {
            defaultTextColor()
            classes("checkbox")
            isChecked = isCheckedFunc()
            if (isChecked) classes("checkbox_active")

            configIcon(iconChecked as CharIcon)
            configIcon(iconUnchecked as CharIcon)

            postMount {
                centerX()
            }

            onClick { checkFunc(isChecked); rerender() }
        }
    }

    private fun CheckBox.configIcon(icon: CharIcon) {
        icon.color = Config.colorPalette.bright4.toColor()
        icon.size = Vector2f(24f, 24f)
        icon.position = Vector2f(size.x - icon.size.x, 0f)
        icon.horizontalAlign = HorizontalAlign.CENTER
    }
}