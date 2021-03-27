package com.cout970.modeler.gui

import com.cout970.modeler.Debugger
import com.cout970.modeler.core.config.colorOf
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.core.log.print
import com.cout970.reactive.dsl.hide
import com.cout970.reactive.dsl.show
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of
import jdk.nashorn.api.scripting.ScriptObjectMirror
import org.joml.Vector4f
import org.liquidengine.legui.component.*
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.component.optional.align.VerticalAlign
import org.liquidengine.legui.icon.CharIcon
import org.liquidengine.legui.style.Style
import org.liquidengine.legui.style.border.SimpleLineBorder
import org.liquidengine.legui.theme.*
import java.io.File
import javax.script.ScriptEngineManager

object CSSTheme : Theme(createThemeManager()) {

    private val engine = ScriptEngineManager(null).getEngineByExtension("js")
    private lateinit var style: Map<Selector, ScriptObjectMirror>

    init {
        loadCss()
    }

    fun loadCss() {
        try {
            val reader = if (Debugger.STATIC_DEBUG) {
                File("../src/main/resources/assets/style.js").reader()
            } else {
                Thread.currentThread().contextClassLoader.getResourceAsStream("assets/style.js")!!.reader()
            }
            val obj = engine.eval(reader) as ScriptObjectMirror
            style = obj.keys.map { it.toSelector() to (obj[it] as ScriptObjectMirror) }.toMap()
        } catch (e: Exception) {
            log(Level.ERROR) { "Unable to load style file (style.js)" }
            e.print()
        }
    }

    private fun String.toSelector(): Selector {
        if (!contains(":")) return Selector(this)
        return Selector(substringBefore(":"), substringAfter(":"))
    }

    fun getColor(name: String): IVector3 {
        val som = style[Selector("colors")] ?: error("Missing 'colors' section in style.js")

        if (som.hasMember(name)) {
            val color = colorOf(som.getMember(name) as String)
            return vec3Of(color.x, color.y, color.z)
        }

        return Vector3.ONE
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

            modes.forEach { (type, styleObj) ->
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
        style.getFloat("borderWidth") { (styleObj.border as? SimpleLineBorder)?.thickness = it }
        style.getFloat("borderRadius") { styleObj.setBorderRadius(it) }
        style.getColor("borderColor") { (styleObj.border as? SimpleLineBorder)?.color = it }
        style.getColor("focusedStrokeColor") { styleObj.focusedStrokeColor = it }

        if (comp is ScrollBar) {
            style.getColor("scrollColor") { comp.scrollColor = it }
            style.getColor("arrowColor") { comp.arrowColor = it }
            style.getColor("arrowColor") { comp.arrowColor = it }
        }

        if (comp is ToggleButton) {
            style.getColor("toggledBackgroundColor") { comp.toggledBackgroundColor = it }
        }

        if (comp is CheckBox) {
            style.getColor("checkedIconColor") { (comp.iconChecked as CharIcon).color = it }
            style.getColor("uncheckedIconColor") { (comp.iconUnchecked as CharIcon).color = it }
        }

        if (comp is TextComponent) {
            style.getString("textAlign") {
                when (it) {
                    "right" -> comp.style.horizontalAlign = HorizontalAlign.RIGHT
                    "left" -> comp.style.horizontalAlign = HorizontalAlign.LEFT
                    "center" -> comp.style.horizontalAlign = HorizontalAlign.CENTER
                    else -> log(Level.DEBUG) { "Invalid textAlign value: $it" }
                }
            }
            style.getString("textAlignVertical") {
                when (it) {
                    "top" -> comp.style.verticalAlign = VerticalAlign.TOP
                    "middle" -> comp.style.verticalAlign = VerticalAlign.MIDDLE
                    "bottom" -> comp.style.verticalAlign = VerticalAlign.BOTTOM
                    else -> log(Level.DEBUG) { "Invalid textAlignVertical value: $it" }
                }
            }
            style.getFloat("textSize") { comp.textState.textWidth = it }
            style.getColor("color") { comp.style.textColor = it }
            style.getColor("highlightColor") { comp.style.highlightColor = it }
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
    m.addComponent<ProgressBar>()
    m.addComponent<ScrollablePanel>()
    m.addComponent<RadioButton>()
    m.addComponent<ScrollBar>()
    m.addComponent<SelectBox<String>>()
    m.addComponent<SelectBox<String>.SelectBoxScrollablePanel>()
    m.addComponent<SelectBox<String>.SelectBoxElement<String>>()
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