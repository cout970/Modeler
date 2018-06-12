package com.cout970.modeler.gui

import com.cout970.modeler.core.config.ColorPalette.Companion.colorOf
import com.cout970.reactive.dsl.borderless
import com.cout970.reactive.dsl.hide
import com.cout970.reactive.dsl.show
import jdk.nashorn.api.scripting.ScriptObjectMirror
import org.joml.Vector4f
import org.liquidengine.legui.component.*
import org.liquidengine.legui.style.Style
import org.liquidengine.legui.style.border.SimpleLineBorder
import org.liquidengine.legui.theme.*
import java.io.File
import javax.script.ScriptEngineManager

object CSSTheme : Theme(createThemeManager()) {

    private lateinit var style: Map<String, ScriptObjectMirror>

    init {
        loadCss()
    }

    fun loadCss() {
        val engine = ScriptEngineManager().getEngineByExtension("js")
        val obj = engine.eval(File("../src/main/resources/assets/style.js").reader()) as ScriptObjectMirror
        style = obj.keys.map { it to (obj[it] as ScriptObjectMirror) }.toMap()
    }


    fun applyComp(component: Component) {
        val classes = component.metadata["classes"] ?: return
        if (classes is String) {

            classes.split(",").forEach { thisClass ->
                val style = style[thisClass] ?: return@forEach

                style.getString("display") {
                    when (it) {
                        "none" -> component.hide()
                        "block" -> component.also { it.show(); it.style.display = Style.DisplayType.MANUAL }
                        "fex" -> component.also { it.show(); it.style.display = Style.DisplayType.FLEX }
                    }
                }
                style.getColor("backgroundColor") { component.style.background.color = it }
                style.getString("borderStyle") {
                    when (it) {
                        "solid" -> component.style.border = SimpleLineBorder()
                        "none" -> component.borderless()
                    }
                }
                style.getFloat("borderWidth") { (component.style.border as SimpleLineBorder).thickness = it }
                style.getFloat("borderRadius") { component.style.setBorderRadius(it) }
                style.getColor("borderColor") { (component.style.border as SimpleLineBorder).color = it }

                if (component is ScrollBar) {
                    style.getColor("scrollColor") { component.scrollColor = it }
                }
            }
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
        get(clazz) { func((this as String).toFloat()) }
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