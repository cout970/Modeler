package com.cout970.modeler.gui

import com.cout970.modeler.core.config.ColorPalette.Companion.colorOf
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.reactive.dsl.hide
import com.cout970.reactive.dsl.show
import jdk.nashorn.api.scripting.ScriptObjectMirror
import org.joml.Vector4f
import org.liquidengine.legui.component.*
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.style.Style
import org.liquidengine.legui.style.border.SimpleLineBorder
import org.liquidengine.legui.theme.*
import java.io.File
import javax.script.ScriptEngineManager

object CSSTheme : Theme(createThemeManager()) {

    private lateinit var style: Map<Selector, ScriptObjectMirror>

    init {
        loadCss()
    }

    fun loadCss() {
        val engine = ScriptEngineManager().getEngineByExtension("js")
        val obj = engine.eval(File("../src/main/resources/assets/style.js").reader()) as ScriptObjectMirror
        style = obj.keys.map { it.toSelector() to (obj[it] as ScriptObjectMirror) }.toMap()
    }

    private fun String.toSelector(): Selector {
        if (!contains(":")) return Selector(this)
        return Selector(substringBefore(":"), substringAfter(":"))
    }

    fun applyComp(component: Component) {
        val classes = component.metadata["classes"] ?: return
        if (classes is String) {
            val modes = mapOf(
                    null to component.style,
                    "hover" to component.hoveredStyle,
                    "focus" to component.focusedStyle,
                    "pressed" to component.pressedStyle
            )

            modes.forEach { type, styleObj ->
                classes.split(",").forEach classLoop@{ thisClass ->
                    val style = style[Selector(thisClass, type)] ?: return@classLoop
                    applyStyle(style, styleObj, component)
                }
            }
        }
    }

    private fun applyStyle(style: ScriptObjectMirror, styleObj: Style, comp: Component) {
        style.getString("display") { display ->
            when (display) {
                "none" -> comp.hide()
                "block" -> comp.also { it.show(); styleObj.display = Style.DisplayType.MANUAL }
                "fex" -> comp.also { it.show(); styleObj.display = Style.DisplayType.FLEX }
            }
        }
        style.getColor("backgroundColor") { styleObj.background.color = it }
        style.getString("borderStyle") {
            when (it) {
                "solid" -> styleObj.border = SimpleLineBorder()
                "none" -> styleObj.border = null
            }
        }
        style.getFloat("borderWidth") { (styleObj.border as SimpleLineBorder).thickness = it }
        style.getFloat("borderRadius") { styleObj.setBorderRadius(it) }
        style.getColor("borderColor") { (styleObj.border as SimpleLineBorder).color = it }

        if (comp is ScrollBar) {
            style.getColor("scrollColor") { comp.scrollColor = it }
        }

        if (comp is ToggleButton) {
            style.getColor("toggledBackgroundColor") { comp.toggledBackgroundColor = it }
        }

        if (comp is TextComponent) {
            style.getString("textAlign") {
                when (it) {
                    "right" -> comp.textState.horizontalAlign = HorizontalAlign.RIGHT
                    "left" -> comp.textState.horizontalAlign = HorizontalAlign.LEFT
                    "center" -> comp.textState.horizontalAlign = HorizontalAlign.CENTER
                    else -> log(Level.DEBUG) { "Invalid textAlign value: $it" }
                }
            }
            style.getFloat("textSize") { comp.textState.fontSize = it }
        }
    }

    private inline fun ScriptObjectMirror.getColor(clazz: String, func: (Vector4f) -> Unit) {
        get(clazz) {
            func(colorOf(this as String))
        }
    }

    private inline fun ScriptObjectMirror.getInt(clazz: String, func: (Int) -> Unit) {
        get(clazz) { func((this as String).toInt()) }
    }

    private inline fun ScriptObjectMirror.getFloat(clazz: String, func: (Float) -> Unit) {
        get(clazz) {
            if (this is String) {
                func(this.toFloat())
            } else if (this is Number) {
                func(this.toFloat())
            }
        }
    }

    private inline fun ScriptObjectMirror.getString(clazz: String, func: (String) -> Unit) {
        get(clazz) { func(this as String) }
    }

    private inline fun ScriptObjectMirror.get(clazz: String, func: Any.() -> Unit) {
        if (hasMember(clazz)) {
            getMember(clazz).func()
        }
    }
}

private data class Selector(val key: String, val mode: String? = null)

private fun createThemeManager(): ThemeManager {
    val m = DefaultThemeManager()
    m.addComponent<Button>()
    m.addComponent<Panel>()
    m.addComponent<CheckBox>()
    m.addComponent<Component>()
    m.addComponent<Label>()
    m.addComponent<LayerContainer>()
    m.addComponent<ProgressBar>()
    m.addComponent<ScrollablePanel>()
    m.addComponent<RadioButton>()
    m.addComponent<ScrollBar>()
    m.addComponent<SelectBox>()
    m.addComponent<SelectBox.SelectBoxScrollablePanel>()
    m.addComponent<SelectBox.SelectBoxElement>()
    m.addComponent<Slider>()
    m.addComponent<TextArea>()
    m.addComponent<TextInput>()
    m.addComponent<ToggleButton>()
    m.addComponent<Tooltip>()
    m.addComponent<Widget>()
    return m
}

private inline fun <reified T : Component> DefaultThemeManager.addComponent() {
    setComponentTheme(T::class.java, object : AbstractTheme<T>() {
        override fun apply(comp: T) {
            // Apply default theme
            Themes.FLAT_DARK.themeManager.getComponentTheme(T::class.java).applyAll(comp)
            CSSTheme.applyComp(comp)
        }
    })
}